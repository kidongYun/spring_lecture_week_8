package com.artineer.spring_lecture_week_2.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisRunner implements ApplicationRunner {
    private final RedisTemplate<String, String> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisRunner(RedisTemplate<String, String> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        redisTemplate.opsForValue().set("key1", "value1");
        log.info(redisTemplate.opsForValue().get("key1"));

        stringRedisTemplate.opsForValue().set("key2", "value2");
        log.info(stringRedisTemplate.opsForValue().get("key2"));
    }
}
