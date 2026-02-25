package org.example.cinema.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "seats")
@Data
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private CinemaHall hall;

    @Column(name = "row_num", nullable = false)
    private Integer rowNumber;  // Java поле - rowNumber, SQL колонка - row_num

    @Column(name = "seat_num", nullable = false)
    private Integer seatNumber; // Java поле - seatNumber, SQL колонка - seat_num

    @Column(nullable = false)
    private String seatType; // "STANDARD", "VIP", "COUPLE", "DISABLED"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @OneToOne(mappedBy = "seat")
    private Ticket ticket;

    @Column(nullable = false)
    private Boolean isAvailable;

    public Seat() {}

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public CinemaHall getHall() {
        return hall;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public Session getSession() {
        return session;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHall(CinemaHall hall) {
        this.hall = hall;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }
}