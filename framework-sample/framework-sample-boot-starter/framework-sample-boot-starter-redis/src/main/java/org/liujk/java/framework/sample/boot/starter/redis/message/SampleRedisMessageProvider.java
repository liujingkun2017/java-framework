package org.liujk.java.framework.sample.boot.starter.redis.message;

import org.liujk.java.framework.boot.starter.redis.message.produce.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleRedisMessageProvider {

    Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 需要redis配置为哨兵模式
     * 并且在配置设置 messageEnable=true
     */

    @Autowired(required = false)
    private MessageProducer messageProducer;

    public void sendMessage1(String message) {
        logger.info("发送消息[channel1] ==>>{}", message);
        messageProducer.publish("channel1", message);
    }

    public void sendMessage2(String message) {
        logger.info("发送消息[channel2] ==>>{}", message);
        messageProducer.publish("channel2", message);
    }
}
