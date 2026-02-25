package org.example.cinema.service;

import org.example.cinema.dto.*;
import org.example.cinema.model.*;
import org.example.cinema.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final SessionService sessionService;
    private final SeatService seatService;
    private final PaymentService paymentService;

    @Autowired
    public TicketService(TicketRepository ticketRepository, UserService userService,
                         SessionService sessionService, SeatService seatService,
                         PaymentService paymentService) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.sessionService = sessionService;
        this.seatService = seatService;
        this.paymentService = paymentService;
    }

    // getTicketsByUser - получение билетов пользователя
    public List<Ticket> getTicketsByUser(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    // getTicketById - получение билета по ID
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    // saveTicket - сохранение билета
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    // purchaseTicket - покупка билета
    public Ticket purchaseTicket(Long userId, Long sessionId, Long seatId, String paymentMethod) {
        logger.info("Purchase attempt - User: {}, Session: {}, Seat: {}",
                userId, sessionId, seatId);
        Optional<User> userOptional = userService.findUserById(userId);
        Optional<Session> sessionOptional = sessionService.getSessionById(sessionId);
        Optional<Seat> seatOptional = seatService.getSeatById(seatId);

        if (userOptional.isEmpty() || sessionOptional.isEmpty() || seatOptional.isEmpty()) {
            throw new RuntimeException("Invalid user, session or seat");
        }

        Session session = sessionOptional.get();
        Seat seat = seatOptional.get();

        // Проверка доступности места
        if (!seat.getIsAvailable()) {
            throw new RuntimeException("Место уже занято");
        }

        // Проверка, не занято ли уже место на этот сеанс
        if (ticketRepository.existsBySessionIdAndSeatIdAndStatus(sessionId, seatId, "PURCHASED")) {
            throw new RuntimeException("Место уже занято для этого сеанса");
        }

        // Создание билета
        Ticket ticket = new Ticket();
        ticket.setUser(userOptional.get());
        ticket.setSession(session);
        ticket.setSeat(seat);
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setPurchaseDate(LocalDateTime.now());

        // РАСЧЕТ ЦЕНЫ ПО ТИПУ МЕСТА
        double price = calculateSeatPrice(sessionId, seat.getSeatType());
        ticket.setPrice(price);

        ticket.setStatus("RESERVED");

        // Создание платежа
        Payment payment = paymentService.createPayment(userOptional.get(),
                paymentMethod, ticket.getPrice());
        ticket.setPayment(payment);

        // Сохранение билета
        Ticket savedTicket = saveTicket(ticket);

        // ОБНОВЛЕНИЕ МЕСТА: помечаем как занятое
        seat.setIsAvailable(false);
        seat.setSession(session); // связываем место с сеансом
        seatService.saveSeat(seat);

        // Подтверждение платежа
        paymentService.confirmPayment(payment.getId());

        // Обновление статуса билета
        savedTicket.setStatus("PURCHASED");
        return saveTicket(savedTicket);
    }

    // reserveTicket - резервирование билета
    public Ticket reserveTicket(Long userId, Long sessionId, Long seatId) {
        Optional<User> userOptional = userService.findUserById(userId);
        Optional<Session> sessionOptional = sessionService.getSessionById(sessionId);
        Optional<Seat> seatOptional = seatService.getSeatById(seatId);

        if (userOptional.isEmpty() || sessionOptional.isEmpty() || seatOptional.isEmpty()) {
            throw new RuntimeException("Invalid user, session or seat");
        }

        // Проверка доступности места
        if (!seatOptional.get().getIsAvailable()) {
            throw new RuntimeException("Seat is not available");
        }

        // Создание билета
        Ticket ticket = new Ticket();
        ticket.setUser(userOptional.get());
        ticket.setSession(sessionOptional.get());
        ticket.setSeat(seatOptional.get());
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setPrice(sessionOptional.get().getPrice());
        ticket.setStatus("RESERVED");

        // Обновление статуса места
        seatService.updateSeatAvailability(seatId, false);

        return saveTicket(ticket);
    }

    // cancelTicket - отмена билета
    public void cancelTicket(Long ticketId) {
        Optional<Ticket> ticketOptional = getTicketById(ticketId);
        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();
            ticket.setStatus("CANCELLED");

            // Возврат места в доступные
            seatService.updateSeatAvailability(ticket.getSeat().getId(), true);

            // Возврат денег при необходимости
            if (ticket.getPayment() != null) {
                paymentService.refundPayment(ticket.getPayment().getId());
            }

            saveTicket(ticket);
        }
    }

    // getAvailableSeatsForSession - получение ВСЕХ мест для сеанса (и доступных, и занятых)
    public List<SeatSelectionDTO> getAvailableSeatsForSession(Long sessionId) {
        Optional<Session> sessionOptional = sessionService.getSessionById(sessionId);
        if (sessionOptional.isEmpty()) {
            return new ArrayList<>();
        }

        Session session = sessionOptional.get();
        if (session.getHall() == null) {
            return new ArrayList<>();
        }

        // Получаем все места зала
        List<Seat> allSeats = seatService.getSeatsByHall(session.getHall().getId());

        // Преобразуем в DTO с правильным статусом доступности
        return allSeats.stream()
                .map(seat -> {
                    SeatSelectionDTO dto = new SeatSelectionDTO();
                    dto.setSeatId(seat.getId());
                    dto.setRowNumber(seat.getRowNumber());
                    dto.setSeatNumber(seat.getSeatNumber());
                    dto.setSeatType(seat.getSeatType());

                    // Определяем, доступно ли место для этого сеанса
                    boolean isAvailable = !ticketRepository.existsBySessionIdAndSeatIdAndStatus(
                            sessionId, seat.getId(), "PURCHASED");
                    dto.setIsAvailable(isAvailable);

                    // Рассчитываем цену
                    dto.setPrice(calculateSeatPrice(sessionId, seat.getSeatType()));
                    dto.setStatus(isAvailable ? "AVAILABLE" : "OCCUPIED");

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // calculateSeatPrice - расчет цены места (делаем публичным для контроллера)
    public Double calculateSeatPrice(Long sessionId, String seatType) {
        Optional<Session> sessionOptional = sessionService.getSessionById(sessionId);
        if (sessionOptional.isEmpty()) {
            return 0.0;
        }

        double basePrice = sessionOptional.get().getPrice();
        double multiplier = getSeatTypeMultiplier(seatType);

        return basePrice * multiplier;
    }

    // getSeatTypeMultiplier - получение множителя цены по типу места
    private double getSeatTypeMultiplier(String seatType) {
        // Исправляем множители согласно ценам из HTML:
        // Стандарт: 250 ₽ (множитель 1.0)
        // VIP: 650 ₽ (множитель = 650/250 = 2.6)
        // Парные: 950 ₽ (множитель = 950/250 = 3.8)
        // Для инвалидов: 0 ₽ (множитель 0)
        if ("VIP".equals(seatType)) {
            return 2.6; // 650 / 250
        } else if ("COUPLE".equals(seatType)) {
            return 3.8; // 950 / 250
        } else if ("DISABLED".equals(seatType)) {
            return 0.0; // бесплатно
        } else {
            return 1.0; // STANDARD
        }
    }

    // generateTicketNumber - генерация номера билета
    private String generateTicketNumber() {
        return "TKT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // getCustomerStats - получение статистики клиента
    public Optional<CustomerStatsDTO> getCustomerStats(Long userId) {
        return ticketRepository.findCustomerStatsByUserId(userId);
    }

    // getMovieStats - получение статистики по фильму
    public Optional<MovieStatsDTO> getMovieStats(Long movieId) {
        return ticketRepository.findMovieStatsByMovieId(movieId);
    }

    // getSessionStats - получение статистики по сеансу
    public Optional<SessionStatsDTO> getSessionStats(Long sessionId) {
        return ticketRepository.findSessionStatsBySessionId(sessionId);
    }

    // getDailySalesStats - получение дневной статистики продаж
    public List<DailySalesDTO> getDailySalesStats(LocalDateTime date) {
        return ticketRepository.findDailySalesStats(date);
    }

    // getPopularMovies - получение популярных фильмов
    public List<PopularMovieDTO> getPopularMovies(int limit) {
        return ticketRepository.findPopularMovies(limit);
    }

    // getTicketDetails - получение деталей билетов пользователя
    public List<TicketDetailsDTO> getTicketDetails(Long userId) {
        return ticketRepository.findCustomerTicketsWithDetails(userId);
    }
    // getTodayMovieSales - получение продаж по фильмам за сегодня
    public List<PopularMovieDTO> getTodayMovieSales() {
        return ticketRepository.findTodayMovieSales();
    }

    // Вспомогательный метод (временно)
    private List<PopularMovieDTO> getMoviesSalesForToday() {
        // Получаем все билеты за сегодня
        LocalDateTime today = LocalDateTime.now();
        List<DailySalesDTO> todayStats = ticketRepository.findDailySalesStats(today);

        // Это заглушка - в реальности нужен отдельный запрос
        // Но для быстрого исправления можно вернуть пустой список
        return new ArrayList<>();
    }

    // updateSeatStatusForSession - обновление статуса места для сеанса
    public void updateSeatStatusForSession(Long sessionId, Long seatId, boolean isAvailable) {
        Optional<Session> sessionOptional = sessionService.getSessionById(sessionId);
        Optional<Seat> seatOptional = seatService.getSeatById(seatId);

        if (sessionOptional.isPresent() && seatOptional.isPresent()) {
            Seat seat = seatOptional.get();
            // Обновляем статус места в контексте сеанса
            // Место становится недоступным для этого сеанса
            // но может быть доступным для других сеансов

            // Проверяем, есть ли уже билет на это место для этого сеанса
            boolean hasTicket = ticketRepository.existsBySessionIdAndSeatIdAndStatus(
                    sessionId, seatId, "PURCHASED");

            if (!isAvailable && !hasTicket) {
                // Помечаем место как занятое для этого сеанса
                // Создаем запись о занятости (можно добавить флаг в Seat или отдельную таблицу)
                seat.setSession(sessionOptional.get());
                seat.setIsAvailable(false);
                seatService.saveSeat(seat);
            }
        }
    }
}