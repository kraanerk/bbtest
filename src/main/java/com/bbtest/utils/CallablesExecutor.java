package com.bbtest.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallablesExecutor {

    public static <T> List<T> run(Collection<? extends Callable<T>> tasks, int parallelism) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(parallelism);
        List<Future<T>> resultFutures = executorService.invokeAll(tasks);
        executorService.shutdown();
        List<T> results = new ArrayList<>(resultFutures.size());
        for (Future<T> resultFuture : resultFutures) {
            results.add(resultFuture.get());
        }
        return results;
    }

}
