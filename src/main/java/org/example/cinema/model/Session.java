package org.example.cinema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessions")
@Data
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)  // ИЗМЕНЕНО НА EAGER
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.EAGER)  // ИЗМЕНЕНО НА EAGER
    @JoinColumn(name = "hall_id", nullable = false)
    private CinemaHall hall;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Double price;

    private String format; // 2D, 3D, IMAX, etc.

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Seat> availableSeats = new ArrayList<>();

    // НОВОЕ: Коэффициенты для разных типов мест
    @Transient
    private static final Double VIP_MULTIPLIER = 1.5;

    @Transient
    private static final Double COUPLE_MULTIPLIER = 2.0;

    @Transient
    private static final Double DISABLED_MULTIPLIER = 0.8;

    @Transient
    private static final Double STANDARD_MULTIPLIER = 1.0;

    public Session() {}

    // НОВЫЕ МЕТОДЫ ДЛЯ РАСЧЕТА ЦЕН

    /**
     * Рассчитывает цену места в зависимости от его типа
     */
    public Double calculateSeatPrice(String seatType) {
        return price * getMultiplierForSeatType(seatType);
    }

    /**
     * Рассчитывает общую стоимость для списка мест
     */
    public Double calculateTotalPrice(List<String> seatTypes) {
        return seatTypes.stream()
                .mapToDouble(this::calculateSeatPrice)
                .sum();
    }

    /**
     * Возвращает множитель цены для типа места
     */
    public static Double getMultiplierForSeatType(String seatType) {
        if (seatType == null) return STANDARD_MULTIPLIER;

        return switch (seatType.toUpperCase()) {
            case "VIP" -> VIP_MULTIPLIER;
            case "COUPLE" -> COUPLE_MULTIPLIER;
            case "DISABLED" -> DISABLED_MULTIPLIER;
            default -> STANDARD_MULTIPLIER;
        };
    }

    /**
     * Возвращает имя типа места на русском
     */
    public static String getSeatTypeName(String seatType) {
        if (seatType == null) return "Стандарт";

        return switch (seatType.toUpperCase()) {
            case "VIP" -> "VIP";
            case "COUPLE" -> "Парные";
            case "DISABLED" -> "Для инвалидов";
            default -> "Стандарт";
        };
    }

    // Геттеры и сеттеры


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public CinemaHall getHall() {
        return hall;
    }

    public void setHall(CinemaHall hall) {
        this.hall = hall;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public List<Seat> getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(List<Seat> availableSeats) {
        this.availableSeats = availableSeats;
    }
}