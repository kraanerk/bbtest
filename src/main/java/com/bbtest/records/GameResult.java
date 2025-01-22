package com.bbtest.records;

import java.util.List;

public record GameResult(int gameNr, String gameId, int score, int turn, int lives, int gold, List<Task> failedTasks, List<Task> succeededTasks, Throwable exception) {
}
