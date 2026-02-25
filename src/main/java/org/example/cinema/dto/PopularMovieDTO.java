package org.example.cinema.dto;

import lombok.Data;

@Data
public class PopularMovieDTO {
    private Long movieId;
    private String title;
    private Long ticketsSold;
    private Double totalRevenue;
    private Double averageRating;
    private Long totalReviews;

    public PopularMovieDTO(Long movieId, String title, Long ticketsSold, Double totalRevenue) {
        this.movieId = movieId;
        this.title = title;
        this.ticketsSold = ticketsSold;
        this.totalRevenue = totalRevenue;
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

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }
}