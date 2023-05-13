package com.example.demo;

import com.example.demo.repository.ProductPropertiesRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class Demo1Application {

    private final UserRepository userRepository;
    private final ProductPropertiesRepository productPropertiesRepository;

    private final Test test;

    public Demo1Application(UserRepository userRepository, ProductPropertiesRepository productPropertiesRepository, Test test) {
        this.userRepository = userRepository;
        this.productPropertiesRepository = productPropertiesRepository;
        this.test = test;
    }

    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }


    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
            System.out.println("In CommandLineRunnerImpl ");
            test.run();
        };
    }

}
