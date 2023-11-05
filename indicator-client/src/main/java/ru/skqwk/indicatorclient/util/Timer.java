package ru.skqwk.indicatorclient.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Timer {
    public static void withTimer(String name, Runnable job) {
        long start = System.nanoTime();
        try {
            job.run();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        long finish = System.nanoTime();
        log.info("Job [{}] took [{}] ms", name, (finish - start) / 1_000_000);
    }
}
