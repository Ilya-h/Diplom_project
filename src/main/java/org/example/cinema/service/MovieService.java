package org.example.cinema.service;

import org.example.cinema.model.Movie;
import org.example.cinema.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // findById - поиск фильма по Id
    public Movie findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Фильм не найден с id: " + id));
    }

    // getAllMovies - получение всех фильмов
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // getMovieById - получение фильма по ID
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    // saveMovie - сохранение фильма
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // deleteMovie - удаление фильма
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    // getNowPlayingMovies - получение фильмов, которые сейчас идут
    public List<Movie> getNowPlayingMovies() {
        LocalDate now = LocalDate.now();
        // Показываем фильмы, которые:
        // 1. Уже вышли (релиз был до сегодня) ИЛИ
        // 2. Выходят в ближайшие 3 месяца
        LocalDate threeMonthsAgo = now.minusMonths(3);
        LocalDate threeMonthsLater = now.plusMonths(3);

        return movieRepository.findAll().stream()
                .filter(movie -> {
                    LocalDate releaseDate = movie.getReleaseDate();
                    return releaseDate.isAfter(threeMonthsAgo) &&
                            releaseDate.isBefore(threeMonthsLater);
                })
                .collect(Collectors.toList());
    }

    // getComingSoonMovies - получение фильмов, которые скоро выйдут
    public List<Movie> getComingSoonMovies() {
        LocalDate now = LocalDate.now();
        return movieRepository.findByReleaseDateAfter(now);
    }

    // searchMovies - поиск фильмов
    public List<Movie> searchMovies(String query) {
        return movieRepository.findByTitleContainingIgnoreCase(query);
    }

    // getMoviesByGenre - получение фильмов по жанру
    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findByGenreContainingIgnoreCase(genre);
    }

    // getMoviesWithActiveSessions - получение фильмов с активными сеансами
    public List<Movie> getMoviesWithActiveSessions() {
        return movieRepository.findMoviesWithActiveSessions(LocalDateTime.now());
    }

    // isMovieUnique - проверка уникальности фильма
    public boolean isMovieUnique(String title, LocalDateTime releaseDate) {
        return !movieRepository.existsByTitleAndReleaseDate(title, releaseDate);
    }

    // updateMovieRating - обновление рейтинга фильма
    public void updateMovieRating(Long movieId, Double newRating) {
        Optional<Movie> movieOptional = getMovieById(movieId);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            // Здесь можно добавить логику расчета рейтинга
            // Пока просто сохраняем
            saveMovie(movie);
        }
    }
}