package com.bbtest.exceptions;

import com.bbtest.records.Task;

import java.util.Arrays;

public class AllTasksUseUnknownEncryptionException extends RuntimeException {

    public AllTasksUseUnknownEncryptionException(Task[] tasks) {
        super(String.join(",", Arrays.stream(tasks).map(Record::toString).toList()));
    }
}
