package com.zah;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class MessageServer {
    public static void main(String[] args) {
        SpringApplication.run(MessageServer.class,args);
    }
}
