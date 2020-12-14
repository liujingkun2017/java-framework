package org.liujk.java.framework.sample.boot.starter.redis;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.liujk.java.framework.sample.boot.starter.redis.message.SampleRedisMessageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {StarterRedisSampleMain.class})
public class RedisMessageTest {

    @Autowired
    private SampleRedisMessageProvider sampleRedisMessageProvider;

    @Test
    public void sendMessageTest() {

        String message = "this is a sample message from test case";
        sampleRedisMessageProvider.sendMessage1(message);
        sampleRedisMessageProvider.sendMessage2(message);

    }


}
