package com.ggzed.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Lc4jServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(Lc4jServerApplication.class, args);
    }
}
