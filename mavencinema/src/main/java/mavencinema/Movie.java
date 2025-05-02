package mavencinema;

import java.util.Objects;

public class Movie {
    private String title;
    private String genre;
    private int duration; // in minutes
    private double rating;

    public Movie(String title, String genre, int duration, double rating) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
    }

    // Getters
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getDuration() { return duration; }
    public double getRating() { return rating; }

    // Business logic method
    public boolean isBlockbuster() {
        return rating >= 8.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return duration == movie.duration && 
               Double.compare(movie.rating, rating) == 0 && 
               Objects.equals(title, movie.title) && 
               Objects.equals(genre, movie.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, genre, duration, rating);
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %d min, %.1f/10)", title, genre, duration, rating);
    }
   
    
}