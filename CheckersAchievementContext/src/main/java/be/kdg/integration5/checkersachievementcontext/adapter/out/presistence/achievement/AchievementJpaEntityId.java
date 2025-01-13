package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AchievementJpaEntityId implements Serializable {
    private UUID achievementId;
    private UUID performerId;
}
