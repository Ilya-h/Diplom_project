package org.example.cinema.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // Получаем роли из объекта Authentication (Spring Security уже загрузил пользователя)
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

        // Логируем для отладки
        System.out.println("=== АВТОРИЗАЦИЯ УСПЕШНА ===");
        System.out.println("Пользователь: " + authentication.getName());
        System.out.println("Роли: " + authentication.getAuthorities());
        System.out.println("isAdmin: " + isAdmin);
        System.out.println("isUser: " + isUser);

        // Перенаправление в зависимости от роли
        if (isAdmin) {
            System.out.println("Перенаправление админа на /admin/dashboard");
            response.sendRedirect("/admin/dashboard");
        } else if (isUser) {
            System.out.println("Перенаправление пользователя на /booking/movies");
            response.sendRedirect("/booking/movies");
        } else {
            System.out.println("Перенаправление на главную");
            response.sendRedirect("/");
        }
    }
}