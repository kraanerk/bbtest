package com.bbtest;

import com.bbtest.exceptions.InvalidApplicationArgument;
import com.bbtest.records.GameResult;
import com.bbtest.records.Item;
import com.bbtest.records.StartGameResult;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.bbtest.GamesRunner.NR_OF_GAMES_AT_LEAST_ZERO;
import static com.bbtest.GamesRunner.PARALLELISM_AT_LEAST_ONE;
import static com.bbtest.records.Item.HEALING_POTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class GamesRunnerTests {

    @Mock
    private GameController gameController;

    @InjectMocks
    private GamesRunner gamesRunner;

    @Test
    void runZeroGames() throws Exception {
        List<GameResult> gameResults = gamesRunner.runGames(0, 1);
        assertEquals(0, gameResults.size());
    }

    @Test
    void runTwoGamesInParallel() throws Exception {
        StartGameResult startGameResult = new StartGameResult(
                "gameId", 0, 0, 0, 0
        );
        when(gameController.startGame())
                .thenReturn(startGameResult);
        when(gameController.listItems(startGameResult.gameId()))
                .thenReturn(new Item[] {
                        new Item("hpot", HEALING_POTION, 50)
                });

        List<GameResult> gameResults = gamesRunner.runGames(2, 2);
        assertEquals(2, gameResults.size());
    }

    @Test
    void run_negativeNrOfGames() {
        Throwable t = assertThrows(InvalidApplicationArgument.class,
                () -> gamesRunner.runGames(-1, 1)
        );
        assertEquals(NR_OF_GAMES_AT_LEAST_ZERO, t.getMessage());
    }

    @Test
    void run_zeroGamesInParallel() {
        Throwable t = assertThrows(InvalidApplicationArgument.class,
                () -> gamesRunner.runGames(1, 0)
        );
        assertEquals(PARALLELISM_AT_LEAST_ONE, t.getMessage());
    }
}
