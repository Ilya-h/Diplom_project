package org.example.cinema.repository;

import org.example.cinema.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // findByEmail - поиск пользователя по email
    // вход: email - email пользователя
    // выход: Optional<User> - пользователь, если найден
    Optional<User> findByEmail(String email);

    // findByRole - поиск пользователей по роли
    // вход: role - роль пользователя
    // выход: список пользователей с указанной ролью
    List<User> findByRole(String role);

    // existsByEmail - проверка существования пользователя с указанным email
    // вход: email - email для проверки
    // выход: true - если пользователь с таким email существует
    boolean existsByEmail(String email);

    // findByPhoneNumber - поиск пользователя по номеру телефона
    // вход: phoneNumber - номер телефона
    // выход: Optional<User> - пользователь, если найден
    Optional<User> findByPhoneNumber(String phoneNumber);
}