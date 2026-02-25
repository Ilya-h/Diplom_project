package org.example.cinema.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class PaymentRequestDTO {

    @NotNull(message = "Total amount is required")
    private Double totalAmount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // "CARD", "CASH", "ONLINE"

    private String cardNumber;
    private String cardHolderName;
    private String cardExpiry;
    private String cardCvv;

    @NotNull(message = "Ticket IDs are required")
    private Long[] ticketIds;

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    public String getCardCvv() {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }

    public Long[] getTicketIds() {
        return ticketIds;
    }

    public void setTicketIds(Long[] ticketIds) {
        this.ticketIds = ticketIds;
    }
}