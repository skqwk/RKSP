package practice1.files.example;

import practice1.common.Sleeper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable {
    private final BlockingQueue<Task> queue;
    private final AtomicInteger amountTasks;
    private final String index;
    private final int total;

    public Consumer(BlockingQueue<Task> queue,
                    AtomicInteger amountTasks, String index) {
        this.queue = queue;
        this.amountTasks = amountTasks;
        this.index = index;
        this.total = amountTasks.get();
    }

    @Override
    public void run() {
        while (amountTasks.get() > 0) {
            try {
                Task task = queue.take();
                Sleeper.sleep(4000);
                int amount = total - amountTasks.decrementAndGet();
                File file = task.getFile();
                try {
                    Files.write(Path.of(file.getPath()), "Processed".getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("%s: Take task - %s. %s/%s\n", index, task.getName(), amount, total);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
