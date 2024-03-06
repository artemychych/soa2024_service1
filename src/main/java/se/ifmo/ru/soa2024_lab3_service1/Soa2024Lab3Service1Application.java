package se.ifmo.ru.soa2024_lab3_service1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Soa2024Lab3Service1Application {

    public static void main(String[] args) {
        SpringApplication.run(Soa2024Lab3Service1Application.class, args);
    }

}
