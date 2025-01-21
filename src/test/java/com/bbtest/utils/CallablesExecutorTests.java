package com.bbtest.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CallablesExecutorTests {

    private final List<Integer> output = new LinkedList<>();
    private final List<Callable<Integer>> callables = Arrays.asList(
            () -> {
                Thread.sleep(1000);
                synchronized (output) {
                    output.add(1);
                }
                return 1;
            },
            () -> {
                synchronized (output) {
                    output.add(2);
                }
                return 2;
            }
    );

    @Test
    void runInSequence() throws Exception {
        CallablesExecutor.run(callables, 1);
        assertArrayEquals(new Integer[]{1, 2}, output.toArray());
    }

    @Test
    void runInParallel() throws Exception {
        CallablesExecutor.run(callables, 2);
        assertArrayEquals(new Integer[]{2, 1}, output.toArray());
    }
}
