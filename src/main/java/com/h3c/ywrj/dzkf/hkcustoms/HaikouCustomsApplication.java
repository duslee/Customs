package com.h3c.ywrj.dzkf.hkcustoms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
public class HaikouCustomsApplication {
    public static void main(String[] args) {
        SpringApplication.run(HaikouCustomsApplication.class, args);
    }
}
