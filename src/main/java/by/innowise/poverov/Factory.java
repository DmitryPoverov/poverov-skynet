package by.innowise.poverov;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Factory implements Runnable {

    private static final int TEN_PARTS = 10;
    private final Storage storage;
    private final DayNightCycle dayNightCycle;


    public Factory(Storage storage, DayNightCycle dayNightCycle) {
        this.storage = storage;
        this.dayNightCycle = dayNightCycle;
    }


    @Override
    public void run() {
        Random random = new Random();
        try {
            while (true) {
                dayNightCycle.awaitDayStart();

                if (dayNightCycle.getIsFinished()) {
                    break;
                }

                List<RobotPart> parts = new ArrayList<>();

                for (int i=0; i<TEN_PARTS; i++) {
                    parts.add(RobotPart.values()[random.nextInt(RobotPart.values().length)]);
                }
                storage.addPartsToStorage(parts);
                Logger.logF("[Factory] Produced: %s\n", parts);

                CountDownLatch dayLatch = dayNightCycle.getDayLatch();
                dayLatch.countDown();

                dayNightCycle.awaitNightStart();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
