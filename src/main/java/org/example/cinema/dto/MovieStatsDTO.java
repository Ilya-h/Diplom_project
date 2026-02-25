package org.example.cinema.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MovieStatsDTO {
    private Long movieId;
    private String title;
    private Long ticketsSold;
    private Double totalRevenue;
    private Double averageTicketPrice;
    private LocalDateTime firstSession;
    private LocalDateTime lastSession;
    private Double occupancyRate;
    private Long totalSessions;

    public MovieStatsDTO(Long movieId, String title, Long ticketsSold, Double totalRevenue,
                         Double averageTicketPrice, LocalDateTime firstSession,
                         LocalDateTime lastSession) {
        this.movieId = movieId;
        this.title = title;
        this.ticketsSold = ticketsSold != null ? ticketsSold : 0L;
        this.totalRevenue = totalRevenue != null ? totalRevenue : 0.0;
        this.averageTicketPrice = averageTicketPrice != null ? averageTicketPrice : 0.0;
        this.firstSession = firstSession;
        this.lastSession = lastSession;
        this.occupancyRate = 0.0; // Нужен отдельный расчет
        this.totalSessions = 0L; // Нужен отдельный запрос
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Double getAverageTicketPrice() {
        return averageTicketPrice;
    }

    public void setAverageTicketPrice(Double averageTicketPrice) {
        this.averageTicketPrice = averageTicketPrice;
    }

    public LocalDateTime getFirstSession() {
        return firstSession;
    }

    public void setFirstSession(LocalDateTime firstSession) {
        this.firstSession = firstSession;
    }

    public LocalDateTime getLastSession() {
        return lastSession;
    }

    public void setLastSession(LocalDateTime lastSession) {
        this.lastSession = lastSession;
    }

    public Double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(Double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public Long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Long totalSessions) {
        this.totalSessions = totalSessions;
    }
}