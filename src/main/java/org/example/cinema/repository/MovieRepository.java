package org.example.cinema.repository;

import org.example.cinema.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {


    // findById - поиск фильма по Id
    Optional<Movie> findById(Long id);
    // findByReleaseDateAfter - поиск фильмов с датой выхода после указанной
    // вход: date - дата для фильтрации
    // выход: список фильмов с датой выхода после указанной
    List<Movie> findByReleaseDateAfter(LocalDate date);

    // findByGenreContainingIgnoreCase - поиск фильмов по жанру
    // вход: genre - жанр для поиска
    // выход: список фильмов, содержащих указанный жанр
    List<Movie> findByGenreContainingIgnoreCase(String genre);

    // findByTitleContainingIgnoreCase - поиск фильмов по названию
    // вход: title - часть названия для поиска
    // выход: список фильмов, содержащих указанный текст в названии
    List<Movie> findByTitleContainingIgnoreCase(String title);

    // findMoviesWithActiveSessions - поиск фильмов с активными сеансами
    // вход: currentDate - текущая дата и время
    // выход: список фильмов, у которых есть сеансы после указанной даты
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.sessions s WHERE s.startTime > :currentDate ORDER BY m.releaseDate DESC")
    List<Movie> findMoviesWithActiveSessions(@Param("currentDate") LocalDateTime currentDate);

    // existsByTitleAndReleaseDate - проверка уникальности фильма
    // вход:
    //   - title - название фильма
    //   - releaseDate - дата выхода
    // выход: true - если фильм с таким названием и датой выхода уже существует
    boolean existsByTitleAndReleaseDate(String title, LocalDateTime releaseDate);
}