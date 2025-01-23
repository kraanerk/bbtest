package com.bbtest;

import com.bbtest.exceptions.InvalidApplicationArgument;
import com.bbtest.records.GameResult;
import com.bbtest.utils.CallablesExecutor;
import com.bbtest.utils.GameResultsAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

@Component
@Profile("!test")
public class GamesRunner implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(GamesRunner.class);

    static final String NR_OF_GAMES_AT_LEAST_ONE = "Application argument 'nrOfGamesToRun' must be at least 1";
    static final String PARALLELISM_AT_LEAST_ONE = "Application argument 'nrOfGamesToRunInParallel' must be at least 1";

    @Autowired
    private GameController gameController;

    @Value("${nrOfGamesToRun}")
    private int nrOfGamesToRun;

    @Value("${nrOfGamesToRunInParallel}")
    private int nrOfGamesToRunInParallel;

    @Override
    public void run(String... args) throws Exception {
        runGames(nrOfGamesToRun, nrOfGamesToRunInParallel);
    }

    List<GameResult> runGames(int nrOfGamesToRun, int nrOfGamesToRunInParallel) throws Exception {
        if (nrOfGamesToRun < 1) {
            throw new InvalidApplicationArgument(NR_OF_GAMES_AT_LEAST_ONE);
        }
        if (nrOfGamesToRunInParallel < 1) {
            throw new InvalidApplicationArgument(PARALLELISM_AT_LEAST_ONE);
        }

        LOG.info("Games to run: {}, parallelism: {}", nrOfGamesToRun, nrOfGamesToRunInParallel);
        List<Game> games = IntStream.range(0, nrOfGamesToRun).mapToObj(i -> new Game(gameController)).toList();
        List<GameResult> gameResults = CallablesExecutor.run(games, nrOfGamesToRunInParallel);
        GameResultsAnalyzer.analyze(gameResults);
        return gameResults;
    }

}
