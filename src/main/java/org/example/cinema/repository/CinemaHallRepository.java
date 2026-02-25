package org.example.cinema.repository;

import org.example.cinema.model.CinemaHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CinemaHallRepository extends JpaRepository<CinemaHall, Long> {

    // Поиск зала по названию
    Optional<CinemaHall> findByName(String name);

    // Проверка существования зала с определенным названием
    boolean existsByName(String name);

    // Поиск залов по общему количеству мест
    List<CinemaHall> findByTotalSeatsGreaterThanEqual(Integer minSeats);

    // Поиск залов с определенным минимальным количеством мест
    List<CinemaHall> findByTotalSeatsBetween(Integer minSeats, Integer maxSeats);
}