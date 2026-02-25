package org.example.cinema.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SessionStatsDTO {
    private Long sessionId;
    private String movieTitle;
    private LocalDateTime startTime;
    private Long ticketsSold;
    private Double totalRevenue;
    private Long occupiedSeats;
    private Long availableSeats;
    private Double occupancyPercentage;
    private Long totalSeats;

    public SessionStatsDTO(Long sessionId, String movieTitle, LocalDateTime startTime,
                           Long ticketsSold, Double totalRevenue, Long occupiedSeats,
                           Long availableSeats) {
        this.sessionId = sessionId;
        this.movieTitle = movieTitle;
        this.startTime = startTime;
        this.ticketsSold = ticketsSold != null ? ticketsSold : 0L;
        this.totalRevenue = totalRevenue != null ? totalRevenue : 0.0;
        this.occupiedSeats = occupiedSeats != null ? occupiedSeats : 0L;
        this.availableSeats = availableSeats != null ? availableSeats : 0L;
        this.totalSeats = this.occupiedSeats + this.availableSeats;
        this.occupancyPercentage = this.totalSeats > 0 ?
                Math.round((this.occupiedSeats.doubleValue() / this.totalSeats.doubleValue()) * 100.0) : 0.0;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Long getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(Long ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getOccupiedSeats() {
        return occupiedSeats;
    }

    public void setOccupiedSeats(Long occupiedSeats) {
        this.occupiedSeats = occupiedSeats;
    }

    public Long getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Long availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Double getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(Double occupancyPercentage) {
        this.occupancyPercentage = occupancyPercentage;
    }

    public Long getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Long totalSeats) {
        this.totalSeats = totalSeats;
    }
}