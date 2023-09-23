package practice3.mapreduce;

import io.reactivex.rxjava3.core.Emitter;
import io.reactivex.rxjava3.core.Observable;

import java.util.Random;

public class RandomMapReduceTest {
    private static final int THOUSAND = 1000;

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        // 2.1.3
        // countRandom();

        // 2.2.3
        // parallelProcess();

        // 2.3.3
        // latest();
    }

    /**
     * 2.1.3
     * Преобразовать поток из случайного количества (от 0 до 1000)
     * случайных чисел в поток, содержащий количество чисел.
     */
    public static void countRandom() {
        Observable.generate(() -> 0,
                        (s, e) -> {return randomGenerator(e,"Generate");})
                .take(RANDOM.nextInt(THOUSAND))
                .count()
                .subscribe(System.out::println);
    }

    /**
     * 2.2.3
     * Даны два потока по 1000 элементов. Каждый содержит случайные цифры. Сформировать поток, обрабатывающий оба потока параллельно.
     * Например, при входных потоках (1, 2, 3) и (4, 5, 6) выходной поток — (1, 4, 2, 5, 3, 6)
     */
    public static void parallelProcess() {
        Observable<Object> first = Observable.generate(() -> 0,
                        (s, e) -> {return randomGenerator(e,"#1 Generate");})
                .take(THOUSAND);

        Observable<Object> second = Observable.generate(() -> 0,
                        (s, e) -> {return randomGenerator(e,"#2 Generate");})
                .take(THOUSAND);

        Observable.merge(first, second)
                .subscribe(val -> System.out.printf("Take %s\n", val));
    }

    /**
     * 2.3.3
     * Дан поток из случайного количества случайных чисел.
     * Сформировать поток, содержащий только последнее число.
     */
    public static void latest() {
        Observable.generate(() -> 0,
                        (s, e) -> {return randomGenerator(e,"Generate");})
                .take(RANDOM.nextInt(THOUSAND))
                .lastElement()
                .subscribe(last -> System.out.printf("Last element - %s\n", last));
    }

    public static Integer randomGenerator(Emitter<Object> emitter,
                                          String message) {
        int nextValue = RANDOM.nextInt();
        System.out.printf("%s %s\n", message, nextValue);
        emitter.onNext(nextValue);
        return nextValue;
    }
}
