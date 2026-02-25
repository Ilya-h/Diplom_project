package org.example.cinema.controller;

import org.example.cinema.dto.TicketDetailsDTO;
import org.example.cinema.model.User;
import org.example.cinema.service.TicketService;
import org.example.cinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    private final TicketService ticketService;
    private final UserService userService;

    @Autowired
    public CustomerController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    // dashboard - личный кабинет клиента
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userService.findUserByEmail(userDetails.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Получение статистики клиента
            var customerStats = ticketService.getCustomerStats(user.getId());

            // Получение последних билетов
            List<TicketDetailsDTO> recentTickets = ticketService.getTicketDetails(user.getId())
                    .stream()
                    .limit(5)
                    .toList();

            model.addAttribute("user", user);
            model.addAttribute("customerStats", customerStats.orElse(null));
            model.addAttribute("recentTickets", recentTickets);

            return "customer/dashboard";
        }
        return "redirect:/login";
    }

    // myTickets - просмотр билетов пользователя
    @GetMapping("/tickets")
    public String myTickets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userService.findUserByEmail(userDetails.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<TicketDetailsDTO> tickets = ticketService.getTicketDetails(user.getId());

            model.addAttribute("tickets", tickets);
            return "customer/my-tickets";
        }
        return "redirect:/login";
    }

    // viewTicketDetails - просмотр деталей билета
    @GetMapping("/tickets/{id}")
    public String viewTicketDetails(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userService.findUserByEmail(userDetails.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            var ticketOptional = ticketService.getTicketById(id);
            if (ticketOptional.isPresent() &&
                    ticketOptional.get().getUser().getId().equals(user.getId())) {

                model.addAttribute("ticket", ticketOptional.get());
                return "customer/ticket-details";
            }
        }
        return "redirect:/customer/tickets";
    }

    // cancelTicket - отмена билета
    @PostMapping("/tickets/{id}/cancel")
    public String cancelTicket(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) { // Используем RedirectAttributes
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userService.findUserByEmail(userDetails.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            var ticketOptional = ticketService.getTicketById(id);
            if (ticketOptional.isPresent() &&
                    ticketOptional.get().getUser().getId().equals(user.getId())) {

                try {
                    ticketService.cancelTicket(id);
                    redirectAttributes.addFlashAttribute("success", "Билет успешно отменен");
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "Ошибка при отмене билета: " + e.getMessage());
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Билет не найден или у вас нет доступа");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден");
        }

        return "redirect:/customer/tickets";
    }

    // GET запрос - отображение страницы профиля
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userService.findUserByEmail(userDetails.getUsername());
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "customer/profile";
        }
        return "redirect:/login";
    }

    // updateProfile - обновление профиля
    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam(required = false) String phoneNumber,
                                RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            Optional<User> userOptional = userService.findUserByEmail(userDetails.getUsername());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                userService.updateUserProfile(user.getId(), firstName, lastName, phoneNumber);
                redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Пользователь не найден");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении профиля: " + e.getMessage());
        }

        return "redirect:/customer/profile";
    }
}