package org.example.cinema.controller;

import org.example.cinema.model.*;
import org.example.cinema.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin/movies")
public class MovieController {
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;
    private final SessionService sessionService;
    private final CinemaHallService cinemaHallService;
    private final UserService userService;

    @Autowired
    public MovieController(MovieService movieService, SessionService sessionService,
                           CinemaHallService cinemaHallService, UserService userService) {
        this.movieService = movieService;
        this.sessionService = sessionService;
        this.cinemaHallService = cinemaHallService;
        this.userService = userService;
    }

    // listMovies - отображение списка всех фильмов
    @GetMapping
    public String listMovies(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> userOptional = userService.findUserByEmail(userDetails.getUsername());
        if (userOptional.isPresent() && "ADMIN".equals(userOptional.get().getRole())) {
            List<Movie> movies = movieService.getAllMovies();
            model.addAttribute("movies", movies);
            return "admin/movies";
        }
        return "redirect:/login";
    }

    // showCreateMovieForm - отображение формы создания нового фильма
    @GetMapping("/new")
    public String showCreateMovieForm(Model model) {
        model.addAttribute("movie", new Movie());
        return "admin/movie-form";
    }

    // createMovie - создание нового фильма БЕЗ загрузки файла
    @PostMapping
    public String createMovie(@AuthenticationPrincipal UserDetails userDetails,
                              @ModelAttribute("movie") Movie movie,
                              RedirectAttributes redirectAttributes) {

        Optional<User> userOptional = userService.findUserByEmail(userDetails.getUsername());
        if (userOptional.isPresent() && "ADMIN".equals(userOptional.get().getRole())) {
            try {
                // Установите значение по умолчанию для ageRating если null
                if (movie.getAgeRating() == null || movie.getAgeRating().isEmpty()) {
                    movie.setAgeRating("16+");
                }

                // Если releaseDate null, установите сегодняшнюю дату
                if (movie.getReleaseDate() == null) {
                    movie.setReleaseDate(LocalDate.now());
                }

                // Убираем обработку файлов
                movieService.saveMovie(movie);
                redirectAttributes.addFlashAttribute("success", "Фильм успешно создан");

            } catch (Exception e) {
                logger.error("Ошибка создания фильма", e);
                redirectAttributes.addFlashAttribute("error",
                        "Ошибка создания фильма: " + e.getMessage());
                return "redirect:/admin/movies/new";
            }
            return "redirect:/admin/movies";
        }
        return "redirect:/login";
    }

    // showEditMovieForm - отображение формы редактирования фильма
    @GetMapping("/{id}/edit")
    public String showEditMovieForm(@PathVariable Long id, Model model) {
        Optional<Movie> movieOptional = movieService.getMovieById(id);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            logger.debug("Редактирование фильма ID={}", id);
            logger.debug("Название: {}, Постер URL: {}", movie.getTitle(), movie.getPosterUrl());

            model.addAttribute("movie", movie);
            return "admin/movie-form";
        }
        return "redirect:/admin/movies";
    }

    // updateMovie - обновление данных фильма БЕЗ загрузки файла
    @PostMapping("/{id}")
    public String updateMovie(@PathVariable Long id,
                              @ModelAttribute("movie") Movie movie,
                              RedirectAttributes redirectAttributes) {

        logger.debug("Начало обновления фильма ID: {}", id);

        try {
            Optional<Movie> existingMovieOptional = movieService.getMovieById(id);
            if (existingMovieOptional.isPresent()) {
                Movie existingMovie = existingMovieOptional.get();

                // Обновляем поля
                existingMovie.setTitle(movie.getTitle());
                existingMovie.setDescription(movie.getDescription());
                existingMovie.setDurationMinutes(movie.getDurationMinutes());
                existingMovie.setGenre(movie.getGenre());
                existingMovie.setDirector(movie.getDirector());
                existingMovie.setActors(movie.getActors());
                existingMovie.setAgeRating(movie.getAgeRating());

                // Оставляем текущий posterUrl - он уже есть в movie из формы
                existingMovie.setPosterUrl(movie.getPosterUrl());

                // Оставляем текущую дату - она уже есть в movie из формы
                // или можно обновить если нужно:
                existingMovie.setReleaseDate(movie.getReleaseDate());

                movieService.saveMovie(existingMovie);
                redirectAttributes.addFlashAttribute("success", "Фильм успешно обновлен");
                logger.debug("Фильм ID: {} успешно обновлен", id);
            } else {
                redirectAttributes.addFlashAttribute("error", "Фильм не найден");
                logger.warn("Фильм ID: {} не найден", id);
            }
        } catch (Exception e) {
            logger.error("Ошибка обновления фильма ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка обновления фильма: " + e.getMessage());
            return "redirect:/admin/movies/" + id + "/edit";
        }
        return "redirect:/admin/movies";
    }

    // viewMovie - просмотр деталей фильма
    @GetMapping("/{id}")
    public String viewMovie(@PathVariable Long id, Model model) {
        Optional<Movie> movieOptional = movieService.getMovieById(id);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            List<Session> sessions = sessionService.getSessionsByMovie(id);

            model.addAttribute("movie", movie);
            model.addAttribute("sessions", sessions);
            return "admin/movie-details";
        }
        return "redirect:/admin/movies";
    }

    // deleteMovie - удаление фильма
    @PostMapping("/{id}/delete")
    public String deleteMovie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            movieService.deleteMovie(id);
            redirectAttributes.addFlashAttribute("success", "Фильм успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении фильма: " + e.getMessage());
        }
        return "redirect:/admin/movies";
    }
}