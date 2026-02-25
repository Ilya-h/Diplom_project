package org.example.cinema.controller;

import org.example.cinema.dto.*;
import org.example.cinema.model.CinemaHall;
import org.example.cinema.model.Movie;
import org.example.cinema.model.Session;
import org.example.cinema.model.User;
import org.example.cinema.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final TicketService ticketService;
    private final MovieService movieService;
    private final SessionService sessionService;
    private final UserService userService;
    private final PaymentService paymentService;

    @Autowired
    public AdminController(TicketService ticketService, MovieService movieService,
                           SessionService sessionService, UserService userService,
                           PaymentService paymentService) {
        this.ticketService = ticketService;
        this.movieService = movieService;
        this.sessionService = sessionService;
        this.userService = userService;
        this.paymentService = paymentService;
    }

    // dashboard - панель управления администратора
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Добавляем проверку на null
        if (userDetails == null) {
            return "redirect:/login";
        }

        var userOptional = userService.findUserByEmail(userDetails.getUsername());
        if (userOptional.isPresent() && "ADMIN".equals(userOptional.get().getRole())) {

            // Продажи по фильмам за сегодня (используем PopularMovieDTO с фильтром по дате)
            List<PopularMovieDTO> todayMovieSales = ticketService.getTodayMovieSales();

            // Популярные фильмы за все время
            List<PopularMovieDTO> popularMovies = ticketService.getPopularMovies(5);

            // Общая выручка
            Double totalRevenue = paymentService.getTotalRevenue();

            model.addAttribute("todayMovieSales", todayMovieSales);  // ← Изменили имя атрибута!
            model.addAttribute("popularMovies", popularMovies);
            model.addAttribute("totalRevenue", totalRevenue);

            return "admin/dashboard";
        }
        return "redirect:/login";
    }

    // salesReport - отчет по продажам
    @GetMapping("/reports/sales")
    public String salesReport(@RequestParam(required = false, defaultValue = "today") String period,
                              @RequestParam(required = false) String movieTitle,
                              @RequestParam(required = false) String genre,
                              Model model) {

        LocalDateTime startDate, endDate;
        LocalDateTime now = LocalDateTime.now();

        switch (period) {
            case "today":
                startDate = LocalDateTime.of(now.toLocalDate(), LocalTime.MIN);
                endDate = LocalDateTime.of(now.toLocalDate(), LocalTime.MAX);
                break;
            case "week":
                startDate = now.minusDays(7);
                endDate = now;
                break;
            case "month":
                startDate = now.minusMonths(1);
                endDate = now;
                break;
            default:
                startDate = now.minusMonths(1);
                endDate = now;
        }

        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("movieTitle", movieTitle);
        model.addAttribute("genre", genre);

        return "admin/sales-report";
    }

    // manageUsers - управление пользователями
    @GetMapping("/users")
    public String manageUsers(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Добавляем проверку на null
        if (userDetails == null) {
            return "redirect:/login";
        }

        var userOptional = userService.findUserByEmail(userDetails.getUsername());
        if (userOptional.isPresent() && "ADMIN".equals(userOptional.get().getRole())) {

            List<User> allUsers = userService.getAllUsers();
            model.addAttribute("users", allUsers);

            return "admin/users";
        }
        return "redirect:/login";
    }

    // viewUserDetails - просмотр деталей пользователя
    @GetMapping("/users/{id}")
    public String viewUserDetails(@PathVariable Long id, Model model) {
        var userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Получение статистики пользователя
            var customerStats = ticketService.getCustomerStats(user.getId());
            List<TicketDetailsDTO> userTickets = ticketService.getTicketDetails(user.getId());

            model.addAttribute("user", user);
            model.addAttribute("customerStats", customerStats.orElse(null));
            model.addAttribute("userTickets", userTickets);

            return "admin/user-details";
        }
        return "redirect:/admin/users";
    }

    // updateUserRole - обновление роли пользователя
    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id,
                                 @RequestParam String role,
                                 RedirectAttributes redirectAttributes) {

        var userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(role);
            userService.saveUser(user);

            redirectAttributes.addFlashAttribute("success", "Роль пользователя обновлена");
        } else {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден");
        }

        return "redirect:/admin/users/" + id;
    }

    // movieStats - статистика по фильмам
    @GetMapping("/reports/movies")
    public String movieStats(Model model) {
        List<Movie> movies = movieService.getAllMovies();
        List<PopularMovieDTO> popularMovies = ticketService.getPopularMovies(10);

        model.addAttribute("movies", movies);
        model.addAttribute("popularMovies", popularMovies);

        return "admin/movie-stats";
    }

    // sessionStats - статистика по сеансам
    @GetMapping("/reports/sessions")
    public String sessionStats(Model model) {
        List<Session> sessions = sessionService.getAllSessions();

        // Сортируем по возрастанию ID (от 1, 2, 3...)
        List<Session> sortedSessions = sessions.stream()
                .sorted(Comparator.comparing(Session::getId))
                .collect(Collectors.toList());

        model.addAttribute("sessions", sortedSessions);

        return "admin/session-stats";
    }

}