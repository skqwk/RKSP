package practice3.files;

import common.RandomUtil;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RxTest {
    public static void main(String[] args) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(9);
        AtomicInteger counter = new AtomicInteger(10);

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
        ConnectableObservable<Integer> generator = Observable
                .defer(() -> {
                    int value = RandomUtil.randomInt(0, 10);
                    System.out.printf("%s Сгенерировано - %s\n", counter.getAndDecrement(), value);
                    return Observable.just(value);
                })
                .doOnNext(num -> {
                    System.out.printf("Кладем в очередь - %s\n", num);
                    queue.put(num);
                })
                .repeatUntil(() -> {
                    System.out.printf("Текущее значение в генераторе - %s\n", counter.get());
                    return counter.get() == 0;
                })
                .subscribeOn(Schedulers.computation())
                .doOnComplete(() -> System.out.println("Генерация завершена"))
                .publish();

        generator.connect();

        ConnectableObservable<Integer> publisher = Observable.defer(() -> {
                    Integer taken = queue.take();
                    return Observable.just(taken);
                })
                .repeatUntil(() -> {
                    System.out.printf("Текущее значение в обработчике очереди - %s\n", counter.get());
                    return counter.get() == 0;
                })
                .doOnComplete(() -> System.out.println("Обработка очереди завершена"))
                .publish();

        publisher.subscribe(num -> {
            if (num % 2 == 0) {
                latch.countDown();
                System.out.printf("Получил чётное - %s\n", num);
            }
        });
        publisher.subscribe(num -> {
            if (num % 2 != 0) {
                latch.countDown();
                System.out.printf("Получил нечётное - %s\n", num);
            }
        });

        publisher.connect();

        latch.await();
    }

    private static void test() {
        ConnectableObservable<Integer> randomDefer = Observable.defer(() -> {
                    int value = RandomUtil.randomInt(0, 10);
                    System.out.printf("Сгенерировано - %s\n", value);
                    return Observable.just(value);
                })
                .repeat(10)
                .publish();

        randomDefer.subscribe(num -> {
            if (num % 2 == 0) {
                System.out.printf("Получил чётное - %s\n", num);
            }
        });
        randomDefer.subscribe(num -> {
            if (num % 2 != 0) {
                System.out.printf("Получил нечётное - %s\n", num);
            }
        });

        randomDefer.connect();
    }
}
