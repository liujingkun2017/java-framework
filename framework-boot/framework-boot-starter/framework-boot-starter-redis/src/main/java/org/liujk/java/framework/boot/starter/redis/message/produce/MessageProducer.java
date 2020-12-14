package org.liujk.java.framework.boot.starter.redis.message.produce;


import org.liujk.java.framework.boot.starter.redis.message.RedisMessage;

public interface MessageProducer {
    /**
     * 该接口用于发布消息到指定通道
     * 所有订阅了该通道且在线的消费者，能收到该消息
     * 没有订阅或者订阅后但挂掉的消费者，则不能收到该消息
     *
     * @params: channel 类似MQ的Queue
     * @params: msg 消息实例
     * @return: 发送成功返回true
     */
    Boolean publish(String channel, RedisMessage msg);

    /**
     * 该接口用于发布消息到指定通道
     * 所有订阅了该通道且在线的消费者，能收到该消息
     * 没有订阅或者订阅后但挂掉的消费者，则不能收到该消息
     *
     * @params: channel 类似MQ的Queue
     * @params: msg 消息实例
     * @return: 发送成功返回true
     */
    Boolean publish(String channel, String stringMsg);

    /**
     * 该接口用于发布消息到指定通道
     * 所有订阅了该通道且在线的消费者，能收到该消息
     * 没有订阅或者订阅后但挂掉的消费者，则不能收到该消息
     *
     * @params: channel 类似MQ的Queue
     * @params: msg 消息实例
     * @return: 发送成功返回true
     */
    Boolean publish(String channel, Object msg);
}
