package org.example.cinema.repository;

import org.example.cinema.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // findByMovieId - поиск отзывов по фильму
    // вход: movieId - идентификатор фильма
    // выход: список отзывов на указанный фильм
    List<Review> findByMovieId(Long movieId);

    // findByUserId - поиск отзывов пользователя
    // вход: userId - идентификатор пользователя
    // выход: список отзывов, оставленных пользователем
    List<Review> findByUserId(Long userId);

    // findByMovieIdAndUserId - поиск отзыва пользователя на конкретный фильм
    // вход:
    //   - movieId - идентификатор фильма
    //   - userId - идентификатор пользователя
    // выход: Optional<Review> - отзыв, если найден
    List<Review> findByMovieIdAndUserId(Long movieId, Long userId);

    // calculateAverageRatingByMovieId - расчет среднего рейтинга фильма
    // вход: movieId - идентификатор фильма
    // выход: средний рейтинг фильма
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie.id = :movieId")
    Double calculateAverageRatingByMovieId(@Param("movieId") Long movieId);

    // findTopRatedMovies - поиск фильмов с наивысшим рейтингом
    // вход: limit - ограничение количества результатов
    // выход: список фильмов с наивысшим рейтингом
    @Query("SELECT r.movie.id, AVG(r.rating) as avgRating, COUNT(r) as reviewCount " +
            "FROM Review r GROUP BY r.movie.id HAVING COUNT(r) >= 5 " +
            "ORDER BY avgRating DESC")
    List<Object[]> findTopRatedMovies(@Param("limit") int limit);

    // existsByMovieIdAndUserId - проверка существования отзыва пользователя на фильм
    // вход:
    //   - movieId - идентификатор фильма
    //   - userId - идентификатор пользователя
    // выход: true - если пользователь уже оставлял отзыв на этот фильм
    boolean existsByMovieIdAndUserId(Long movieId, Long userId);
}