package com.bbtest.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StartGameResult(String gameId, int lives, int gold, int score, int turn) {
}
