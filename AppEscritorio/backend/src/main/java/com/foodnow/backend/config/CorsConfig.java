package com.foodnow.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Permitir todas las rutas de la API
                        .allowedOrigins("*") // Permitir acceso desde CUALQUIER lugar (Vue, React, Móvil...)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permitir todos los verbos HTTP
                        .allowedHeaders("*");
            }
        };
    }
}