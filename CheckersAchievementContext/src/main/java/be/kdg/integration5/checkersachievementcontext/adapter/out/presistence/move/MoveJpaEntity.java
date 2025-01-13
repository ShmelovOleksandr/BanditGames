package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.move;

import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.game.GameJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player.PlayerJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "moves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoveJpaEntity {
    @EmbeddedId
    private MoveJpaEntityId moveId;

    @ManyToOne
    @JoinColumn(name = "game_id", referencedColumnName = "game_id")
    @MapsId("gameId")
    private GameJpaEntity game;

    @ManyToOne
    @JoinColumn(name = "mover_id", referencedColumnName = "player_id")
    @MapsId("moverId")
    private PlayerJpaEntity mover;

    @Column(nullable = false)
    private int oldX;

    @Column(nullable = false)
    private int oldY;

    @Column(nullable = false)
    private int newX;

    @Column(nullable = false)
    private int newY;

}
