package br.gov.hackgov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HackgovApplication {

    public static void main(String[] args) {
        SpringApplication.run(HackgovApplication.class, args);
    }
}

