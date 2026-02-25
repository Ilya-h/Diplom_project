package org.example.cinema.repository;

import org.example.cinema.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    // findByHallId - поиск всех мест в зале
    // вход: hallId - идентификатор зала
    // выход: список мест в указанном зале
    List<Seat> findByHallId(Long hallId);

    // findByHallIdAndRowNumberAndSeatNumber - поиск места по координатам
    // вход:
    //   - hallId - идентификатор зала
    //   - rowNumber - номер ряда
    //   - seatNumber - номер места
    // выход: Optional<Seat> - место, если найдено
    Optional<Seat> findByHallIdAndRowNumberAndSeatNumber(Long hallId, Integer rowNumber, Integer seatNumber);

    // findAvailableSeatsBySessionId - поиск свободных мест на сеансе
    // вход: sessionId - идентификатор сеанса
    // выход: список свободных мест
    @Query("SELECT s FROM Seat s WHERE s.hall.id = (SELECT ses.hall.id FROM Session ses WHERE ses.id = :sessionId) " +
            "AND s.isAvailable = true")
    List<Seat> findAvailableSeatsBySessionId(@Param("sessionId") Long sessionId);

    // findOccupiedSeatsBySessionId - поиск занятых мест на сеансе
    // вход: sessionId - идентификатор сеанса
    // выход: список занятых мест
    @Query("SELECT s FROM Seat s JOIN Ticket t ON s.id = t.seat.id " +
            "WHERE t.session.id = :sessionId AND t.status = 'PURCHASED'")
    List<Seat> findOccupiedSeatsBySessionId(@Param("sessionId") Long sessionId);

    // countByHallIdAndSeatType - подсчет мест по типу в зале
    // вход:
    //   - hallId - идентификатор зала
    //   - seatType - тип места
    // выход: количество мест указанного типа
    Long countByHallIdAndSeatType(Long hallId, String seatType);

    List<Seat> findBySessionIdAndIsAvailable(Long sessionId, boolean isAvailable);
}