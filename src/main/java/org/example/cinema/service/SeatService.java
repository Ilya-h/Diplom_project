package org.example.cinema.service;

import org.example.cinema.model.CinemaHall;
import org.example.cinema.model.Seat;
import org.example.cinema.model.Session;
import org.example.cinema.repository.SeatRepository;
import org.example.cinema.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final CinemaHallService cinemaHallService;
    private final SessionService sessionService;

    @Autowired
    public SeatService(SeatRepository seatRepository, TicketRepository ticketRepository, CinemaHallService cinemaHallService, SessionService sessionService) {
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.cinemaHallService = cinemaHallService;
        this.sessionService = sessionService;
    }

    // getSeatsByHall - получение всех мест по залу (и доступные, и занятые)
    public List<Seat> getSeatsByHall(Long hallId) { return seatRepository.findByHallId(hallId); }

    // getSeatsBySession - получение всех мест для сеанса с определением доступности
    public List<Seat> getSeatsBySession(Long sessionId) {
        Optional<Session> sessionOptional = sessionService.getSessionById(sessionId);
        if (sessionOptional.isEmpty()) {
            throw new RuntimeException("Сеанс не найден: " + sessionId);
        }

        Session session = sessionOptional.get();
        if (session.getHall() == null) {
            throw new RuntimeException("Для сеанса не указан зал");
        }

        // Получаем ВСЕ места зала
        List<Seat> allSeats = seatRepository.findByHallId(session.getHall().getId());

        // Обновляем статус isAvailable для каждого места на основе купленных билетов
        for (Seat seat : allSeats) {
            // Проверяем, есть ли купленный билет на это место для этого сеанса
            boolean isOccupied = ticketRepository.existsBySessionIdAndSeatIdAndStatus(
                    sessionId, seat.getId(), "PURCHASED");

            // Если есть купленный билет, помечаем место как занятое
            if (isOccupied) {
                seat.setIsAvailable(false);
            }
        }

        return allSeats;
    }

    // getSeatById - получение места по ID
    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }

    // saveSeat - сохранение места
    public Seat saveSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    // deleteSeat - удаление места
    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }

    // createSeatsForHall - создание мест для зала
    public void createSeatsForHall(Long hallId, int rows, int seatsPerRow) {
        Optional<CinemaHall> hallOptional = cinemaHallService.getCinemaHallById(hallId);
        if (hallOptional.isEmpty()) {
            throw new RuntimeException("Зал не найден: " + hallId);
        }
        CinemaHall hall = hallOptional.get();
        for (int row = 1; row <= rows; row++) {
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                Seat seat = new Seat();
                seat.setHall(hall);
                seat.setRowNumber(row);
                seat.setSeatNumber(seatNum);
                seat.setSeatType(determineSeatType(row, seatNum, rows, seatsPerRow));
                seat.setIsAvailable(true);
                saveSeat(seat);
            }
        }
    }

    // findAvailableSeatsBySessionId - поиск доступных мест на сеанс
    public List<Seat> findAvailableSeatsBySessionId(Long sessionId) {
        // Важно: фильтровать по session_id и is_available
        return seatRepository.findBySessionIdAndIsAvailable(sessionId, true);
    }

    // findOccupiedSeatsBySessionId - поиск занятых мест на сеанс
    public List<Seat> findOccupiedSeatsBySessionId(Long sessionId) {
        return seatRepository.findOccupiedSeatsBySessionId(sessionId);
    }

    // updateSeatAvailability - обновление доступности места
    public void updateSeatAvailability(Long seatId, boolean isAvailable) {
        Optional<Seat> seatOptional = getSeatById(seatId);
        if (seatOptional.isPresent()) {
            Seat seat = seatOptional.get();
            seat.setIsAvailable(isAvailable);
            saveSeat(seat);
        }
    }

    // determineSeatType - определение типа места
    private String determineSeatType(int row, int seatNum, int totalRows, int totalSeatsPerRow) {
        // VIP места в первых 3 рядах
        if (row <= 3) return "VIP";

        // Места для пар в центральных рядах
        if (row >= totalRows - 2 && seatNum >= totalSeatsPerRow/2 - 1 && seatNum <= totalSeatsPerRow/2 + 1) {
            return "COUPLE";
        }

        // Места для инвалидов в первом ряду по краям
        if (row == 1 && (seatNum <= 2 || seatNum >= totalSeatsPerRow - 1)) {
            return "DISABLED";
        }

        return "STANDARD";
    }

    // markSeatsAsOccupied - пометить места как занятые для сеанса
    public void markSeatsAsOccupied(List<Long> seatIds, Long sessionId) {
        List<Seat> seats = seatRepository.findAllById(seatIds);
        Optional<Session> sessionOptional = sessionService.getSessionById(sessionId);

        if (sessionOptional.isPresent()) {
            for (Seat seat : seats) {
                // Устанавливаем связь с сеансом
                seat.setSession(sessionOptional.get());
                // Помечаем как занятое
                seat.setIsAvailable(false);

                // Также можно создать запись в Ticket без пользователя
                // для отслеживания занятости мест
            }
            seatRepository.saveAll(seats);
        }
    }

    // areSeatsAvailable - проверка доступности мест
    public boolean areSeatsAvailable(List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);
        for (Seat seat : seats) {
            if (seat == null || !seat.getIsAvailable()) {
                return false;
            }
        }
        return true;
    }
}