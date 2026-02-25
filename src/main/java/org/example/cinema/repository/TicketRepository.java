package org.example.cinema.repository;

import org.example.cinema.dto.*;
import org.example.cinema.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // findByUserId - поиск всех билетов пользователя
    // вход: userId - идентификатор пользователя
    // выход: список билетов пользователя
    List<Ticket> findByUserId(Long userId);

    // findBySessionId - поиск всех билетов на сеанс
    // вход: sessionId - идентификатор сеанса
    // выход: список билетов на указанный сеанс
    List<Ticket> findBySessionId(Long sessionId);

    // findByUserIdAndSessionId - поиск билетов пользователя на конкретный сеанс
    // вход:
    //   - userId - идентификатор пользователя
    //   - sessionId - идентификатор сеанса
    // выход: список билетов пользователя на указанный сеанс
    List<Ticket> findByUserIdAndSessionId(Long userId, Long sessionId);

    // existsBySessionIdAndSeatIdAndStatus - проверка занятости места на сеансе
    // вход:
    //   - sessionId - идентификатор сеанса
    //   - seatId - идентификатор места
    //   - status - статус билета
    // выход: true - если место уже занято билетом с указанным статусом
    boolean existsBySessionIdAndSeatIdAndStatus(Long sessionId, Long seatId, String status);

    // findByStatusAndPurchaseDateBefore - поиск билетов по статусу и дате покупки
    // вход:
    //   - status - статус билета
    //   - purchaseDate - предельная дата покупки
    // выход: список билетов, соответствующих критериям
    List<Ticket> findByStatusAndPurchaseDateBefore(String status, LocalDateTime purchaseDate);

    // findCustomerStatsByUserId - получение статистики покупок пользователя
    // вход: userId - идентификатор пользователя
    // выход: DTO со статистикой покупок пользователя
    @Query("SELECT new org.example.cinema.dto.CustomerStatsDTO(" +
            "u.id, u.firstName, u.lastName, COUNT(t), SUM(t.price), AVG(t.price), MAX(t.purchaseDate)) " +
            "FROM User u LEFT JOIN Ticket t ON u.id = t.user.id AND t.status = 'PURCHASED' " +
            "WHERE u.id = :userId " +
            "GROUP BY u.id, u.firstName, u.lastName")
    Optional<CustomerStatsDTO> findCustomerStatsByUserId(@Param("userId") Long userId);

    // findMovieStatsByMovieId - получение статистики по фильму
    // вход: movieId - идентификатор фильма
    // выход: DTO со статистикой продаж по фильму
    @Query("SELECT new org.example.cinema.dto.MovieStatsDTO(" +
            "m.id, m.title, COUNT(t), SUM(t.price), AVG(t.price), MIN(s.startTime), MAX(s.startTime)) " +
            "FROM Movie m " +
            "LEFT JOIN Session s ON m.id = s.movie.id " +
            "LEFT JOIN Ticket t ON s.id = t.session.id AND t.status = 'PURCHASED' " +
            "WHERE m.id = :movieId " +
            "GROUP BY m.id, m.title")
    Optional<MovieStatsDTO> findMovieStatsByMovieId(@Param("movieId") Long movieId);

    // findSessionStatsBySessionId - получение статистики по сеансу
    // ВХОД: sessionId - идентификатор сеанса
    // ВЫХОД: DTO со статистикой сеанса
    @Query("SELECT new org.example.cinema.dto.SessionStatsDTO(" +
            "s.id, m.title, s.startTime, " +
            "COUNT(CASE WHEN t.status = 'PURCHASED' THEN 1 END), " +
            "COALESCE(SUM(CASE WHEN t.status = 'PURCHASED' THEN t.price END), 0.0), " +
            "COUNT(DISTINCT CASE WHEN t.status = 'PURCHASED' THEN t.seat.id END), " +
            "h.totalSeats - COUNT(DISTINCT CASE WHEN t.status = 'PURCHASED' THEN t.seat.id END)) " +
            "FROM Session s " +
            "JOIN s.movie m " +
            "JOIN s.hall h " +
            "LEFT JOIN Ticket t ON s.id = t.session.id " +
            "WHERE s.id = :sessionId " +
            "GROUP BY s.id, m.title, s.startTime, h.totalSeats")
    Optional<SessionStatsDTO> findSessionStatsBySessionId(@Param("sessionId") Long sessionId);

    // findDailySalesStats - получение статистики продаж за день
    // ВХОД: date - дата для анализа
    // ВЫХОД: список DTO с дневной статистикой
    @Query("SELECT new org.example.cinema.dto.DailySalesDTO(" +
            "CAST(t.purchaseDate AS java.time.LocalDate), " +
            "COUNT(t), " +
            "COALESCE(SUM(t.price), 0.0), " +
            "COALESCE(AVG(t.price), 0.0)) " +
            "FROM Ticket t " +
            "WHERE CAST(t.purchaseDate AS java.time.LocalDate) = CAST(:date AS java.time.LocalDate) " +
            "AND t.status = 'PURCHASED' " +
            "GROUP BY CAST(t.purchaseDate AS java.time.LocalDate)")
    List<DailySalesDTO> findDailySalesStats(@Param("date") LocalDateTime date);

    // findPopularMovies - получение списка популярных фильмов
    // вход: limit - ограничение количества результатов
    // выход: список DTO с популярными фильмами
    @Query("SELECT new org.example.cinema.dto.PopularMovieDTO(" +
            "m.id, m.title, COALESCE(COUNT(t), 0), COALESCE(SUM(t.price), 0.0)) " +
            "FROM Movie m " +
            "LEFT JOIN Session s ON m.id = s.movie.id " +
            "LEFT JOIN Ticket t ON s.id = t.session.id AND t.status = 'PURCHASED' " +
            "GROUP BY m.id, m.title " +
            "ORDER BY COALESCE(COUNT(t), 0) DESC")
    List<PopularMovieDTO> findPopularMovies(@Param("limit") int limit);

    // findCustomerTicketsWithDetails - получение детальной информации о билетах пользователя
    // ВХОД: userId - идентификатор пользователя
    // ВЫХОД: список DTO с детальной информацией о билетах
    @Query("SELECT new org.example.cinema.dto.TicketDetailsDTO(" +
            "t.id, t.ticketNumber, m.title, s.startTime, h.name, " +
            "st.rowNumber, st.seatNumber, st.seatType, t.price, t.status, t.purchaseDate) " +
            "FROM Ticket t " +
            "JOIN t.session s " +
            "JOIN s.movie m " +
            "JOIN s.hall h " +
            "JOIN t.seat st " +
            "WHERE t.user.id = :userId " +
            "ORDER BY t.purchaseDate DESC")
    List<TicketDetailsDTO> findCustomerTicketsWithDetails(@Param("userId") Long userId);

    // countPurchasedTicketsByUserId - подсчет количества купленных билетов пользователем
    // вход: userId - идентификатор пользователя
    // выход: количество купленных билетов
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.user.id = :userId AND t.status = 'PURCHASED'")
    Long countPurchasedTicketsByUserId(@Param("userId") Long userId);

    // calculateTotalRevenue - расчет общей выручки
    // выход: общая выручка от всех проданных билетов
    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.status = 'PURCHASED'")
    Double calculateTotalRevenue();

    // findTicketsByPurchaseDateBetween - поиск билетов по диапазону дат покупки
    // вход:
    //   - startDate - начальная дата
    //   - endDate - конечная дата
    // выход: список билетов, купленных в указанный период
    List<Ticket> findByPurchaseDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Подсчет уникальных фильмов, которые посетил пользователь
    @Query("SELECT COUNT(DISTINCT s.movie.id) FROM Ticket t " +
            "JOIN t.session s " +
            "WHERE t.user.id = :userId AND t.status = 'PURCHASED'")
    Long countVisitedMoviesByUserId(@Param("userId") Long userId);

    // Подсчет сеансов для фильма
    @Query("SELECT COUNT(s) FROM Session s WHERE s.movie.id = :movieId")
    Long countTotalSessionsByMovieId(@Param("movieId") Long movieId);

    // findTodayMovieSales - получение продаж по фильмам за сегодня
    @Query("SELECT new org.example.cinema.dto.PopularMovieDTO(" +
            "m.id, m.title, COUNT(t), COALESCE(SUM(t.price), 0.0)) " +
            "FROM Ticket t " +
            "JOIN t.session s " +
            "JOIN s.movie m " +
            "WHERE DATE(t.purchaseDate) = CURRENT_DATE " +
            "AND t.status = 'PURCHASED' " +
            "GROUP BY m.id, m.title " +
            "ORDER BY COUNT(t) DESC")
    List<PopularMovieDTO> findTodayMovieSales();
}