package com.benorim.carhov;

import org.springframework.boot.SpringApplication;

public class TestCarhovApplication {

    public static void main(String[] args) {
        SpringApplication.from(CarhovApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
