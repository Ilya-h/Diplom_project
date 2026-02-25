// TestController.java - создайте новый файл
package org.example.cinema.controller;

import org.example.cinema.model.Session;
import org.example.cinema.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@RestController
public class TestController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SessionRepository sessionRepository;

    @GetMapping("/api/test/session/{id}")
    public String testSession(@PathVariable Long id) {
        StringBuilder result = new StringBuilder();

        try {
            // 1. Проверка через EntityManager
            result.append("=== EntityManager find ===\n");
            Session session1 = entityManager.find(Session.class, id);
            result.append("Session: ").append(session1 != null ? "Found" : "Null").append("\n");
            if (session1 != null) {
                result.append("Movie: ").append(session1.getMovie() != null ? session1.getMovie().getTitle() : "Null").append("\n");
                result.append("Hall: ").append(session1.getHall() != null ? session1.getHall().getName() : "Null").append("\n");
            }

            // 2. Проверка через Repository
            result.append("\n=== Repository findById ===\n");
            var session2 = sessionRepository.findById(id);
            result.append("Session: ").append(session2.isPresent() ? "Found" : "Null").append("\n");
            session2.ifPresent(s -> {
                result.append("Movie: ").append(s.getMovie() != null ? s.getMovie().getTitle() : "Null").append("\n");
                result.append("Hall: ").append(s.getHall() != null ? s.getHall().getName() : "Null").append("\n");
            });

            // 3. Прямой SQL
            result.append("\n=== Database check ===\n");
            List<Object[]> dbResult = entityManager.createNativeQuery(
                            "SELECT s.id, m.title, h.name FROM sessions s " +
                                    "LEFT JOIN movies m ON s.movie_id = m.id " +
                                    "LEFT JOIN cinema_halls h ON s.hall_id = h.id " +
                                    "WHERE s.id = ?1")
                    .setParameter(1, id)
                    .getResultList();

            if (!dbResult.isEmpty()) {
                Object[] row = dbResult.get(0);
                result.append("ID: ").append(row[0]).append("\n");
                result.append("Movie: ").append(row[1]).append("\n");
                result.append("Hall: ").append(row[2]).append("\n");
            } else {
                result.append("No data in database!\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage() + "\n" + e.getStackTrace()[0];
        }
    }
}