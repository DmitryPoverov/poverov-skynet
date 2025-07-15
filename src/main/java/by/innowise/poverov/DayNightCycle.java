package by.innowise.poverov;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DayNightCycle implements Runnable {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean isDay = false;
    private int currentDay = 1;
    private boolean isFinished = false;
    private CountDownLatch nightLatch;
    private CountDownLatch dayLatch;


    public boolean getIsFinished() {
        lock.lock();
        try {
            return isFinished;
        } finally {
            lock.unlock();
        }
    }


    public CountDownLatch getDayLatch() {
        lock.lock();
        try {
            return dayLatch;
        } finally {
            lock.unlock();
        }
    }


    public CountDownLatch getNightLatch() {
        lock.lock();
        try {
            return nightLatch;
        } finally {
            lock.unlock();
        }
    }


    public void awaitDayStart() throws InterruptedException {
        lock.lock();
        try {
            while (!isFinished && !isDay) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }


    public void awaitNightStart() throws InterruptedException {
        lock.lock();
        try {
            while (!isFinished && isDay) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void run() {
        while (currentDay <= 5) {
            lock.lock();
            try {
                this.dayLatch =  new CountDownLatch(1);
                this.nightLatch = new CountDownLatch(2);
                isDay = true;
                Logger.log("\nDay " + currentDay);
                condition.signalAll();
            } finally {
                lock.unlock();
            }

            try {
                dayLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            sleep();

            lock.lock();
            try {
                isDay = false;
                Logger.log("Night " + currentDay);
                condition.signalAll();
            } finally {
                lock.unlock();
            }

            try {
                nightLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            sleep();

            currentDay++;
        }

        lock.lock();
        try {
            isFinished = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {
        }
    }
}