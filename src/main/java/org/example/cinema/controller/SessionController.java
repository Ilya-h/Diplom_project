package org.example.cinema.controller;

import org.example.cinema.model.CinemaHall;
import org.example.cinema.model.Movie;
import org.example.cinema.model.Session;
import org.example.cinema.model.User;
import org.example.cinema.service.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin/sessions")
public class SessionController {
    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);
    private final SessionService sessionService;
    private final MovieService movieService;
    private final CinemaHallService cinemaHallService;
    private final UserService userService;

    @Autowired
    public SessionController(SessionService sessionService, MovieService movieService,
                             CinemaHallService cinemaHallService, UserService userService) {
        this.sessionService = sessionService;
        this.movieService = movieService;
        this.cinemaHallService = cinemaHallService;
        this.userService = userService;
    }

    // listSessions - отображение списка всех сеансов
    @GetMapping
    public String listSessions(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> userOptional = userService.findUserByEmail(userDetails.getUsername());
        if (userOptional.isPresent() && "ADMIN".equals(userOptional.get().getRole())) {
            List<Session> sessions = sessionService.getAllSessions();
            sessions.sort(Comparator.comparing(Session::getId));
            model.addAttribute("sessions", sessions);
            return "admin/sessions";
        }
        return "redirect:/login";
    }

    // viewSession - просмотр деталей сеанса (ПРОСТОЙ И РАБОЧИЙ ВАРИАНТ)
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String viewSession(@PathVariable Long id, Model model) {
        logger.info("=== НАЧАЛО viewSession для ID: {} ===", id);

        try {
            Optional<Session> sessionOptional = sessionService.getSessionById(id);

            if (sessionOptional.isEmpty()) {
                logger.error("Сеанс с ID {} не найден", id);
                model.addAttribute("error", "Сеанс не найден");

                // Явно добавляем атрибут session как null для отладки
                model.addAttribute("session", null);

                return "admin/session-details";
            }

            Session session = sessionOptional.get();

            // ОТЛАДКА: Логируем все поля
            logger.info("=== ДЕТАЛЬНАЯ ОТЛАДКА СЕАНСА ===");
            logger.info("Session ID: {}", session.getId());
            logger.info("Session object: {}", session);
            logger.info("Session class: {}", session.getClass().getName());

            if (session.getMovie() != null) {
                logger.info("Movie: {} (class: {})",
                        session.getMovie().getTitle(),
                        session.getMovie().getClass().getName());
                logger.info("Movie is proxy?: {}",
                        Hibernate.isPropertyInitialized(session.getMovie(), "title") ? "No, initialized" : "Yes, proxy");
            } else {
                logger.info("Movie is NULL");
            }

            if (session.getHall() != null) {
                logger.info("Hall: {} (class: {})",
                        session.getHall().getName(),
                        session.getHall().getClass().getName());
                logger.info("Hall capacity: {}", session.getHall().getCapacity());
            } else {
                logger.info("Hall is NULL");
            }

            logger.info("Price: {}", session.getPrice());
            logger.info("StartTime: {}", session.getStartTime());
            logger.info("Format: {}", session.getFormat());

            // Форматируем дату для отображения
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            String formattedStartTime = session.getStartTime().format(formatter);

            // Очищаем модель перед добавлением (на случай конфликта)
            model.asMap().clear();

            // Добавляем сеанс с явным именем
            model.addAttribute("currentSession", session);  // Изменили имя!
            model.addAttribute("session", session);         // Оставляем и старое для совместимости

            model.addAttribute("formattedStartTime", formattedStartTime);

            // Дополнительно передаем данные фильма и зала
            if (session.getMovie() != null) {
                // Явно инициализируем ленивые поля
                Hibernate.initialize(session.getMovie());

                model.addAttribute("movieTitle", session.getMovie().getTitle());
                model.addAttribute("movieDescription", session.getMovie().getDescription());
                model.addAttribute("movieDuration", session.getMovie().getDurationMinutes());
                model.addAttribute("movieGenre", session.getMovie().getGenre());
                model.addAttribute("movieDirector", session.getMovie().getDirector());
                model.addAttribute("movieActors", session.getMovie().getActors());
                model.addAttribute("movieAgeRating", session.getMovie().getAgeRating());
                model.addAttribute("moviePosterUrl", session.getMovie().getPosterUrl());
                model.addAttribute("movieReleaseDate", session.getMovie().getReleaseDate());
            }

            if (session.getHall() != null) {
                // Явно инициализируем ленивые поля
                Hibernate.initialize(session.getHall());

                model.addAttribute("hallName", session.getHall().getName());
                model.addAttribute("hallCapacity", session.getHall().getCapacity());
            }

            // Для отладки передаем все значения явно
            model.addAttribute("debugId", session.getId());
            model.addAttribute("debugPrice", session.getPrice());
            model.addAttribute("debugFormat", session.getFormat());

            logger.info("=== ВСЕ АТРИБУТЫ ДОБАВЛЕНЫ В МОДЕЛЬ ===");
            model.asMap().forEach((key, value) -> {
                logger.info("Model attribute: {} = {}", key, value);
            });

            return "admin/session-details";

        } catch (Exception e) {
            logger.error("Ошибка при загрузке сеанса ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("error", "Ошибка загрузки сеанса: " + e.getMessage());
            return "admin/session-details";
        }
    }

    // showCreateSessionForm - отображение формы создания нового сеанса
    @GetMapping("/new")
    public String showCreateSessionForm(Model model) {
        List<Movie> movies = movieService.getAllMovies();
        List<CinemaHall> halls = cinemaHallService.getAllCinemaHalls();

        model.addAttribute("session", new Session());
        model.addAttribute("movies", movies);
        model.addAttribute("halls", halls);
        return "admin/session-form";
    }

    // createSession - создание нового сеанса
    @PostMapping
    public String createSession(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam Long movieId,
                                @RequestParam Long hallId,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startTime,
                                @RequestParam Double price,
                                @RequestParam String format,
                                RedirectAttributes redirectAttributes) {

        Optional<User> userOptional = userService.findUserByEmail(userDetails.getUsername());
        if (userOptional.isPresent() && "ADMIN".equals(userOptional.get().getRole())) {
            try {
                Session session = sessionService.createSession(movieId, hallId, startTime, price, format);
                redirectAttributes.addFlashAttribute("success", "Сеанс успешно создан");
                return "redirect:/admin/sessions/" + session.getId();
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Ошибка создания сеанса: " + e.getMessage());
                return "redirect:/admin/sessions/new";
            }
        }
        return "redirect:/login";
    }

    // deleteSession - удаление сеанса
    @PostMapping("/{id}/delete")
    public String deleteSession(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sessionService.deleteSession(id);
            redirectAttributes.addFlashAttribute("success", "Сеанс успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления сеанса: " + e.getMessage());
        }
        return "redirect:/admin/sessions";
    }

    // getTodaySessions - получение сеансов на сегодня
    @GetMapping("/today")
    public String getTodaySessions(Model model) {
        List<Session> todaySessions = sessionService.getSessionsForToday();
        todaySessions.sort(Comparator.comparing(Session::getStartTime));
        model.addAttribute("sessions", todaySessions);
        return "admin/today-sessions";
    }

    // showEditSessionForm - форма редактирования сеанса
    @GetMapping("/{id}/edit")
    public String showEditSessionForm(@PathVariable Long id, Model model) {
        Optional<Session> sessionOptional = sessionService.getSessionById(id);
        if (sessionOptional.isPresent()) {
            List<Movie> movies = movieService.getAllMovies();
            List<CinemaHall> halls = cinemaHallService.getAllCinemaHalls();

            model.addAttribute("session", sessionOptional.get());
            model.addAttribute("movies", movies);
            model.addAttribute("halls", halls);
            return "admin/session-edit-form";
        }
        return "redirect:/admin/sessions";
    }
}