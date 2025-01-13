package be.kdg.integration5.statisticscontext.port.in;

import be.kdg.integration5.statisticscontext.domain.Player;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface GetPlayersUseCase {

    Page<Player> getPlayers(Pageable pageable);
}
