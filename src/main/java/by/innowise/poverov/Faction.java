package by.innowise.poverov;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public abstract class Faction implements Runnable {
    protected final Storage storage;
    protected final DayNightCycle dayNightCycle;
    protected final Map<RobotPart, Integer> innerPartStorage = new EnumMap<>(RobotPart.class);
    protected int completedRobotsCount = 0;


    public Faction(Storage storage, DayNightCycle dayNightCycle) {
        this.storage = storage;
        this.dayNightCycle = dayNightCycle;
        for (RobotPart part : RobotPart.values()) {
            innerPartStorage.put(part, 0);
        }
    }


    protected void tryAssembleRobots() {
        int minCount = Collections.min(innerPartStorage.values());
        if (minCount > 0) {
            completedRobotsCount += minCount;
            for (RobotPart part : RobotPart.values()) {
                innerPartStorage.put(part, innerPartStorage.get(part) - minCount);
            }
        }
    }

    public int getRobotCount() {
        return completedRobotsCount;
    }

    @Override
    public void run() {
        try {
            while (!dayNightCycle.getIsFinished()) {
                dayNightCycle.awaitNightStart();

                List<RobotPart> partsFromStorage = storage.takePartsToFaction();
                for (RobotPart part : partsFromStorage) {
                    innerPartStorage.put(part, innerPartStorage.get(part) + 1);
                }
                System.out.printf("[%s] Received: %s%n", getClass().getSimpleName(), partsFromStorage);
                tryAssembleRobots();

                CountDownLatch nightLatch = dayNightCycle.getNightLatch();
                nightLatch.countDown();

                dayNightCycle.awaitDayStart();
            }
            tryAssembleRobots();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
