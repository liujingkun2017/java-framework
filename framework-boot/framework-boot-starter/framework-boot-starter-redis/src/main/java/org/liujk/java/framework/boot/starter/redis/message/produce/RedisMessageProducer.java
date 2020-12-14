package org.liujk.java.framework.boot.starter.redis.message.produce;

import com.alibaba.fastjson.JSON;
import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.boot.starter.redis.message.RedisMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.Assert;
import redis.clients.jedis.Jedis;

public class RedisMessageProducer implements MessageProducer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RedisConnectionFactory connectionFactory;
    private String namespace;

    public RedisMessageProducer(String namespace, RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.namespace = namespace;
    }

    @Override
    public Boolean publish(String channel, RedisMessage msg) {
        Assert.notNull(channel, "消息通道不能为空！");
        Assert.notNull(msg, "不能发送空消息！");
        logger.debug("向通道[{}]发送消息:{}", channel, msg);
        Jedis jedis = (Jedis) connectionFactory.getConnection().getNativeConnection();
        jedis.publish(serialize(getPrefixKey() + channel), JSON.toJSONBytes(msg));
        return true;
    }

    @Override
    public Boolean publish(String channel, String stringMsg) {
        Assert.notNull(channel, "消息通道不能为空！");
        Assert.notNull(stringMsg, "不能发送空消息！");
        logger.debug("向通道[{}]发送消息:{}", channel, stringMsg);
        return null;
    }

    @Override
    public Boolean publish(String channel, Object msg) {
        Assert.notNull(channel, "消息通道不能为空！");
        Assert.notNull(msg, "不能发送空消息！");
        logger.debug("向通道[{}]发送消息:{}", channel, msg);
        return null;
    }


    private byte[] serialize(String data) {
        String noNullData = StringUtils.isBlank(data) ? "@NULL" : data;
        try {
            return noNullData.getBytes("utf-8");
        } catch (Exception e) {
            return noNullData.getBytes();
        }
    }

    private String getPrefixKey() {
        return this.namespace + ":";
    }
}
