package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan({"com.example.demo.mapper"})
@SpringBootApplication
public class ZksourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZksourceApplication.class, args);
    }

}
