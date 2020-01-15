package com.bitcoin.examples.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MainApplication.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "1337"));
        app.run(args);

        // SpringApplication.run(MainApplication.class, args);
    }
}
