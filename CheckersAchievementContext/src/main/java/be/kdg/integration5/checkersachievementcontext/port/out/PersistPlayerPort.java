package be.kdg.integration5.checkersachievementcontext.port.out;

import be.kdg.integration5.checkersachievementcontext.domain.Player;

import java.util.List;

public interface PersistPlayerPort {
    List<Player> saveAll(List<Player> players);
}
