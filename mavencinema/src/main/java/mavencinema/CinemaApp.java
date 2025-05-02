package mavencinema;

import java.util.Scanner;
import java.util.Comparator;
import java.util.List;

public class CinemaApp {
    private BookingService bookingService;
    private Scanner scanner;

    public CinemaApp() {
        this.bookingService = new BookingService();
        this.scanner = new Scanner(System.in);
        initializeSampleData();
    }

    public static void main(String[] args) {
        CinemaApp app = new CinemaApp();
        app.run();
    }

    public void run() {
        while (true) {
            showMainMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1: bookTicket(); break;
                case 2: viewAllMovies(); break;
                case 3: exportTickets(); break;
                case 4: importTickets(); break;
                case 5: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private void initializeSampleData() {
        Movie movie1 = new Movie("Inception", "Sci-Fi", 148, 8.8);
        Movie movie2 = new Movie("The Shawshank Redemption", "Drama", 142, 9.3);
        
        Hall hall1 = new Hall(1, 100);
        Hall hall2 = new Hall(2, 80);
        
        bookingService.addMovie(movie1);
        bookingService.addMovie(movie2);
        bookingService.addHall(hall1);
        bookingService.addHall(hall2);
        
        Schedule schedule1 = new Schedule(movie1, hall1, "18:00", 12.50);
        Schedule schedule2 = new Schedule(movie2, hall2, "20:30", 10.00);
        
        bookingService.addSchedule(schedule1);
        bookingService.addSchedule(schedule2);
        

        bookingService.bookTicket( schedule1, 5, false);
        bookingService.bookTicket( schedule2, 10, true);
        bookingService.bookTicket( schedule1, 15, false);
    }

    private void showMainMenu() {
        System.out.println("\n=== Cinema Booking System ===");
        System.out.println("1. Book Ticket");
        System.out.println("2. View All Movies");
        System.out.println("3. Export Tickets to CSV");
        System.out.println("4. Import Tickets from CSV");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }
    private void bookTicket() {
        System.out.println("Available schedules:");
        for (Schedule schedule : bookingService.getSchedules()) {
            System.out.println("- " + schedule);
        }
        System.out.print("Enter movie title: ");
        String title = scanner.nextLine();
        System.out.print("Enter show time: ");
        String time = scanner.nextLine();
        
        Schedule foundSchedule = null;
        for (Schedule s : bookingService.getSchedules()) {
            if (s.getMovie().getTitle().equalsIgnoreCase(title) && s.getTime().equals(time)) {
                foundSchedule = s;
                break; // зупиняємо цикл, якщо знайшли
            }
        }

        if (foundSchedule == null) {
            System.out.println("Schedule not found!");
            return;
        }
        System.out.println("\nAvailable schedules:");
        for (Schedule schedule : bookingService.getSchedules()) {
            System.out.println("- " + schedule);
        }
        
        List<Integer> availableSeats = bookingService.getAvailableSeats(foundSchedule);
        System.out.println("\nAvailable seats for " + title + " at " + time + ":");
        System.out.println("Total capacity: " + foundSchedule.getHall().getCapacity());
        System.out.println("Available seats (" + availableSeats.size() + "):");
        
        int seatsPerRow = 10;
        for (int i = 0; i < availableSeats.size(); i++) {
            System.out.printf("%3d ", availableSeats.get(i));
            if ((i + 1) % seatsPerRow == 0) {
                System.out.println();
            }
        }
        System.out.println();
        System.out.print("Enter seat number: ");
        int seatNumber = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Is VIP seat? (true/false): ");
        boolean isVip = scanner.nextBoolean();
        scanner.nextLine(); // consume newline
        if (foundSchedule.isEveningSession()) {
            System.out.println("🌃 It`s evening session!");
        }

        try {
            Ticket ticket = bookingService.bookTicket(foundSchedule, seatNumber, isVip);
            System.out.println("Ticket booked: " + ticket);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void viewAllMovies() {
        System.out.println("All Movies:");
        for (Movie m : bookingService.getMovies()) {
            System.out.println("- " + m.getTitle() + " (" + m.getGenre() + ")");
            System.out.println("  Duration: " + m.getDuration() + " min, Rating: " + m.getRating());
            System.out.println("  Schedules:");
            
            for (Schedule s : bookingService.getSchedulesForMovie(m.getTitle())) {
                System.out.println("  * " + s.getTime() + " in Hall " + s.getHall().getHallNumber());
            }
        }
    }
    private void exportTickets() {
        System.out.println("\n--- Export Tickets ---");
        
        System.out.println("Choose sorting:");
        System.out.println("1. By movie title");
        System.out.println("2. By show time");
        System.out.println("3. By seat number");
        System.out.println("4. By price (low to high)");
        System.out.print("Your choice: ");
        int sortChoice = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter filename (e.g., tickets.csv): ");
        String filename = scanner.nextLine();

        Comparator<Ticket> comparator;
        switch (sortChoice) {
            case 1:
                comparator = Comparator.comparing(t -> t.getSchedule().getMovie().getTitle());
                break;
            case 2:
                comparator = Comparator.comparing(t -> t.getSchedule().getTime());
                break;
            case 3:
                comparator = Comparator.comparingInt(Ticket::getSeatNumber);
                break;
            case 4:
                comparator = Comparator.comparingDouble(Ticket::calculateFinalPrice);
                break;
            default:
                System.out.println("Invalid choice, using default sorting by movie title");
                comparator = Comparator.comparing(t -> t.getSchedule().getMovie().getTitle());
        }

        bookingService.exportTicketsToFile(filename, comparator);
    }

    private void importTickets() {
        System.out.print("\nEnter filename to import from (e.g., tickets.csv): ");
        String filename = scanner.nextLine();
        
        bookingService.importTicketsFromFile(filename);
    }

}