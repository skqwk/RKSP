package practice3.indicator;

import io.reactivex.rxjava3.core.Observable;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IndicatorNotifierTest {
    private static final int MAX_VALID_TEMP = 25;
    private static final int MAX_VALID_SMOKE = 70;
    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Observable<Integer> tempSensor = Observable.interval(1, TimeUnit.SECONDS)
                .map(n -> generate(15, 30));

        Observable<Integer> smokeSensor = Observable.interval(1, TimeUnit.SECONDS)
                .map(n -> generate(50, 120));

        Observable.zip(tempSensor, smokeSensor, TempAndSmoke::new)
                .subscribe(IndicatorNotifierTest::notify);

        latch.await();
    }

    public static void notify(TempAndSmoke indicator) {
        int smoke = indicator.getSmoke();
        int temp = indicator.getTemp();

        System.out.printf("Get values - [%s, %s]\n", smoke, temp);

        if (smoke > MAX_VALID_SMOKE && temp > MAX_VALID_TEMP) {
            System.out.println("ALARM");
        }

        if (smoke > MAX_VALID_SMOKE) {
            System.out.printf("Smoke increase - %s\n", smoke);
        }


        if (temp > MAX_VALID_TEMP) {
            System.out.printf("Temp increase - %s\n", temp);
        }
    }

    public static int generate(int min, int max) {
        return RANDOM.nextInt(min, max);
    }
}
