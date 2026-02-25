package org.example.cinema.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketDetailsDTO {
    private Long id;
    private String ticketNumber;
    private String movieTitle;
    private LocalDateTime sessionDateTime;
    private String hallName;
    private Integer rowNumber;          // Оставляем rowNumber
    private Integer seatNumber;         // Оставляем seatNumber
    private String seatType;
    private Double price;
    private String status;
    private LocalDateTime purchaseDate;
    private String format;
    private String qrCodeUrl;

    public TicketDetailsDTO(Long id, String ticketNumber, String movieTitle,
                            LocalDateTime sessionDateTime, String hallName, Integer rowNumber,
                            Integer seatNumber, String seatType, Double price,
                            String status, LocalDateTime purchaseDate) {
        this.id = id;
        this.ticketNumber = ticketNumber;
        this.movieTitle = movieTitle;
        this.sessionDateTime = sessionDateTime;
        this.hallName = hallName;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.price = price;
        this.status = status;
        this.purchaseDate = purchaseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public LocalDateTime getSessionDateTime() {
        return sessionDateTime;
    }

    public void setSessionDateTime(LocalDateTime sessionDateTime) {
        this.sessionDateTime = sessionDateTime;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}