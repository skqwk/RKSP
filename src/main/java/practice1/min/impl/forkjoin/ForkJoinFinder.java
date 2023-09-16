package practice1.min.impl.forkjoin;

import practice1.min.Finder;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinFinder implements Finder {
    private final int[] nums;

    public ForkJoinFinder(int[] nums) {
        this.nums = nums;
    }

    @Override
    public int min() {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        return forkJoinPool.invoke(new ForkJoinFindTask(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3}, 1);
        verify(new int[]{-11, 2, 3}, -11);
        verify(new int[]{1, -5, 3, -5}, -5);
        verify(new int[]{1, 2, 3, 3, 2, 1, 1}, 1);
        verify(new int[]{1, 2, 3, -100, 2, 1, 1}, -100);
        verify(new int[]{1, 2, 3, 3, 2, 1, 1}, 1);
    }

    public static void verify(int[] nums, int expected) {
        int actual = new ForkJoinFinder(nums).min();
        if (actual != expected) {
            System.out.printf("Expected = [%s], but actual = [%s] in array - %s\n", expected, actual, Arrays.toString(nums));
        }
    }
}
