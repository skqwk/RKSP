package practice1.files.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Stand {
    private static final int AMOUNT = 10;

    public static void main(String[] args) {
        BlockingQueue<Task> queue = new LinkedBlockingQueue<>(5);

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        Producer producer = new Producer();

        executorService.submit(() -> {
            for (int i = 0; i < AMOUNT; i++) {
                Task task = producer.produce();
                try {
                    queue.put(task);
                    System.out.printf("Put task[%s] - %s. Size = %s\n", i + 1, task.getName(), queue.size());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        AtomicInteger amountTasks = new AtomicInteger(AMOUNT);
        executorService.submit(new Consumer(queue, amountTasks, "1"));
        executorService.submit(new Consumer(queue, amountTasks, "2"));
    }
}
