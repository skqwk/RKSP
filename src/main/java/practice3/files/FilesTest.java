package practice3.files;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import practice1.common.Sleeper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static common.RandomUtil.chooseRandom;
import static common.RandomUtil.randomInt;

public class FilesTest {

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger amount = new AtomicInteger(10);
        CountDownLatch latch = new CountDownLatch(10);

        BlockingQueue<File> queue = new LinkedBlockingQueue<>(5);
        ConnectableObservable<File> repeatGenerator = createRepeatGenerator(amount, queue);
        repeatGenerator.connect();

        ConnectableObservable<File> publisher = createPublisher(latch, queue);

        // Каждый потребитель задачи будет сбрасывать countDownLatch и после выполнения последней задачи,
        // main разблокируется
        Consumer<File> simpleConsumer = file -> {
            latch.countDown();
            String threadName = Thread.currentThread().getName();
            Sleeper.sleep(file.getSize() * 7);
            System.out.printf("%s: Обработал файл - %s, с типом - %s\n", threadName, file.getNumber(), file.getType().name());
        };

        publisher.subscribe(ifThen(file -> file.getType() == FileType.XML, simpleConsumer)::accept);
        publisher.subscribe(ifThen(file -> file.getType() == FileType.JSON, simpleConsumer)::accept);
        publisher.subscribe(ifThen(file -> file.getType() == FileType.XLS, simpleConsumer)::accept);

        publisher.connect();

        // countDownLatch - для блокировки main, пока не выполнятся все задачи
        latch.await();
    }

    /**
     * Создаем публикатора из очереди. Он будет брать из очереди, пока задачи не закончатся
     */
    private static ConnectableObservable<File> createPublisher(CountDownLatch latch, BlockingQueue<File> queue) {
        return Observable.defer(() -> Observable.just(queue.take()))
                .repeatUntil(() -> latch.getCount() == 0)
                .doOnComplete(() -> System.out.println("Получение файлов из очереди завершено"))
                .publish();
    }

    /**
     * Создаем генератор файлов в очередь. Он будет класть в очередь, пока не будет сгенерировано кол-во файлов,
     * ограниченное {@code amount}
     */
    private static ConnectableObservable<File> createRepeatGenerator(AtomicInteger amount, BlockingQueue<File> queue) {
        return Observable
                .defer(() -> fileGenerator(amount))
                .doOnNext(file -> {
                    queue.put(file);
                    System.out.printf("Файл %s добавлен в очередь\n", file.getNumber());
                })
                .repeatUntil(() -> amount.get() == 0)
                .subscribeOn(Schedulers.computation())
                .doOnComplete(() -> System.out.println("Генерация файлов завершена"))
                .publish();
    }

    private static Observable<File> fileGenerator(AtomicInteger amount) {
        Sleeper.sleep(randomInt(10, 100));

        int number = amount.getAndDecrement();

        File file = new File(chooseRandom(FileType.values()), randomInt(10, 100), number);
        System.out.printf("Создан файл - %s. Тип - %s\n", file.getNumber(), file.getType().name());

        return Observable.just(file);
    }

    private static <T> Consumer<T> ifThen(Predicate<T> predicate, Consumer<T> consumer) {
        return value -> {
            if (predicate.test(value)) {
                consumer.accept(value);
            }
        };
    }
}
