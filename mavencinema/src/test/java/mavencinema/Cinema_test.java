package mavencinema;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class Cinema_test {
    private BookingService bookingService;
    private Movie testMovie;
    private Hall testHall;
    private Schedule testSchedule;
    
    @BeforeEach
    void setUp() {
        bookingService = new BookingService();
        testMovie = new Movie("Inception", "Sci-Fi", 148, 8.8);
        testHall = new Hall(1, 100);
        testSchedule = new Schedule(testMovie, testHall, "18:00", 12.50);
        
        bookingService.addMovie(testMovie);
        bookingService.addHall(testHall);
        bookingService.addSchedule(testSchedule);
    }

    // Тест 1: Успішне бронювання квитка
    @Test
    void bookTicket_Success() {
        assertDoesNotThrow(() -> {
            Ticket ticket = bookingService.bookTicket(testSchedule, 1, false);
            assertNotNull(ticket);
            
            // Перевіряємо через getTicketsForSchedule замість getTickets
            List<Ticket> tickets = bookingService.getTicketsForSchedule(testSchedule);
            assertEquals(1, tickets.size());
        });
    }

    // Тест 2: Спроба бронювання null розкладу
    @Test
    void bookTicket_NullSchedule_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> bookingService.bookTicket(null, 1, false));
        
        assertEquals("Schedule cannot be null", exception.getMessage());
    }

    // Тест 3: Спроба бронювання невалідного місця
    @Test
    void bookTicket_InvalidSeat_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> bookingService.bookTicket(testSchedule, 101, false));
        
        assertTrue(exception.getMessage().contains("Invalid seat number"));
    }

    // Тест 4: Спроба бронювання зайнятого місця
    @Test
    void bookTicket_OccupiedSeat_ThrowsException() {
        bookingService.bookTicket(testSchedule, 1, false);
        Exception exception = assertThrows(IllegalStateException.class,
            () -> bookingService.bookTicket(testSchedule, 1, false));
        
        assertTrue(exception.getMessage().contains("already booked"));
    }

    // Тест 5: Отримання квитків за розкладом
    @Test
    void getTicketsForSchedule_ReturnsCorrectTickets() {
        bookingService.bookTicket(testSchedule, 1, false);
        bookingService.bookTicket(testSchedule, 2, true);
        
        List<Ticket> tickets = bookingService.getTicketsForSchedule(testSchedule);
        assertEquals(2, tickets.size());
        assertEquals(1, tickets.get(0).getSeatNumber());
        assertEquals(2, tickets.get(1).getSeatNumber());
    }

    // Тест 6: Отримання вільних місць
    @Test
    void getAvailableSeats_ReturnsCorrectSeats() {
        bookingService.bookTicket(testSchedule, 1, false);
        bookingService.bookTicket(testSchedule, 3, true);
        
        List<Integer> available = bookingService.getAvailableSeats(testSchedule);
        assertFalse(available.contains(1));
        assertFalse(available.contains(3));
        assertTrue(available.contains(2));
        assertEquals(testHall.getCapacity() - 2, available.size());
    }

    // Тест 7: Експорт квитків
    @Test
    void exportTicketsToFile_WritesCorrectData() throws IOException {
        bookingService.bookTicket(testSchedule, 1, false);
        String testFile = "test_export.csv";
        
        try {
            bookingService.exportTicketsToFile(testFile, 
                Comparator.comparingInt(Ticket::getSeatNumber));
            
            BufferedReader reader = new BufferedReader(new FileReader(testFile));
            String header = reader.readLine();
            String data = reader.readLine();
            
            assertEquals("Movie,Time,Hall,SeatNumber,IsVIP,Price", header);
            assertTrue(data.contains("Inception,18:00,1,1,false"));
        } finally {
            new File(testFile).delete();
        }
    }

    // Тест 8: Імпорт квитків
    @Test
    void importTicketsFromFile_ReadsCorrectData() throws IOException {
        String testFile = "test_import.csv";
        try (PrintWriter writer = new PrintWriter(testFile)) {
            writer.println("Movie,Time,Hall,SeatNumber,IsVIP");
            writer.println("Inception,18:00,1,42,true");
        }
        
        bookingService.importTicketsFromFile(testFile);
        
        // Перевіряємо через getTicketsForSchedule замість getTickets
        List<Ticket> tickets = bookingService.getTicketsForSchedule(testSchedule);
        assertEquals(1, tickets.size());
        assertEquals(42, tickets.get(0).getSeatNumber());
        assertTrue(tickets.get(0).isVipSeat());
        
        new File(testFile).delete();
    }

    // Тест 9: Фільтрація фільмів за жанром
    @Test
    void getMoviesByGenre_ReturnsCorrectMovies() {
        Movie drama = new Movie("The Godfather", "Drama", 175, 9.2);
        bookingService.addMovie(drama);
        
        List<Movie> sciFi = bookingService.getMoviesByGenre("Sci-Fi");
        assertEquals(1, sciFi.size());
        assertEquals("Inception", sciFi.get(0).getTitle());
        
        List<Movie> dramaMovies = bookingService.getMoviesByGenre("Drama");
        assertEquals(1, dramaMovies.size());
        assertEquals("The Godfather", dramaMovies.get(0).getTitle());
    }
}