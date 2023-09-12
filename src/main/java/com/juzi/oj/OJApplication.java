package com.juzi.oj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author codejuzi
 */
@MapperScan(basePackages = {"com.juzi.oj.mapper"})
@SpringBootApplication
public class OJApplication {
    public static void main(String[] args) {
        SpringApplication.run(OJApplication.class, args);
    }
}
