package be.kdg.integration5.statisticscontext.core;

import be.kdg.integration5.statisticscontext.domain.Player;
import be.kdg.integration5.statisticscontext.port.in.GetPlayersUseCase;
import be.kdg.integration5.statisticscontext.port.out.FindPlayerPort;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@Transactional
public class GetPlayerUseCaseImpl implements GetPlayersUseCase {

    private final FindPlayerPort findPlayerPort;

    public GetPlayerUseCaseImpl(FindPlayerPort findPlayerPort) {
        this.findPlayerPort = findPlayerPort;
    }

    @Override
    public Page<Player> getPlayers(Pageable pageable) {
        return findPlayerPort.findAllFetched(pageable);
    }
}
