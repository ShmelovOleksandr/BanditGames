package be.kdg.integration5.statisticscontext.adapter.in.web;

import java.util.Objects;
import java.util.UUID;

public record PlayerDto(
        UUID playerId,
        String playerName,
        int age,
        String gender,
        String country,
        String city,
        double churn,
        double firstMoveWinProbability,
        String playerClass) {
    public PlayerDto {
        Objects.requireNonNull(playerId);
        Objects.requireNonNull(playerName);
        Objects.requireNonNull(country);
        Objects.requireNonNull(city);
        Objects.requireNonNull(playerClass);
    }
}
