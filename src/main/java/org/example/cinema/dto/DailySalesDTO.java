package org.example.cinema.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailySalesDTO {
    private LocalDate date;
    private Long ticketsSold;
    private Double totalRevenue;
    private Double averageRevenuePerTicket;
    private Long uniqueCustomers;
    private Long totalSessions;

    // Конструктор для запроса из TicketRepository (4 параметра)
    public DailySalesDTO(LocalDate date, Long ticketsSold, Double totalRevenue,
                         Double averageRevenuePerTicket) {
        this.date = date;
        this.ticketsSold = ticketsSold != null ? ticketsSold : 0L;
        this.totalRevenue = totalRevenue != null ? totalRevenue : 0.0;
        this.averageRevenuePerTicket = averageRevenuePerTicket != null ? averageRevenuePerTicket : 0.0;
        this.uniqueCustomers = 0L;
        this.totalSessions = 0L;
    }

    // Конструктор для 3 параметров
    public DailySalesDTO(LocalDate date, Long ticketsSold, Double totalRevenue) {
        this.date = date;
        this.ticketsSold = ticketsSold != null ? ticketsSold : 0L;
        this.totalRevenue = totalRevenue != null ? totalRevenue : 0.0;
        this.averageRevenuePerTicket = (ticketsSold != null && ticketsSold > 0 && totalRevenue != null)
                ? totalRevenue / ticketsSold.doubleValue() : 0.0;
        this.uniqueCustomers = 0L;
        this.totalSessions = 0L;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public Double getAverageRevenuePerTicket() {
        return averageRevenuePerTicket;
    }

    public void setAverageRevenuePerTicket(Double averageRevenuePerTicket) {
        this.averageRevenuePerTicket = averageRevenuePerTicket;
    }

    public Long getUniqueCustomers() {
        return uniqueCustomers;
    }

    public void setUniqueCustomers(Long uniqueCustomers) {
        this.uniqueCustomers = uniqueCustomers;
    }

    public Long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Long totalSessions) {
        this.totalSessions = totalSessions;
    }
}