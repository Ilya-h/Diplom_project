package org.example.cinema.repository;

import org.example.cinema.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    // findByHallId - поиск сеансов по залу
    List<Session> findByHallId(Long hallId);

    // findActiveSessions - поиск активных сеансов (будущих)
    @Query("SELECT s FROM Session s WHERE s.startTime > :currentTime ORDER BY s.startTime ASC")
    List<Session> findActiveSessions(@Param("currentTime") LocalDateTime currentTime);

    // deleteByMovieId - удаление всех сеансов по фильму
    void deleteByMovieId(Long movieId);

    // deleteByHallId - удаление всех сеансов по залу
    void deleteByHallId(Long hallId);

    // Метод с загрузкой связанных сущностей
    @EntityGraph(attributePaths = {"movie", "hall"})
    Optional<Session> findWithMovieAndHallById(Long id);

    // Метод для получения всех сеансов с загруженными связями
    @EntityGraph(attributePaths = {"movie", "hall"})
    @Override
    List<Session> findAll();

    // findByMovieId - поиск сеансов по фильму
    List<Session> findByMovieId(Long movieId);

    // findByStartTimeBetween - поиск сеансов в временном диапазоне
    List<Session> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // findByMovieIdAndStartTimeAfter - будущие сеансы
    List<Session> findByMovieIdAndStartTimeAfter(Long movieId, LocalDateTime now);

    @Query("SELECT s FROM Session s " +
            "LEFT JOIN FETCH s.movie " +
            "LEFT JOIN FETCH s.hall " +
            "WHERE s.id = :id")
    Optional<Session> findByIdWithDetails(@Param("id") Long id);

    // Или если не хотите создавать отдельный метод,
// можно использовать стандартный с аннотацией @EntityGraph:
    @EntityGraph(attributePaths = {"movie", "hall"})
    Optional<Session> findWithDetailsById(Long id);

}