package org.example.cinema.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cinema_halls")
@Data
@ToString(exclude = {"sessions", "seats"})
public class CinemaHall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer totalSeats;

    private String description;

    @Column(name = "hall_type")
    private String hallType; // "STANDARD", "VIP", "IMAX", "3D"

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "rows_count")
    private Integer rowsCount;

    @Column(name = "seats_per_row")
    private Integer seatsPerRow;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL)
    private List<Session> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    public CinemaHall() {}

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public String getDescription() {
        return description;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public String getHallType() {
        return hallType;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Integer getRowsCount() {
        return rowsCount;
    }

    public Integer getSeatsPerRow() {
        return seatsPerRow;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public void setHallType(String hallType) {
        this.hallType = hallType;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setRowsCount(Integer rowsCount) {
        this.rowsCount = rowsCount;
    }

    public void setSeatsPerRow(Integer seatsPerRow) {
        this.seatsPerRow = seatsPerRow;
    }
}