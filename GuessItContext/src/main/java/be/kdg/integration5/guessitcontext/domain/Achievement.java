package be.kdg.integration5.guessitcontext.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "achievements")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Achievement {

    @Id
    private UUID achievementId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

}
