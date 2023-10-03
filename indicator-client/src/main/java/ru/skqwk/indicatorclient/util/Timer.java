package ru.skqwk.indicatorclient.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class Timer {
    public static <T> T withTimer(String name, Callable<T> job) {
        long start = System.nanoTime();
        T result = null;
        try {
            result = job.call();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        long finish = System.nanoTime();
        log.info("Job [{}] took [{}] ms", name, (finish - start) / 1_000_000);
        return result;
    }
}
