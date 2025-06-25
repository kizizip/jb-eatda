package com.jbeatda.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.File;
@Configuration
public class EnvConfig {

    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(System.getProperty("user.dir") + "/jbeatda")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });

            System.out.println("✅ .env 파일 로드 완료");

        } catch (Exception e) {
            System.out.println("❌ .env 파일 로드 실패: " + e.getMessage());
        }
    }
}