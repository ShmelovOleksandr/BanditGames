package be.kdg.integration5.gameplatformcontext.adapter.out.persistence.game;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameJpaEntity {


    // comment
    @Id
    @Column(nullable = false, unique = true, updatable = false)
    private UUID gameId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double priceAmount;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private int maxLobbyPlayerAmount;

    @Column(nullable = false)
    private String frontendUrl;

    @Column(nullable = false)
    private String backendUrl;

    @Column(nullable = false)
    private String gameImageUrl;




}
