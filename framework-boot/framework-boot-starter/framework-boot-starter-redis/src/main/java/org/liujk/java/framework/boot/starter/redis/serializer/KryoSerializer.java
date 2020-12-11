package org.liujk.java.framework.boot.starter.redis.serializer;

import org.liujk.java.framework.base.utils.serializer.kryo.Kryos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class KryoSerializer<T> implements RedisSerializer<T> {
    private static ThreadLocal<Class<?>> deserializeType = new ThreadLocal<>();
    private Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    /**
     * 兼容缓存中的老数据
     *
     * @param clazz
     */
    public static void setDeserializeType(Class<?> clazz) {
        deserializeType.set(clazz);
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        return Kryos.serialize(t);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        try {
            return (T) Kryos.deserialize(bytes);
        } catch (Exception e) {
            logger.error("KryoSerializer deserialize exception", e);
            throw e;
        }
    }
}
