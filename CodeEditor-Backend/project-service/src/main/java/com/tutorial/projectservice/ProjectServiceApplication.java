package com.tutorial.projectservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ProjectServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner printKafka(Environment env) {
        return args -> {
            System.out.println("BOOTSTRAP SERVERS = " +
                    env.getProperty("spring.kafka.bootstrap-servers"));
        };
    }
}
