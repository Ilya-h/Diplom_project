package org.example.cinema.service;

import org.example.cinema.model.Review;
import org.example.cinema.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final MovieService movieService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, UserService userService,
                         MovieService movieService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.movieService = movieService;
    }

    // createReview - создание отзыва
    public Review createReview(Long userId, Long movieId, Integer rating, String comment) {
        // Проверка, не оставлял ли пользователь уже отзыв на этот фильм
        if (reviewRepository.existsByMovieIdAndUserId(movieId, userId)) {
            throw new RuntimeException("User has already reviewed this movie");
        }

        Review review = new Review();
        review.setUser(userService.findUserById(userId).orElseThrow());
        review.setMovie(movieService.getMovieById(movieId).orElseThrow());
        review.setRating(rating);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    // getReviewsByMovie - получение отзывов по фильму
    public List<Review> getReviewsByMovie(Long movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    // getReviewsByUser - получение отзывов пользователя
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    // getReviewById - получение отзыва по ID
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    // deleteReview - удаление отзыва
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    // updateReview - обновление отзыва
    public Review updateReview(Long reviewId, Integer rating, String comment) {
        Optional<Review> reviewOptional = getReviewById(reviewId);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setRating(rating);
            review.setComment(comment);
            return reviewRepository.save(review);
        }
        throw new RuntimeException("Review not found");
    }

    // calculateAverageRating - расчет среднего рейтинга фильма
    public Double calculateAverageRating(Long movieId) {
        return reviewRepository.calculateAverageRatingByMovieId(movieId);
    }

    // getTopRatedMovies - получение фильмов с наивысшим рейтингом
    public List<Object[]> getTopRatedMovies(int limit) {
        return reviewRepository.findTopRatedMovies(limit);
    }

    // canUserReviewMovie - может ли пользователь оставить отзыв на фильм
    public boolean canUserReviewMovie(Long userId, Long movieId) {
        return !reviewRepository.existsByMovieIdAndUserId(movieId, userId);
    }
}