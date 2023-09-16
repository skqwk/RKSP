package practice1.min.impl;

import practice1.min.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ThreadFinder implements Finder {
    private static final int AMOUNT_THREADS = 70;
    private final int[] nums;

    public ThreadFinder(int[] nums) {
        this.nums = nums;
    }

    @Override
    public int min() {
        List<Callable<Integer>> tasks = createTasks();
        ExecutorService executorService = Executors.newFixedThreadPool(AMOUNT_THREADS);
        try {
            List<Future<Integer>> results = executorService.invokeAll(tasks);
            int[] converted = new int[AMOUNT_THREADS];
            for (int i = 0; i < results.size(); i++) {
                converted[i] = results.get(i).get();
            }

            executorService.shutdown();

            return Arrays.stream(converted).min()
                    .orElseThrow(() -> new RuntimeException("Not found!"));

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Callable<Integer>> createTasks() {
        int length = nums.length;
        List<Callable<Integer>> tasks = new ArrayList<>();
        int step = length / (AMOUNT_THREADS - 1);

        for (int i = 0; i < AMOUNT_THREADS; i++) {
            int first = i * step;
            int last = (i + 1) * step;
            int lastRounded = last - (last / length) * (last % length);
            tasks.add(() -> new SyncFinder(nums, first, lastRounded).min());
        }

        return tasks;
    }
}
