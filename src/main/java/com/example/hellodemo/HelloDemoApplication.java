package com.example.hellodemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class HelloDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloDemoApplication.class, args);
    }

    @RestController
    public class HelloController {

        @GetMapping("/testapi")
        public String sayHello() {
            return "Hello, World!!!";
        }
    }
}