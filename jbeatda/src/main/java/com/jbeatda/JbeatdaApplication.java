package com.jbeatda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JbeatdaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JbeatdaApplication.class, args);
    }

}


