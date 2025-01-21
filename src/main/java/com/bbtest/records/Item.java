package com.bbtest.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Item(String id, String name, Integer cost) {
}
