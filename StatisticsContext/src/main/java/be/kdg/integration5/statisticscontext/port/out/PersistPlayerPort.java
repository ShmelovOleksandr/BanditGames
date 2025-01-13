package be.kdg.integration5.statisticscontext.port.out;

import be.kdg.integration5.statisticscontext.domain.Player;

import java.util.List;

public interface PersistPlayerPort {
    Player save(Player player);
    List<Player> saveAll(List<Player> players);
    void updateAll(List<Player> players);
}
