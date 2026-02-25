package org.example.cinema.service;

import org.example.cinema.model.Session;
import org.example.cinema.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SessionService {
    private final SessionRepository sessionRepository;
    private final MovieService movieService;
    private final CinemaHallService cinemaHallService;

    @Autowired
    public SessionService(SessionRepository sessionRepository, MovieService movieService,
                          CinemaHallService cinemaHallService) {
        this.sessionRepository = sessionRepository;
        this.movieService = movieService;
        this.cinemaHallService = cinemaHallService;
    }

    // getSessionById - получение сеанса по ID с загрузкой movie и hall
    @Transactional(readOnly = true)
    public Optional<Session> getSessionById(Long id) {
        // Сначала пробуем найти через специальный метод
        Optional<Session> sessionOpt = sessionRepository.findWithMovieAndHallById(id);

        // Если не нашли, пробуем обычный поиск
        if (sessionOpt.isEmpty()) {
            sessionOpt = sessionRepository.findById(id);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                // Инициализируем связи
                if (session.getMovie() != null) {
                    // Принудительно загружаем фильм
                    session.getMovie().getTitle(); // Просто вызываем геттер
                }
                if (session.getHall() != null) {
                    session.getHall().getName(); // Просто вызываем геттер
                }
            }
        }

        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();

            // ГАРАНТИРУЕМ, ЧТО ВСЕ ПОЛЯ БУДУТ ЗАПОЛНЕНЫ
            if (session.getPrice() == null) {
                session.setPrice(0.0);
            }
            if (session.getFormat() == null) {
                session.setFormat("2D");
            }
            if (session.getEndTime() == null && session.getStartTime() != null && session.getMovie() != null) {
                if (session.getMovie().getDurationMinutes() != null) {
                    session.setEndTime(session.getStartTime().plusMinutes(session.getMovie().getDurationMinutes()));
                }
            }

            // Отладочная информация - ВКЛЮЧИТЬ ДЛЯ ПРОВЕРКИ
            System.out.println("=== ДАННЫЕ СЕАНСА ===");
            System.out.println("ID: " + session.getId());
            System.out.println("Фильм: " + (session.getMovie() != null ? session.getMovie().getTitle() : "null"));
            System.out.println("Зал: " + (session.getHall() != null ? session.getHall().getName() : "null"));
            System.out.println("Цена: " + session.getPrice());
            System.out.println("Формат: " + session.getFormat());
            System.out.println("Время начала: " + session.getStartTime());
            System.out.println("Время окончания: " + session.getEndTime());

            // Проверяем фильм
            if (session.getMovie() != null) {
                System.out.println("=== ФИЛЬМ ===");
                System.out.println("Название: " + session.getMovie().getTitle());
                System.out.println("Жанр: " + session.getMovie().getGenre());
                System.out.println("Режиссер: " + session.getMovie().getDirector());
            } else {
                System.out.println("=== ВНИМАНИЕ: Фильм равен null! ===");
            }

            // Проверяем зал
            if (session.getHall() != null) {
                System.out.println("=== ЗАЛ ===");
                System.out.println("Название: " + session.getHall().getName());
                System.out.println("Тип: " + session.getHall().getHallType());
                System.out.println("Вместимость: " + session.getHall().getCapacity());
            } else {
                System.out.println("=== ВНИМАНИЕ: Зал равен null! ===");
            }
        } else {
            System.out.println("=== ОШИБКА: Сеанс с ID " + id + " не найден! ===");
        }

        return sessionOpt;
    }

    // Другие методы остаются без изменений
    // getSessionsByMovie - получение сеансов по фильму
    public List<Session> getSessionsByMovie(Long movieId) {
        return sessionRepository.findByMovieId(movieId);
    }

    // saveSession - сохранение сеанса
    public Session saveSession(Session session) {
        return sessionRepository.save(session);
    }

    // deleteSession - удаление сеанса
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    // getAllSessions - получение всех сеансов с загрузкой movie и hall
    @Transactional(readOnly = true)
    public List<Session> getAllSessions() {
        // findAll() теперь использует @EntityGraph
        List<Session> sessions = sessionRepository.findAll();

        // Отладочная информация
        System.out.println("Всего сеансов: " + sessions.size());
        sessions.forEach(session -> {
            System.out.println("Сеанс ID: " + session.getId() +
                    ", Фильм: " + (session.getMovie() != null ? session.getMovie().getTitle() : "null") +
                    ", Цена: " + session.getPrice());
        });

        return sessions;
    }

    // getSessionsByDateRange - получение сеансов по диапазону дат
    public List<Session> getSessionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return sessionRepository.findByStartTimeBetween(start, end);
    }

    // createSession - создание нового сеанса
    public Session createSession(Long movieId, Long hallId, LocalDateTime startTime,
                                 Double price, String format) {
        var movieOptional = movieService.getMovieById(movieId);
        var hallOptional = cinemaHallService.getCinemaHallById(hallId);

        if (movieOptional.isEmpty() || hallOptional.isEmpty()) {
            throw new RuntimeException("Movie or hall not found");
        }

        Session session = new Session();
        session.setMovie(movieOptional.get());
        session.setHall(hallOptional.get());
        session.setStartTime(startTime);

        // Устанавливаем время окончания
        Integer duration = movieOptional.get().getDurationMinutes();
        if (duration != null) {
            session.setEndTime(startTime.plusMinutes(duration));
        } else {
            session.setEndTime(startTime.plusHours(2)); // Значение по умолчанию
        }

        session.setPrice(price != null ? price : 0.0);
        session.setFormat(format != null && !format.isEmpty() ? format : "2D");

        return saveSession(session);
    }

    // checkSessionAvailability - проверка доступности сеанса
    public boolean checkSessionAvailability(Long sessionId) {
        Optional<Session> sessionOptional = getSessionById(sessionId);
        if (sessionOptional.isEmpty()) {
            return false;
        }

        Session session = sessionOptional.get();
        // Проверка, что сеанс еще не начался
        return session.getStartTime().isAfter(LocalDateTime.now());
    }

    // getSessionsForToday - получение сеансов на сегодня
    public List<Session> getSessionsForToday() {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        return getSessionsByDateRange(todayStart, todayEnd);
    }

    // getFutureSessionsByMovie - получение БУДУЩИХ сеансов по фильму
    public List<Session> getFutureSessionsByMovie(Long movieId) {
        LocalDateTime now = LocalDateTime.now();
        return sessionRepository.findByMovieIdAndStartTimeAfter(movieId, now);
    }

    @Transactional(readOnly = true)
    public Optional<Session> getSessionWithDetails(Long id) {
        return sessionRepository.findByIdWithDetails(id);
    }
}