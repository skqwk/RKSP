package ru.skqwk.indicatorclient.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomUtil {
    private static final Random RANDOM = new Random();

    public static int randomInt() {
        return RANDOM.nextInt();
    }

    public static int randomInt(int left, int right) {
        return RANDOM.nextInt(left, right);
    }

    public static int randomInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    public static <T> T chooseRandom(List<T> values) {
        int size = values.size();

        return values.get(randomInt(size));
    }

    public static <T> T chooseRandom(T[] values) {
        return chooseRandom(Arrays.asList(values));
    }
}
