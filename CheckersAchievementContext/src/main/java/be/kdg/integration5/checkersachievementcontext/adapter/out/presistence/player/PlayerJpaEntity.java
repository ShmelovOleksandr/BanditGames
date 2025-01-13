package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player;

import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.AchievementJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerJpaEntity {
    @Id
    @Column(name = "player_id", nullable = false, unique = true, updatable = false)
    private UUID playerId;

    @OneToMany(mappedBy = "performer")
    private Set<AchievementJpaEntity> achievements;

    public PlayerJpaEntity(UUID playerId) {
        this.playerId = playerId;
    }
}
