package mavencinema;

import java.util.Objects;

public class Schedule {
    private Movie movie;
    private Hall hall;
    private String time;
    private double price;

    public Schedule(Movie movie, Hall hall, String time, double price) {
        this.movie = movie;
        this.hall = hall;
        this.time = time;
        this.price = price;
    }

    public Movie getMovie() { return movie; }
    public Hall getHall() { return hall; }
    public String getTime() { return time; }
    public double getPrice() { return price; }

    public boolean isEveningSession() {
        return this.time.compareTo("18:00") >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return Double.compare(schedule.price, price) == 0 && 
               Objects.equals(movie, schedule.movie) && 
               Objects.equals(hall, schedule.hall) && 
               Objects.equals(time, schedule.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movie, hall, time, price);
    }

    @Override
    public String toString() {
        return String.format("%s at %s in %s - $%.2f", 
               movie.getTitle(), time, hall.toString(), price);
    }
}
