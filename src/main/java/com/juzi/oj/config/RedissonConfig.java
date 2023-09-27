package com.juzi.oj.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author codejuzi
 */
@Data
@Configuration
@ConfigurationProperties("spring.redis")
public class RedissonConfig {

    private Integer database;
    private String host;
    private Integer port;
//    private String password;


    @Bean
    public RedissonClient redissonClient() {
        Config redissonConfig = new Config();
        redissonConfig.useSingleServer()
                .setDatabase(database)
                .setAddress(String.format("redis://%s:%s", host, port));
//                .setPassword(password);
        return Redisson.create(redissonConfig);
    }
}

