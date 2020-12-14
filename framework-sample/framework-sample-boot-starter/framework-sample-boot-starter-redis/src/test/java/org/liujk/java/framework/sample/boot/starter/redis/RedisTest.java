package org.liujk.java.framework.sample.boot.starter.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {StarterRedisSampleMain.class})
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 测试redis的基本操作
     */
    @Test
    public void redisMethodTest() {

        redisTemplate.opsForValue().set("sample-boot-starter-redis-key", "123");
        String result = (String) redisTemplate.opsForValue().get("sample-boot-starter-redis-key");
        System.out.println("result=" + result);

    }

}
