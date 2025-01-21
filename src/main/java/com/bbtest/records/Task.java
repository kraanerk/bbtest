package com.bbtest.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Task(String adId, String probability, Integer expiresIn, Integer reward, String message, Byte encrypted) {
    public String firstMessageWord() {
        return message().split(" ")[0];
    }
}
