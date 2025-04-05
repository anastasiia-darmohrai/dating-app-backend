package com.lab1.datingapp;



import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Objects;


@SpringBootApplication
@EnableMongoRepositories
public class DatingappApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("MONGO_DB_USERNAME", Objects.requireNonNull(dotenv.get("MONGO_DB_USERNAME")));
        System.setProperty("MONGO_DB_PASSWORD", Objects.requireNonNull(dotenv.get("MONGO_DB_PASSWORD")));

        SpringApplication.run(DatingappApplication.class, args);
    }
}
