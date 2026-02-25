package org.example.cinema.controller;

import org.example.cinema.dto.RegistrationRequest;
import org.example.cinema.model.User;
import org.example.cinema.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // home - главная страница
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {

            String username = authentication.getName();
            var userOptional = userService.findUserByEmail(username);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String role = user.getRole();

                switch (role) {
                    case "ADMIN":
                        return "redirect:/admin/dashboard";
                    case "USER":
                        return "redirect:/booking/movies";
                }
            }
        }
        return "redirect:/booking/movies";
    }

    // showRegistrationForm - отображение формы регистрации
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register";
    }

    // registerUser - регистрация пользователя
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationRequest") RegistrationRequest registrationRequest,
                               BindingResult bindingResult,
                               Model model) {

        User user = new User();
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(registrationRequest.getPassword());
        user.setPhoneNumber(registrationRequest.getPhoneNumber());
        // ВАЖНО: Устанавливаем роль из формы
        user.setRole(registrationRequest.getRole());

        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationRequest", registrationRequest);
            return "register";
        }

        try {
            userService.registerUser(user);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка регистрации: " + e.getMessage());
            model.addAttribute("registrationRequest", registrationRequest);
            return "register";
        }
    }

    // showLoginForm - отображение формы входа
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}