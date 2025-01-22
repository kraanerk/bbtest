package com.bbtest.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Item(String id, String name, int cost) {
    public static final String HEALING_POTION = "Healing potion";
}
