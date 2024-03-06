package se.ifmo.ru.soa2024_lab3_service1.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan("se.ifmo.ru.soa2024_lab3_service1.web.controller")
@EnableWebMvc
@Slf4j
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configured cors");
        registry.addMapping("/**").allowedMethods("*");
    }
}
