package practice1.min.impl.forkjoin;

import practice1.min.impl.SyncFinder;
import practice1.common.Validator;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

public class ForkJoinFindTask extends RecursiveTask<Integer> {
    private static final int SIZE = 3;

    private final int[] nums;
    private final int first;
    private final int last;

    public ForkJoinFindTask(int[] nums,
                            int first,
                            int last) {
        Validator.validate(nums, first, last);
        this.nums = nums;
        this.first = first;
        this.last = last;
    }

    public ForkJoinFindTask(int[] nums) {
        this.nums = nums;
        this.first = 0;
        this.last = nums.length;
    }

    @Override
    protected Integer compute() {
        if ((last - first) < SIZE) {
            return new SyncFinder(nums, first, last).min();
        }

        int lastWithOffset = first + ((last - first) / 2) + 1;
        ForkJoinTask<Integer> firstFork = new ForkJoinFindTask(nums, first, lastWithOffset).fork();
        ForkJoinTask<Integer> secondFork = new ForkJoinFindTask(nums, lastWithOffset, last).fork();

        return Integer.min(firstFork.join(), secondFork.join());
    }

    public static void main(String[] args) {
        int first = 0;
        int last = 50;
        int[] nums = IntStream.range(0, 50).toArray();

        divide(nums, first, last);
    }

    public static void divide(int[] nums, int first, int last) {
        if (last - first < 4) {
            int min = new SyncFinder(nums, first, last).min();
            System.out.printf("[%s - %s) - %s\n", first, last, min);
            return;
        }
        int lastWithOffset = first + ((last - first) / 2) + 1;
        divide(nums, first, lastWithOffset);
        divide(nums, lastWithOffset, last);
    }
}

