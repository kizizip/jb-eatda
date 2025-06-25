package com.jbeatda.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    // 비밀번호 프로퍼티 주입
    @Value("${spring.data.redis.password:}")   // 기본값 빈 문자열
    private String redisPassword;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory cf = new LettuceConnectionFactory(redisHost, redisPort);
        if (!redisPassword.isBlank()) {
            cf.setPassword(redisPassword);      // AUTH 명령 설정
        }
        return cf;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(
            LettuceConnectionFactory cf) {
        RedisTemplate<String, String> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(cf);
        // key/value 직렬화
        tpl.setKeySerializer(new StringRedisSerializer());
        tpl.setValueSerializer(new StringRedisSerializer());
        tpl.setHashKeySerializer(new StringRedisSerializer());
        tpl.setHashValueSerializer(new StringRedisSerializer());
        tpl.afterPropertiesSet();
        return tpl;
    }
}