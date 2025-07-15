package by.innowise.poverov;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Storage {

    private final List<RobotPart> parts = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock(true);

    public void addPartsToStorage(List<RobotPart> newParts) {
        lock.lock();
        try {
            parts.addAll(newParts);
        } finally {
            lock.unlock();
        }
    }

    public List<RobotPart> takePartsToFaction() {
        lock.lock();
        try {
            List<RobotPart> taken = new ArrayList<>();
            int count = Math.min(5, parts.size());
            for (int i = 0; i < count; i++) {
                taken.add(parts.removeFirst());
            }
            return taken;
        } finally {
            lock.unlock();
        }
    }
}
