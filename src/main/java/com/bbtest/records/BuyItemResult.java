package com.bbtest.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BuyItemResult(boolean shoppingSuccess, int gold, int lives, int turn) {
}
