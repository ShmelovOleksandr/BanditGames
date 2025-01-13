package be.kdg.integration5.gameplatformcontext.adapter.out.persistence.player;

import be.kdg.integration5.gameplatformcontext.adapter.out.persistence.lobby.LobbyJpaEntity;
import be.kdg.integration5.gameplatformcontext.adapter.out.persistence.lobby_player.LobbyPlayerJpaEntity;
import be.kdg.integration5.gameplatformcontext.domain.Player;
import be.kdg.integration5.gameplatformcontext.domain.PlayerId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerJpaEntity {
    @Id
    @Column(nullable = false, unique = true, updatable = false, name = "player_id")
    private UUID playerId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Player.Gender gender;

    @OneToMany(mappedBy = "lobbyOwner")
    private List<LobbyJpaEntity> ownsLobbies;

    @OneToMany(mappedBy = "player")
    private List<LobbyPlayerJpaEntity> lobbyPlayers;

    @ManyToMany
    @JoinTable(
            name = "players_friends",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<PlayerJpaEntity> friends;

    public PlayerJpaEntity(UUID playerId, int age, Player.Gender gender, String username) {
        this.playerId = playerId;
        this.age = age;
        this.gender = gender;
        this.username = username;
    }

    public PlayerJpaEntity(UUID playerId, String username, int age, Player.Gender gender) {
        this.playerId = playerId;
        this.username = username;
        this.age = age;
        this.gender = gender;
    }

    public PlayerJpaEntity(UUID playerId, int age, Player.Gender gender, String username, Set<PlayerJpaEntity> friends) {
        this.playerId = playerId;
        this.age = age;
        this.gender = gender;
        this.username = username;
        this.friends = friends;
    }

    private Player toSimpleDomain() {
        return new Player(
                new PlayerId(this.playerId),
                this.username,
                this.age,
                this.gender
        );
    }

    public Player toDomain() {
        return new Player(
                new PlayerId(this.playerId),
                this.username,
                this.age,
                this.gender,
                new ArrayList<>(),
                this.friends != null ? new ArrayList<>(this.friends.stream().map(PlayerJpaEntity::toSimpleDomain).toList()) : new ArrayList<>()
        );
    }

    public static PlayerJpaEntity of(Player player) {
        return new PlayerJpaEntity(
                player.getPlayerId().uuid(),
                player.getAge(),
                player.getGender(),
                player.getUsername(),
                player.getFriends() == null ? null : player.getFriends().stream().map(
                        friend -> new PlayerJpaEntity(
                                friend.getPlayerId().uuid(),
                                friend.getUsername(),
                                friend.getAge(),
                                friend.getGender()
                        )
                ).collect(Collectors.toSet())
        );
    }
}
