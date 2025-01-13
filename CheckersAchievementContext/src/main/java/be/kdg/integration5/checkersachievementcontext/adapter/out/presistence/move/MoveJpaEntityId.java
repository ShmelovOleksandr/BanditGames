package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.move;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MoveJpaEntityId implements Serializable {
    private UUID gameId;
    private UUID moverId;
    private LocalDateTime movedAt;
}
