package be.kdg.integration5.statisticscontext.adapter.in.web;


import be.kdg.integration5.statisticscontext.domain.Player;
import be.kdg.integration5.statisticscontext.port.in.CollectPlayerStatisticsCsvUseCase;
import be.kdg.integration5.statisticscontext.port.in.GeneratePlayerDataUseCase;
import be.kdg.integration5.statisticscontext.port.in.GetPlayersUseCase;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Pageable;
import java.io.OutputStream;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final CollectPlayerStatisticsCsvUseCase collectPlayerStatisticsCsvUseCase;
    private final GetPlayersUseCase getPlayersUseCase;
    private final GeneratePlayerDataUseCase generatePlayerDataUseCase;

    public PlayerController(CollectPlayerStatisticsCsvUseCase collectPlayerStatisticsCsvUseCase, GetPlayersUseCase getPlayersUseCase, GeneratePlayerDataUseCase generatePlayerDataUseCase) {
        this.collectPlayerStatisticsCsvUseCase = collectPlayerStatisticsCsvUseCase;
        this.getPlayersUseCase = getPlayersUseCase;
        this.generatePlayerDataUseCase = generatePlayerDataUseCase;
    }

    @GetMapping("/csv")
    @PreAuthorize("hasRole('admin')")
    public void exportSessionStatisticsCsv(HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"games_statistics.csv\"");

        try (OutputStream outputStream = response.getOutputStream()) {
            collectPlayerStatisticsCsvUseCase.exportPlayerData(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export session statistics CSV", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> generateData() {
        int createdPlayersCount = generatePlayerDataUseCase.generatePlayerDataFromCsv();
        return ResponseEntity.status(201).body(createdPlayersCount + " players successfully created.");
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Page<PlayerDto>> getPlayers(Pageable pageable) {
        Page<Player> players = getPlayersUseCase.getPlayers(pageable);

        if (players.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Page<PlayerDto> playersDTO = players.map(p ->
                        new PlayerDto(
                            p.getPlayerId().uuid(),
                            p.getPlayerName(),
                            p.getAge(),
                            p.getGender().name(),
                            p.getLocation().country(),
                            p.getLocation().city(),
                            p.getPredictions().getChurn(),
                            p.getPredictions().getFirstMoveWinProbability(),
                            p.getPredictions().getPlayerClass().name()
                        ));
        return ResponseEntity.ok(playersDTO);
    }
}
