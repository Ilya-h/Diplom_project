package org.example.cinema.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerStatsDTO {
    private Long customerId;
    private String firstName;
    private String lastName;
    private Long totalTickets;
    private Double totalSpent;
    private Double averageTicketPrice;
    private LocalDateTime lastPurchase;
    private Long visitedMovies;
    private Double loyaltyDiscount;

    // Конструктор для запроса из TicketRepository
    public CustomerStatsDTO(Long customerId, String firstName, String lastName,
                            Long totalTickets, Double totalSpent, Double averageTicketPrice,
                            LocalDateTime lastPurchase) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalTickets = totalTickets != null ? totalTickets : 0L;
        this.totalSpent = totalSpent != null ? totalSpent : 0.0;
        this.averageTicketPrice = averageTicketPrice != null ? averageTicketPrice : 0.0;
        this.lastPurchase = lastPurchase;
        this.visitedMovies = 0L; // Будет установлено отдельно
        this.loyaltyDiscount = calculateLoyaltyDiscount(this.totalTickets);
    }

    // Полный конструктор
    public CustomerStatsDTO(Long customerId, String firstName, String lastName,
                            Long totalTickets, Double totalSpent, Double averageTicketPrice,
                            LocalDateTime lastPurchase, Long visitedMovies) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalTickets = totalTickets != null ? totalTickets : 0L;
        this.totalSpent = totalSpent != null ? totalSpent : 0.0;
        this.averageTicketPrice = averageTicketPrice != null ? averageTicketPrice : 0.0;
        this.lastPurchase = lastPurchase;
        this.visitedMovies = visitedMovies != null ? visitedMovies : 0L;
        this.loyaltyDiscount = calculateLoyaltyDiscount(this.totalTickets);
    }

    private Double calculateLoyaltyDiscount(Long totalTickets) {
        if (totalTickets == null) return 0.0;
        if (totalTickets >= 50) return 15.0;
        if (totalTickets >= 20) return 10.0;
        if (totalTickets >= 10) return 5.0;
        return 0.0;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Double getAverageTicketPrice() {
        return averageTicketPrice;
    }

    public void setAverageTicketPrice(Double averageTicketPrice) {
        this.averageTicketPrice = averageTicketPrice;
    }

    public LocalDateTime getLastPurchase() {
        return lastPurchase;
    }

    public void setLastPurchase(LocalDateTime lastPurchase) {
        this.lastPurchase = lastPurchase;
    }

    public Long getVisitedMovies() {
        return visitedMovies;
    }

    public void setVisitedMovies(Long visitedMovies) {
        this.visitedMovies = visitedMovies;
    }

    public Double getLoyaltyDiscount() {
        return loyaltyDiscount;
    }

    public void setLoyaltyDiscount(Double loyaltyDiscount) {
        this.loyaltyDiscount = loyaltyDiscount;
    }
}