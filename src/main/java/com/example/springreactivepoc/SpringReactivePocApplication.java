package com.example.springreactivepoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;


@SpringBootApplication
public class SpringReactivePocApplication {

    public static void main(String[] args) {
        BlockHound.install();
        SpringApplication.run(SpringReactivePocApplication.class, args);
    }

}
