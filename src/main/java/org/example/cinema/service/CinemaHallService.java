package org.example.cinema.service;

import org.example.cinema.model.CinemaHall;
import org.example.cinema.repository.CinemaHallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CinemaHallService {
    private final CinemaHallRepository cinemaHallRepository;

    @Autowired
    public CinemaHallService(CinemaHallRepository cinemaHallRepository) {
        this.cinemaHallRepository = cinemaHallRepository;
    }

    // getAllCinemaHalls - получение всех кинозалов
    public List<CinemaHall> getAllCinemaHalls() {
        return cinemaHallRepository.findAll();
    }

    // getCinemaHallById - получение кинозала по ID
    public Optional<CinemaHall> getCinemaHallById(Long id) {
        return cinemaHallRepository.findById(id);
    }

    // saveCinemaHall - сохранение кинозала
    public CinemaHall saveCinemaHall(CinemaHall cinemaHall) {
        return cinemaHallRepository.save(cinemaHall);
    }

    // deleteCinemaHall - удаление кинозала
    public void deleteCinemaHall(Long id) {
        cinemaHallRepository.deleteById(id);
    }

    // isHallNameUnique - проверка уникальности названия зала
    public boolean isHallNameUnique(String name) {
        return cinemaHallRepository.findByName(name).isEmpty();
    }

    // createHallLayout - создание расположения мест в зале
    public void createHallLayout(Long hallId, int rowsCount, int seatsPerRow) {
        Optional<CinemaHall> hallOptional = getCinemaHallById(hallId);
        if (hallOptional.isPresent()) {
            CinemaHall hall = hallOptional.get();
            // Здесь можно добавить логику создания мест
            // Пока просто устанавливаем общее количество мест
            hall.setTotalSeats(rowsCount * seatsPerRow);
            saveCinemaHall(hall);
        }
    }
    // saveCinemaHallWithLayout - создание зала с расположением мест
    public CinemaHall saveCinemaHallWithLayout(CinemaHall hall, String name, String description, int rowsCount, int seatsPerRow, Integer capacity, String hallType) {
        hall.setRowsCount(rowsCount);
        hall.setSeatsPerRow(seatsPerRow);
        hall.setTotalSeats(rowsCount * seatsPerRow);
        hall.setCapacity(rowsCount * seatsPerRow);

        if (hall.getHallType() == null) {
            hall.setHallType("STANDARD");
        }

        return cinemaHallRepository.save(hall);
    }
}