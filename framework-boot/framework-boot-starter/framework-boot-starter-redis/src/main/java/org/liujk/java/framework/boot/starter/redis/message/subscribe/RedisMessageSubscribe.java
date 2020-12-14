package org.liujk.java.framework.boot.starter.redis.message.subscribe;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisMessageSubscribe {
    /**
     * 消息通道
     *
     * @return
     */
    String[] channel();
}
