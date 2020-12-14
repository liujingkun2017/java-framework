package org.liujk.java.framework.sample.boot.starter.redis.message;

import org.liujk.java.framework.boot.starter.redis.message.RedisMessage;
import org.liujk.java.framework.boot.starter.redis.message.subscribe.RedisMessageSubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SampleRedisMessageSubscribe {
    Logger logger = LoggerFactory.getLogger(SampleRedisMessageSubscribe.class);

    @RedisMessageSubscribe(channel = {"channel1"})
    public void listenerChannel1(RedisMessage message) {
        logger.info("channel1收到消息：{}", message);
    }

    @RedisMessageSubscribe(channel = {"channel2"})
    public void listenerChannel2(RedisMessage message) {
        logger.info("channel2收到消息：{}", message);
    }

    @RedisMessageSubscribe(channel = {"channel1", "channel2"})
    public void listenerChannel12(RedisMessage message) {
        logger.info("channel12收到消息：{}", message);
    }
}
