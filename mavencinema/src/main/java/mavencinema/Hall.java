package mavencinema;

public class Hall {
    private int hallNumber;
    private int capacity;

    public Hall(int hallNumber, int capacity) {
        this.hallNumber = hallNumber;
        this.capacity = capacity;
    }

    public int getHallNumber() { return hallNumber; }
    public int getCapacity() { return capacity; }

    @Override
    public String toString() {
        return "Hall " + hallNumber + " (Capacity: " + capacity + ")";
    }
}
