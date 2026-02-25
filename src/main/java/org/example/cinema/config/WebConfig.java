package org.example.cinema.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Статические маршруты
        registry.addViewController("/about").setViewName("about");
        registry.addViewController("/contact").setViewName("contact");
        registry.addViewController("/faq").setViewName("faq");
        registry.addViewController("/privacy").setViewName("privacy");
        registry.addViewController("/terms").setViewName("terms");
        registry.addViewController("/access-denied").setViewName("access-denied");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Здесь можно добавить интерсепторы для логирования, аудита и т.д.
    }
}