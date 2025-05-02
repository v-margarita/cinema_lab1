package mavencinema;

import java.util.Objects;

public class Ticket {
    private Schedule schedule;
    private int seatNumber;
    private boolean isVipSeat;

    public Ticket( Schedule schedule, int seatNumber, boolean isVipSeat) {
        this.schedule = schedule;
        this.seatNumber = seatNumber;
        this.isVipSeat = isVipSeat;
    }

    public Schedule getSchedule() { return schedule; }
    public int getSeatNumber() { return seatNumber; }
    public boolean isVipSeat() { return isVipSeat; }

    public double calculateFinalPrice() {
        double basePrice = schedule.getPrice();
        if (isVipSeat) {
            basePrice *= 1.2; // 20% more for VIP seats
        }
        return basePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return seatNumber == ticket.seatNumber && 
               isVipSeat == ticket.isVipSeat && 
               Objects.equals(schedule, ticket.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schedule, seatNumber, isVipSeat);
    }

    @Override
    public String toString() {
        return String.format("Ticket{ movie=%s, time=%s, seat=%d%s, price=$%.2f}", 
               schedule.getMovie().getTitle(), schedule.getTime(), 
               seatNumber, isVipSeat ? " (VIP)" : "", calculateFinalPrice());
    }
}
