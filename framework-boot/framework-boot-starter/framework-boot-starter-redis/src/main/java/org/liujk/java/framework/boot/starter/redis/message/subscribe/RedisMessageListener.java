package org.liujk.java.framework.boot.starter.redis.message.subscribe;

import com.alibaba.fastjson.JSON;
import org.liujk.java.framework.base.utils.lang.ToString;
import org.liujk.java.framework.boot.starter.redis.message.RedisMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.handler.HandlerMethod;

import java.lang.reflect.InvocationTargetException;

public class RedisMessageListener implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(RedisMessageListener.class);
    /**
     * 是否打印接收到的消息日志
     */
    private boolean enableLog = true;

    private HandlerMethod handlerMethod;

    public RedisMessageListener(HandlerMethod handlerMethod) {
        this.handlerMethod = handlerMethod;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handle(message);
    }


    private void handle(Message message) {
        try {
            RedisMessage redisMessage = JSON.parseObject(message.getBody(), RedisMessage.class);
            if (enableLog) {
                logger.info("收到消息:{}",
                        ToString.toString(redisMessage));
            }
            Object[] args = {redisMessage};

            handlerMethod.getMethod().invoke(handlerMethod.getBean(), args);

            if (enableLog) {
                logger.info("消息完成");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.warn("调用监听方法[{}#{}]失败！", handlerMethod.getBean().getClass().getName(),
                    handlerMethod.getMethod().getName());
        } finally {
            MDC.clear();
        }
    }

    public boolean isEnableLog() {
        return enableLog;
    }

    public void setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
    }

    public HandlerMethod getHandlerMethod() {
        return handlerMethod;
    }

    public void setHandlerMethod(HandlerMethod handlerMethod) {
        this.handlerMethod = handlerMethod;
    }
}
