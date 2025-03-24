package com.example.ticketHandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.ticketHandler")
@EnableScheduling
@EnableAsync
public class TicketHandlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketHandlerApplication.class, args);
    }

}
