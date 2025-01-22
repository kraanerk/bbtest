package com.bbtest.utils;

import com.bbtest.records.GameResult;
import com.bbtest.records.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameResultsAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(GameResultsAnalyzer.class);

    public static void analyze(List<GameResult> gameResults) {
        int nrOfGames = gameResults.size();

        List<GameResult> failedGames =
                gameResults.stream().filter(gr -> gr.exception() != null).toList();

        List<GameResult> finishedGames =
                new ArrayList<>(gameResults.stream().filter(gr -> gr.exception() == null).toList());
        finishedGames.sort(Comparator.comparing(GameResult::score));

        logExceptions(failedGames);

        logGameStatistics(nrOfGames, finishedGames, failedGames);

//        logTaskSuccessFailureRatiosByProbabilityAndType(gameResults);

//        logFailedTasksForGamesWithTheLowestScore(nrOfGames, finishedGames);
    }

    private static void logGameStatistics(int nrOfGames, List<GameResult> finishedGames, List<GameResult> failedGames) {
        long countScoreBelow1000 = finishedGames.stream().filter(gr -> gr.score() < 1000).count();

        int minScore = finishedGames.isEmpty() ? 0 :
                finishedGames.get(0).score();
        int maxScore = finishedGames.isEmpty() ? 0 :
                finishedGames.get(finishedGames.size() - 1).score();
        int avgScore = finishedGames.isEmpty() ? 0 :
                finishedGames.stream().mapToInt(GameResult::score).sum() / finishedGames.size();

        LOG.info("Nr of games: {}", nrOfGames);
        LOG.info("Nr of failed games: {}", failedGames.size());
        LOG.info("Nr of games having score <1000: {}", countScoreBelow1000);
        LOG.info("Min score: {}", minScore);
        LOG.info("Max score: {}", maxScore);
        LOG.info("Average score: {}", avgScore);
    }

    private static void logTaskSuccessFailureRatiosByProbabilityAndType(List<GameResult> gameResults) {
        Set<String> taskProbabilityTypes = new HashSet<>();
        Map<String, Integer> taskProbabilityTypeSolves = new HashMap<>();
        Map<String, Integer> taskProbabilityTypeFailures = new HashMap<>();

        for (GameResult gameResult : gameResults) {
            gameResult.succeededTasks().forEach(t -> {
                String key = "%s-%s".formatted(t.probability(), t.firstMessageWord());
                taskProbabilityTypes.add(key);
                taskProbabilityTypeSolves.compute(key, (k,v) -> v == null ? 1 : v + 1);
            });
            gameResult.failedTasks().forEach(t -> {
                String key = "%s-%s".formatted(t.probability(), t.firstMessageWord());
                taskProbabilityTypes.add(key);
                taskProbabilityTypeFailures.compute(key, (k,v) -> v == null ? 1 : v + 1);
            });
        }

        Map<String, Double> ratiosMap = new HashMap<>();
        taskProbabilityTypes.forEach( key -> {
            Double ratio = taskProbabilityTypeSolves.getOrDefault(key, 0).doubleValue() /
                    Math.max(taskProbabilityTypeFailures.getOrDefault(key, 1), 1);
            ratiosMap.put(key, ratio);
        });

        List<Map.Entry<String, Double>> ratiosList = new ArrayList<>(ratiosMap.entrySet());
        ratiosList.sort(Map.Entry.comparingByValue());
        Collections.reverse(ratiosList);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> ratio: ratiosList) {
            String key = ratio.getKey();
            Double value = ratio.getValue();
            sb.append("\nsuccessFailureRatios.put(\"%s\", %.2f);".formatted(key, value));
        }
        LOG.info("Task success/failure ratios: {}", sb);
    }

    private static void logExceptions(List<GameResult> failedGames) {
        for (GameResult failedGame: failedGames) {
            Throwable t = failedGame.exception();
            LOG.info("Game {} encountered an exception: {} {}", failedGame.gameId(), t.getClass().getCanonicalName(), t.getMessage());
        }
    }

    private static void logFailedTasksForGamesWithTheLowestScore(int nrOfGames, List<GameResult> finishedGames) {
        if (nrOfGames >= 100) {
            LOG.info("Failed tasks for finished games with the lowest score:");
            for (GameResult gr: finishedGames.subList(0, 10)) {
                LOG.info("{}", gr);
                for (Task t: gr.failedTasks()) {
                    LOG.info("{}", t);
                }
            }
        }
    }
}
