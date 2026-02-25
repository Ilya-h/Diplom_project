package org.example.cinema.service;

import org.example.cinema.model.User;
import org.example.cinema.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // registerUser - регистрация пользователя
    public User registerUser(User user) {
        // Проверка на существующий email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email уже зарегистрирован");
        }

        // Хешируем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Проверяем и устанавливаем роль (по умолчанию USER, если не указано)
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        } else if (!user.getRole().equals("USER") && !user.getRole().equals("ADMIN")) {
            // Если передана недопустимая роль, устанавливаем USER по умолчанию
            user.setRole("USER");
        }

        return userRepository.save(user);
    }


    // findByEmail -
    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }

    // findUserByEmail - поиск пользователя по email
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // findUserById - поиск пользователя по ID
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    // saveUser - сохранение пользователя
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // deleteUser - удаление пользователя
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // validateRegistration - валидация регистрации
    public boolean validateRegistration(User user, BindingResult bindingResult) {
        if (userRepository.existsByEmail(user.getEmail())) {
            bindingResult.addError(new FieldError("user", "email", "Email already exists"));
            return false;
        }

        return !bindingResult.hasErrors();
    }

    // getAllUsers - получение всех пользователей
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // getUsersByRole - получение пользователей по роли
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    // updateUserProfile - обновление профиля пользователя
    public User updateUserProfile(Long userId, String firstName, String lastName, String phoneNumber) {
        Optional<User> userOptional = findUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            return saveUser(user);
        }
        throw new RuntimeException("User not found");
    }
}