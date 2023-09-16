package practice1.reader;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SquareReader {
    private static final int AMOUNT_THREADS = 8;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        ExecutorService executorService = Executors.newFixedThreadPool(AMOUNT_THREADS);

        while (!Objects.equals(input, "stop")) {
            int num = convertToInt(input);

            CompletableFuture.supplyAsync(() -> new SquareTask(num).call(), executorService)
                    .whenComplete((result, ex) -> print(num, result, ex));

            input = scanner.next();
        }
        executorService.shutdown();

    }

    private static void print(int target, int result, Throwable ex) {
        if (ex != null) {
            System.err.println(ex.getMessage());
        }
        System.out.printf("Результат вычисления %s^2 = %s\n", target, result);
    }

    private static int convertToInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new RuntimeException("На вход ожидается целое число, получено - [" + input + "]");
        }
    }
}
