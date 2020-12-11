package org.liujk.java.framework.boot.starter.redis.serializer;

import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.boot.starter.core.ApplicationInfo;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;


public class RedisKeySerializer implements RedisSerializer<String> {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private String namespace = ApplicationInfo.getAppName();

    public RedisKeySerializer() {
    }

    public RedisKeySerializer(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public byte[] serialize(String t) throws SerializationException {
        return realKey(t).getBytes(UTF8);
    }

    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        String key = new String(bytes, UTF8);
        return parseKey(key);
    }

    private String realKey(String key) {
        if (StringUtils.isNotBlank(namespace)) {
            return namespace + ":" + key;
        }
        return key;
    }

    private String parseKey(String key) {
        if (StringUtils.isNotBlank(key) && key.startsWith(namespace)) {
            return key.substring(namespace.length() + 1);
        }
        return key;
    }
}
