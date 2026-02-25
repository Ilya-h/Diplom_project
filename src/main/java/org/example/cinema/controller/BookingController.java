package org.example.cinema.controller;

import org.example.cinema.dto.SeatSelectionDTO;
import org.example.cinema.model.*;
import org.example.cinema.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/booking")
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final MovieService movieService;
    private final SessionService sessionService;
    private final TicketService ticketService;
    private final UserService userService;
    private final SeatService seatService;
    private final CinemaHallService cinemaHallService;

    @Autowired
    public BookingController(MovieService movieService, SessionService sessionService,
                             TicketService ticketService, UserService userService,
                             SeatService seatService, CinemaHallService cinemaHallService) {
        this.movieService = movieService;
        this.sessionService = sessionService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.seatService = seatService;
        this.cinemaHallService = cinemaHallService;
    }

    @ModelAttribute("seatTypeTranslations")
    public Map<String, String> getSeatTypeTranslations() {
        Map<String, String> translations = new HashMap<>();
        translations.put("STANDARD", "Стандарт");
        translations.put("VIP", "VIP");
        translations.put("COUPLE", "Парное");
        translations.put("DISABLED", "Для инвалидов");
        return translations;
    }

    // showMovies - отображение фильмов для бронирования
    @GetMapping("/movies")
    public String showMovies(Model model) {
        // Показываем ВСЕ фильмы для тестирования
        List<Movie> movies = movieService.getAllMovies();
        System.out.println("Показываем " + movies.size() + " фильмов");
        model.addAttribute("movies", movies);
        return "customer/movies";
    }

    // showMovieDetails - отображение деталей фильма и сеансов
    @GetMapping("/movies/{id}")
    public String showMovieDetails(@PathVariable Long id, Model model) {
        System.out.println("=== ОТЛАДКА: Детали фильма ID=" + id);

        Optional<Movie> movieOptional = movieService.getMovieById(id);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();

            // Логируем данные фильма
            System.out.println("Фильм: " + movie.getTitle());
            System.out.println("Постер URL: " + movie.getPosterUrl());
            System.out.println("ID фильма: " + movie.getId());

            // Получаем ВСЕ сеансы (для отладки)
            List<Session> allSessions = sessionService.getSessionsByMovie(id);
            System.out.println("Всего сеансов в БД: " + allSessions.size());

            // Фильтруем только будущие
            LocalDateTime now = LocalDateTime.now();
            List<Session> futureSessions = allSessions.stream()
                    .filter(s -> s.getStartTime() != null && s.getStartTime().isAfter(now))
                    .collect(Collectors.toList());

            System.out.println("Будущих сеансов: " + futureSessions.size());

            // Для отладки выводим все сеансы
            for (Session s : allSessions) {
                System.out.println("Сеанс ID=" + s.getId() +
                        ", Начало: " + s.getStartTime() +
                        ", Фильм: " + (s.getMovie() != null ? s.getMovie().getTitle() : "null"));
            }

            model.addAttribute("movie", movie);
            model.addAttribute("sessions", futureSessions);  // только будущие
            return "customer/movie-details";
        }

        System.out.println("Фильм не найден!");
        return "redirect:/booking/movies";
    }

    // selectSession - выбор сеанса для бронирования
    @GetMapping("/sessions/{id}")
    public String selectSession(@PathVariable Long id,
                                @RequestParam(value = "success", required = false) String successMessage,
                                Model model) {
        System.out.println("=== SELECT SESSION DEBUG ===");

        Optional<Session> sessionOptional = sessionService.getSessionById(id);
        if (sessionOptional.isPresent()) {
            Session session = sessionOptional.get();

            // ИСПРАВЛЕНИЕ: используем новый метод для получения ВСЕХ мест
            List<SeatSelectionDTO> allSeats = ticketService.getAvailableSeatsForSession(id);
            System.out.println("Total seats in hall: " + allSeats.size());

            // Считаем статистику
            long availableCount = allSeats.stream().filter(SeatSelectionDTO::getIsAvailable).count();
            long occupiedCount = allSeats.stream().filter(s -> !s.getIsAvailable()).count();
            System.out.println("Available: " + availableCount + ", Occupied: " + occupiedCount);

            // СОРТИРОВКА: сначала по ряду, потом по номеру места
            allSeats.sort(Comparator
                    .comparing(SeatSelectionDTO::getRowNumber)
                    .thenComparing(SeatSelectionDTO::getSeatNumber));

            // ГРУППИРУЕМ МЕСТА ПО РЯДАМ
            Map<Integer, List<SeatSelectionDTO>> seatsByRow = new TreeMap<>();
            for (SeatSelectionDTO seat : allSeats) {
                seatsByRow.computeIfAbsent(seat.getRowNumber(), k -> new ArrayList<>()).add(seat);
            }

            model.addAttribute("movieSession", session);
            model.addAttribute("seatsByRow", seatsByRow);

            // Добавляем сообщение об успешной покупке, если есть
            if (successMessage != null) {
                model.addAttribute("successMessage", successMessage);
            }

            return "customer/session-selection";
        }

        return "redirect:/booking/movies";
    }

    // selectSeats - выбор мест для бронирования
    @PostMapping("/sessions/{id}/select-seats")
    public String selectSeats(@PathVariable Long id,
                              @RequestParam(value = "selectedSeatIds", required = false) List<Long> selectedSeatIds,
                              HttpSession httpSession,
                              RedirectAttributes redirectAttributes) {

        if (selectedSeatIds == null || selectedSeatIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Выберите хотя бы одно место");
            return "redirect:/booking/sessions/" + id;
        }

        httpSession.setAttribute("selectedSessionId", id);
        httpSession.setAttribute("selectedSeatIds", selectedSeatIds);

        return "redirect:/booking/review";
    }

    // Обработка перехода к оплате
    @PostMapping("/sessions/{id}/payment")
    public String processPayment(@PathVariable Long id,
                                 @RequestParam(value = "selectedSeatIds", required = false) String selectedSeatIdsStr,
                                 @RequestParam(value = "totalPrice", required = false) Double totalPrice,
                                 @RequestParam(value = "selectedSeatsCount", required = false) Integer selectedSeatsCount,
                                 HttpSession httpSession,
                                 RedirectAttributes redirectAttributes) {

        System.out.println("Processing payment for session: " + id);
        System.out.println("Selected seat IDs: " + selectedSeatIdsStr);
        System.out.println("Total price: " + totalPrice);
        System.out.println("Selected seats count: " + selectedSeatsCount);

        if (selectedSeatIdsStr == null || selectedSeatIdsStr.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Выберите хотя бы одно место");
            return "redirect:/booking/sessions/" + id;
        }

        // Разбиваем строку на список ID
        List<Long> selectedSeatIds = Arrays.stream(selectedSeatIdsStr.split(","))
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());

        if (selectedSeatIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Выберите хотя бы одно место");
            return "redirect:/booking/sessions/" + id;
        }

        // Сохраняем в сессии для страницы подтверждения
        httpSession.setAttribute("selectedSessionId", id);
        httpSession.setAttribute("selectedSeatIds", selectedSeatIds);
        httpSession.setAttribute("totalPrice", totalPrice);

        // Перенаправляем на страницу подтверждения
        return "redirect:/booking/review";
    }

    // showReviewPage - отображение страницы подтверждения бронирования
    @GetMapping("/review")
    public String showReviewPage(HttpSession httpSession, Model model) {
        Long sessionId = (Long) httpSession.getAttribute("selectedSessionId");
        List<Long> seatIds = (List<Long>) httpSession.getAttribute("selectedSeatIds");

        if (sessionId == null || seatIds == null || seatIds.isEmpty()) {
            return "redirect:/booking/movies";
        }

        Optional<Session> sessionOptional = sessionService.getSessionById(sessionId);
        if (sessionOptional.isEmpty()) {
            return "redirect:/booking/movies";
        }

        Session session = sessionOptional.get();
        List<Seat> seats = new ArrayList<>();

        for (Long seatId : seatIds) {
            Optional<Seat> seatOptional = seatService.getSeatById(seatId);
            seatOptional.ifPresent(seats::add);
        }

        if (seats.isEmpty()) {
            return "redirect:/booking/movies";
        }

        // ПРАВИЛЬНЫЙ РАСЧЕТ ЦЕНЫ согласно ценам из HTML
        double totalPrice = 0.0;
        for (Seat seat : seats) {
            // Используем фиксированные цены как в HTML
            switch (seat.getSeatType()) {
                case "VIP":
                    totalPrice += 650.0;
                    break;
                case "COUPLE":
                    totalPrice += 950.0;
                    break;
                case "DISABLED":
                    totalPrice += 0.0;
                    break;
                default: // STANDARD или null
                    totalPrice += 250.0;
                    break;
            }
        }

        model.addAttribute("movieSession", session);
        model.addAttribute("seats", seats);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("seatCount", seats.size());

        return "customer/review-booking";
    }

    // confirmBooking - подтверждение бронирования
    @SuppressWarnings("unchecked")
    @PostMapping("/confirm")
    public String confirmBooking(@AuthenticationPrincipal UserDetails userDetails,
                                 HttpSession httpSession,
                                 RedirectAttributes redirectAttributes) {

        Long sessionId = (Long) httpSession.getAttribute("selectedSessionId");
        List<Long> seatIds = (List<Long>) httpSession.getAttribute("selectedSeatIds");

        if (sessionId == null || seatIds == null || seatIds.isEmpty()) {
            return "redirect:/booking/movies";
        }

        String email = userDetails.getUsername();
        Optional<User> userOptional = userService.findUserByEmail(email);

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();
        List<Ticket> purchasedTickets = new ArrayList<>();

        // ДОПОЛНИТЕЛЬНАЯ ПРОВЕРКА: убедимся, что все места все еще доступны
        boolean allAvailable = seatService.areSeatsAvailable(seatIds);
        if (!allAvailable) {
            redirectAttributes.addFlashAttribute("error",
                    "Некоторые выбранные места уже заняты. Пожалуйста, выберите другие места.");
            return "redirect:/booking/review";
        }

        // Покупка каждого выбранного места
        for (Long seatId : seatIds) {
            try {
                Ticket ticket = ticketService.purchaseTicket(user.getId(), sessionId, seatId, "ONLINE");
                purchasedTickets.add(ticket);
            } catch (Exception e) {
                // Если произошла ошибка, отменяем все уже купленные билеты
                for (Ticket purchasedTicket : purchasedTickets) {
                    try {
                        ticketService.cancelTicket(purchasedTicket.getId());
                    } catch (Exception ex) {
                        // Логируем ошибку отмены
                        logger.error("Error cancelling ticket: " + ex.getMessage());
                    }
                }

                redirectAttributes.addFlashAttribute("error",
                        "Ошибка при покупке билета: " + e.getMessage());
                return "redirect:/booking/review";
            }
        }

        // Помечаем все места как занятые для этого сеанса
        try {
            seatService.markSeatsAsOccupied(seatIds, sessionId);
        } catch (Exception e) {
            logger.error("Error marking seats as occupied: " + e.getMessage());
        }

        // Очистка сессии
        httpSession.removeAttribute("selectedSessionId");
        httpSession.removeAttribute("selectedSeatIds");

        if (!purchasedTickets.isEmpty()) {
            List<Long> ticketIds = new ArrayList<>();
            for (Ticket ticket : purchasedTickets) {
                ticketIds.add(ticket.getId());
            }
            httpSession.setAttribute("lastPurchaseIds", ticketIds);
        }

        redirectAttributes.addFlashAttribute("success", "Билеты успешно куплены!");
        return "redirect:/booking/confirmation";
    }

    // showConfirmation - отображение подтверждения покупки
    @SuppressWarnings("unchecked")
    @GetMapping("/confirmation")
    public String showConfirmation(HttpSession httpSession, Model model) {
        List<Long> ticketIds = (List<Long>) httpSession.getAttribute("lastPurchaseIds");

        if (ticketIds != null && !ticketIds.isEmpty()) {
            List<Ticket> tickets = new ArrayList<>();
            for (Long ticketId : ticketIds) {
                Optional<Ticket> ticketOptional = ticketService.getTicketById(ticketId);
                ticketOptional.ifPresent(tickets::add);
            }

            model.addAttribute("tickets", tickets);
            httpSession.removeAttribute("lastPurchaseIds");
        }

        return "customer/confirmation";
    }

    // getSeatTypeMultiplier - получение множителя цены по типу места
    private double getSeatTypeMultiplier(String seatType) {
        if ("VIP".equals(seatType)) {
            return 1.5;
        } else if ("COUPLE".equals(seatType)) {
            return 2.0;
        } else if ("DISABLED".equals(seatType)) {
            return 0.8;
        } else {
            return 1.0; // STANDARD
        }
    }
}