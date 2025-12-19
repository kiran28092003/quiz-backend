package com.example.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class QuizBackendApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(QuizBackendApplication.class, args);
        Environment env = context.getEnvironment();
        String port = env.getProperty("local.server.port");
        System.out.println("✅ Spring Boot is running on port: " + port);
    }
}
