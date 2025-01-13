package be.kdg.integration5.statisticscontext.port.out;

import be.kdg.integration5.statisticscontext.domain.Player;
import be.kdg.integration5.statisticscontext.domain.PlayerId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface FindPlayerPort {
    List<Player> findPlayersByIds(List<PlayerId> playerIds);
    List<Player> findAllFetched();
    Page<Player> findAllFetched(Pageable pageable);
}
