package be.kdg.integration5.statisticscontext.core;

import be.kdg.integration5.statisticscontext.domain.Player;
import be.kdg.integration5.statisticscontext.port.in.GeneratePlayerDataUseCase;
import be.kdg.integration5.statisticscontext.port.out.GeneratePlayerPort;
import be.kdg.integration5.statisticscontext.port.out.PersistGamePort;
import be.kdg.integration5.statisticscontext.port.out.PersistPlayerPort;
import be.kdg.integration5.statisticscontext.port.out.PersistSessionPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class GeneratePlayerDataUseCaseImpl implements GeneratePlayerDataUseCase {

    private final PersistPlayerPort persistPlayerPort;
    private final GeneratePlayerPort generatePlayerPort;

    public GeneratePlayerDataUseCaseImpl(PersistPlayerPort persistPlayerPort, GeneratePlayerPort generatePlayerPort) {
        this.persistPlayerPort = persistPlayerPort;
        this.generatePlayerPort = generatePlayerPort;
    }

    @Override
    public int generatePlayerDataFromCsv() {
        List<Player> generatedPlayers = generatePlayerPort.readPlayerCsv();
        List<Player> savedPlayers = persistPlayerPort.saveAll(generatedPlayers);
        return savedPlayers.size();
    }
}
