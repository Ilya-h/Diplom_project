package org.example.cinema.handler;

import org.example.cinema.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class PaymentSuccessHandler {

    private final TicketService ticketService;

    @Autowired
    public PaymentSuccessHandler(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void handleSuccessfulPayment(Long ticketId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Обновление статуса билета
        // Отправка email с билетом
        // Перенаправление на страницу с деталями билета
        response.sendRedirect("/tickets/" + ticketId + "/success");
    }
}