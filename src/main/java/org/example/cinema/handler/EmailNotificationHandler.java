package org.example.cinema.handler;

import org.example.cinema.model.Ticket;
import org.example.cinema.model.User;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationHandler {

    // Временно отключим отправку email
    public void sendTicketConfirmation(User user, Ticket ticket) {
        // Логирование вместо отправки email
        System.out.println("=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + user.getEmail());
        System.out.println("Subject: Подтверждение покупки билета");
        System.out.println("Ticket: " + ticket.getTicketNumber());
        System.out.println("Movie: " + ticket.getSession().getMovie().getTitle());
        System.out.println("==========================");
    }

    public void sendBookingReminder(User user, Ticket ticket) {
        System.out.println("=== REMINDER NOTIFICATION ===");
        System.out.println("To: " + user.getEmail());
        System.out.println("Subject: Напоминание о сеансе");
        System.out.println("Movie: " + ticket.getSession().getMovie().getTitle());
        System.out.println("Time: " + ticket.getSession().getStartTime());
        System.out.println("=============================");
    }

    public void sendPasswordReset(User user, String resetToken) {
        System.out.println("=== PASSWORD RESET ===");
        System.out.println("To: " + user.getEmail());
        System.out.println("Token: " + resetToken);
        System.out.println("=====================");
    }
}