package by.innowise.poverov;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Storage storage = new Storage();
        DayNightCycle dayNightCycle = new DayNightCycle();
        World worldFaction = new World(storage, dayNightCycle);
        Wednesday wednesdayFaction = new Wednesday(storage, dayNightCycle);
        Factory factory = new Factory(storage, dayNightCycle);

        Thread dayNightManagerThread = new Thread(dayNightCycle);
        Thread factoryThread = new Thread(factory);
        Thread worldThread = new Thread(worldFaction);
        Thread wednesdayThread = new Thread(wednesdayFaction);

        factoryThread.start();
        dayNightManagerThread.start();
        Thread.sleep(50);
        worldThread.start();
        wednesdayThread.start();

        dayNightManagerThread.join();
        factoryThread.join();
        worldThread.join();
        wednesdayThread.join();

        System.out.println("\n=== FINAL RESULT ===");
        System.out.printf("World: %d robots%n", worldFaction.getRobotCount());
        System.out.printf("Wednesday: %d robots%n", wednesdayFaction.getRobotCount());

        if (worldFaction.getRobotCount() > wednesdayFaction.getRobotCount()) {
            System.out.println("Winner: World");
        } else if (worldFaction.getRobotCount() < wednesdayFaction.getRobotCount()) {
            System.out.println("Winner: Wednesday");
        } else {
            System.out.println("It's a tie!");
        }
    }
}
