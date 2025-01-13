package be.kdg.integration5.gameplatformcontext.adapter.in.api;

import be.kdg.integration5.gameplatformcontext.adapter.in.api.dto.PlayerDTO;
import be.kdg.integration5.gameplatformcontext.adapter.in.api.dto.PlayerRegisterDTO;
import be.kdg.integration5.gameplatformcontext.domain.Player;
import be.kdg.integration5.gameplatformcontext.domain.PlayerId;
import be.kdg.integration5.gameplatformcontext.port.in.GetFriendsListUseCase;
import be.kdg.integration5.gameplatformcontext.port.in.GetPlayersByUserNameUseCase;
import be.kdg.integration5.gameplatformcontext.port.in.RegisterNewPlayerUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final RegisterNewPlayerUseCase registerNewPlayerUseCase;
    private final GetPlayersByUserNameUseCase getPlayersByUserNameUseCase;

    @Autowired
    private final GetFriendsListUseCase getFriendsListUseCase;

    public PlayerController(RegisterNewPlayerUseCase registerNewPlayerUseCase, GetPlayersByUserNameUseCase getPlayersByUserNameUseCase, GetFriendsListUseCase getFriendsListUseCase) {
        this.registerNewPlayerUseCase = registerNewPlayerUseCase;
        this.getPlayersByUserNameUseCase = getPlayersByUserNameUseCase;
        this.getFriendsListUseCase = getFriendsListUseCase;
    }

    @PostMapping
    public ResponseEntity<?> registerNewPlayer(@RequestBody PlayerRegisterDTO playerRegisterDTO) {
        registerNewPlayerUseCase.registerNewPlayer(playerRegisterDTO.token());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('player')")
    public ResponseEntity<List<PlayerDTO>> getPlayersByUsername(@RequestParam("username") String username) {
        List<Player> players = getPlayersByUserNameUseCase.getPlayers(username);
        if (players.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<PlayerDTO> playerDTOS = players
                .stream()
                .map(p -> new PlayerDTO(p.getPlayerId().uuid(), p.getUsername()))
                .toList();
        return ResponseEntity.ok(playerDTOS);
    }

    @GetMapping("/{playerId}/friends")
    public ResponseEntity<List<Player>> getFriends(@PathVariable UUID playerId) {
        List<Player> friends = getFriendsListUseCase.getFriends(new PlayerId(playerId));
        return ResponseEntity.ok(friends);
    }
}
