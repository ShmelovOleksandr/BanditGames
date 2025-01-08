package be.kdg.integration5.guessitcontext.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Id
    private UUID
}
