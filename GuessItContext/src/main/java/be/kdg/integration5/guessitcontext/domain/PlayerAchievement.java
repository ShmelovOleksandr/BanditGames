package be.kdg.integration5.guessitcontext.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "players_achievements")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerAchievement {

    @EmbeddedId
    private PlayerAchievementId playerAchievementId;

    @OneToMany(mappedBy = )
    private Player player;
}
