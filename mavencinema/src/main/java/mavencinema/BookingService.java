package mavencinema;

import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

public class BookingService {
	public List<Movie> getMovies() {
	    return Collections.unmodifiableList(movies); 
	}

	public List<Hall> getHalls() {
	    return Collections.unmodifiableList(halls);
	}

	public List<Schedule> getSchedules() {
	    return Collections.unmodifiableList(schedules);
	}

    private List<Ticket> tickets = new ArrayList<>();
    private List<Movie> movies = new ArrayList<>();
    private List<Hall> halls = new ArrayList<>();
    private List<Schedule> schedules = new ArrayList<>();

    public void addMovie(Movie movie) {
        if (movie == null) throw new IllegalArgumentException("Movie cannot be null");
        movies.add(movie);
    }

    public List<Movie> getMoviesByGenre(String genre) {
        List<Movie> result = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.getGenre().equalsIgnoreCase(genre)) {
                result.add(movie);
            }
        }
        return result;
    }

    public void addHall(Hall hall) {
        if (hall == null) throw new IllegalArgumentException("Hall cannot be null");
        halls.add(hall);
    }

    public Hall getHallByNumber(int hallNumber) {
        for (Hall hall : halls) {
            if (hall.getHallNumber() == hallNumber) {
                return hall;
            }
        }
        return null;
    }

    public void addSchedule(Schedule schedule) {
        if (schedule == null) throw new IllegalArgumentException("Schedule cannot be null");
        schedules.add(schedule);
    }

    public List<Schedule> getSchedulesForMovie(String movieTitle) {
        List<Schedule> result = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (schedule.getMovie().getTitle().equalsIgnoreCase(movieTitle)) {
                result.add(schedule);
            }
        }
        return result;
    }

    public Ticket bookTicket(Schedule schedule, int seatNumber, boolean isVipSeat) {
        // Перевірка вхідних параметрів
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule cannot be null");
        }
        
        if (seatNumber <= 0 || seatNumber > schedule.getHall().getCapacity()) {
            throw new IllegalArgumentException("Invalid seat number");
        }

        boolean seatTaken = false;
        for (Ticket ticket : tickets) {
            if (ticket.getSchedule().equals(schedule) && ticket.getSeatNumber() == seatNumber) {
                seatTaken = true;
                break;
            }
        }
        
        if (seatTaken) {
            throw new IllegalStateException("Seat " + seatNumber + " is already booked");
        }

        Ticket newTicket = new Ticket(schedule, seatNumber, isVipSeat);
        tickets.add(newTicket);
       
        return newTicket;
    }

    
    public List<Ticket> getTicketsForSchedule(Schedule schedule) {
        List<Ticket> result = new ArrayList<>();
        
        for (Ticket ticket : tickets) {
            if (ticket.getSchedule().equals(schedule)) {
                result.add(ticket);
            }
        }
        
        Collections.sort(result, new Comparator<Ticket>() {
            @Override
            public int compare(Ticket t1, Ticket t2) {
                return Integer.compare(t1.getSeatNumber(), t2.getSeatNumber());
            }
        });
        
        return result;
    }

    public List<Integer> getAvailableSeats(Schedule schedule) {
        List<Integer> takenSeats = new ArrayList<>();
        
        for (Ticket ticket : tickets) {
            if (ticket.getSchedule().equals(schedule)) {
                takenSeats.add(ticket.getSeatNumber());
            }
        }
        
        List<Integer> availableSeats = new ArrayList<>();
        int capacity = schedule.getHall().getCapacity();
        
        for (int i = 1; i <= capacity; i++) {
            if (!takenSeats.contains(i)) {
                availableSeats.add(i);
            }
        }
        
        return availableSeats;
    }
        public void exportTicketsToFile(String filename, Comparator<Ticket> comparator) {
            try (PrintWriter writer = new PrintWriter(new File(filename))) {
                writer.println("Movie,Time,Hall,SeatNumber,IsVIP,Price");
                
                tickets.stream()
                    .sorted(comparator)
                    .forEach(ticket -> {
                        Schedule s = ticket.getSchedule();
                        writer.println(String.format("%s,%s,%d,%d,%b,%.2f",
                            s.getMovie().getTitle(),
                            s.getTime(),
                            s.getHall().getHallNumber(),
                            ticket.getSeatNumber(),
                            ticket.isVipSeat(),
                            ticket.calculateFinalPrice()));
                    });
                
                System.out.println("Data successfully exported to " + filename);
            } catch (FileNotFoundException e) {
                System.err.println("Export error: " + e.getMessage());
            }
        }

        public void importTicketsFromFile(String filename) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                // Skip header
                reader.readLine();
                
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        String movieTitle = parts[0];
                        String time = parts[1];
                        int hallNumber = Integer.parseInt(parts[2]);
                        int seatNumber = Integer.parseInt(parts[3]);
                        boolean isVip = Boolean.parseBoolean(parts[4]);
                        
                        Optional<Schedule> schedule = schedules.stream()
                            .filter(s -> s.getMovie().getTitle().equals(movieTitle) 
                                    && s.getTime().equals(time)
                                    && s.getHall().getHallNumber() == hallNumber)
                            .findFirst();
                        
                        if (schedule.isPresent()) {
                            try {
                                bookTicket(schedule.get(), seatNumber, isVip);
                            } catch (Exception e) {
                                System.err.println("Skipping ticket - " + e.getMessage());
                            }
                        }
                    }
                }
                System.out.println("Data successfully imported from " + filename);
            } catch (IOException e) {
                System.err.println("Import error: " + e.getMessage());
            }
    }

}
