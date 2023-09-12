package practice1.min;

import practice1.min.impl.forkjoin.ForkJoinFinder;
import practice1.min.impl.SyncFinder;
import practice1.min.impl.ThreadFinder;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

public class FinderTest {
    private static final long SIZE = 10_000;

    public static void main(String[] args) {
        test((nums) -> new SyncFinder(nums).min(), "One thread");

        test((nums) -> new ThreadFinder(nums).min(), "Many thread");

        test((nums) -> new ForkJoinFinder(nums).min(), "Fork join");
    }

    public static void test(Function<int[], Integer> test, String name) {
        long start = System.nanoTime();
        int[] testNums = generate();
        Integer actual = test.apply(testNums);
        long finish = System.nanoTime();

        System.out.printf("%s: took %s ms\n", name, (finish - start) / 1_000_000);
        int expected = dumb(testNums);
        if (expected != actual) {
            throw new RuntimeException("Result should be - " + expected + ", but was - " + actual);
        }
    }

    /**
     * Обычный алгоритм поиска минимума для гарантии корректности остальных алгоритмов
     */
    public static int dumb(int[] nums) {
        return Arrays.stream(nums).min().orElseThrow(RuntimeException::new);
    }

    public static int[] generate() {
        Random random = new Random();
        return IntStream.generate(random::nextInt)
                .limit(SIZE)
                .toArray();
    }
}
