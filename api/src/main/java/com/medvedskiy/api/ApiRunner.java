package com.medvedskiy.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ApiRunner {

    public static ConfigurableApplicationContext start() {
        return SpringApplication.run(
                ApiRunner.class
        );
    }

    public static void main(String[] args) {
        start();
    }
}
