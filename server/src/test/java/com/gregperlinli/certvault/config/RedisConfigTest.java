package com.gregperlinli.certvault.config;

import com.gregperlinli.certvault.domain.dto.UserProfileDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis Template Config Test
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className {@code RedisConfigTest}
 * @date 2025/3/22 21:02
 */
@SpringBootTest
@Slf4j
public class RedisConfigTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate() {
        UserProfileDTO userProfileDTO = new UserProfileDTO("testUser", "testDisplayName", "test@test.com", 1);
        redisTemplate.opsForValue().set("testUser", userProfileDTO);
        log.info("Redis Data: {}", redisTemplate.opsForValue().get("testUser"));
    }

}
