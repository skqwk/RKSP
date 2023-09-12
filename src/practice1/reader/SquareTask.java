package practice1.reader;

import practice1.common.Sleeper;

import java.util.Random;
import java.util.concurrent.Callable;

public class SquareTask implements Callable<Integer> {
    private static final Random random = new Random();

    private final int num;

    public SquareTask(int num) {
        this.num = num;
    }

    @Override
    public Integer call() {
        Sleeper.sleep(random.nextInt(1000, 5000));

        return num * num;
    }
}
