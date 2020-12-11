package org.liujk.java.framework.boot.starter.redis;

import org.liujk.java.framework.boot.starter.redis.config.RedisProperties;
import org.liujk.java.framework.boot.starter.redis.serializer.KryoSerializer;
import org.liujk.java.framework.boot.starter.redis.serializer.RedisKeySerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(prefix = RedisProperties.PREFIX, name = "enable", matchIfMissing = true)
public class RedisAutoConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedisTemplate redisTemplate(
            @Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new KryoSerializer());
        redisTemplate.setKeySerializer(new RedisKeySerializer(redisProperties.getNamespace()));
        return redisTemplate;
    }

    /**
     * 创建JedisConnectionFactory
     *
     * @param redisProperties
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        JedisConnectionFactory jedisConnectionFactory = createJedisConnectionFactory(redisProperties);
        jedisConnectionFactory.setTimeout(redisProperties.getTimeOut());
        return jedisConnectionFactory;
    }

    private JedisConnectionFactory createJedisConnectionFactory(RedisProperties redisProperties) {
        JedisPoolConfig poolConfig = jedisPoolConfig(redisProperties);
        if (redisProperties.isSentinel()) {
            // 哨兵模式
            return new JedisConnectionFactory(getSentinelConfiguration(redisProperties), poolConfig);
        } else {
            if (redisProperties.isCluster()) {
                // 集群模式
                return new JedisConnectionFactory(getClusterConfiguration(redisProperties), poolConfig);
            } else {
                // 单机模式
                return new JedisConnectionFactory(getStandaloneConfiguration(redisProperties));
            }

        }
    }

    private JedisPoolConfig jedisPoolConfig(RedisProperties redisProperties) {
        RedisProperties.Pool pool = redisProperties.getPool();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(pool.getMaxTotal());
        jedisPoolConfig.setMaxIdle(pool.getMaxIdle());
        jedisPoolConfig.setMaxWaitMillis(pool.getMaxWaitMillis());
        jedisPoolConfig.setTestOnBorrow(pool.isTestOnBorrow());
        jedisPoolConfig.setTestWhileIdle(pool.isTestWhileIdle());
        jedisPoolConfig.setTestOnReturn(pool.isTestOnReturn());
        jedisPoolConfig.setBlockWhenExhausted(pool.isBlockWhenExhausted());
        jedisPoolConfig.setMinIdle(pool.getMinIdle());
        jedisPoolConfig.setLifo(pool.isLifo());
        return jedisPoolConfig;
    }

    private RedisClusterConfiguration getClusterConfiguration(RedisProperties redisProperties) {
        RedisClusterConfiguration config = new RedisClusterConfiguration(
                Arrays.asList(redisProperties.getServerAddress()));
        return config;
    }

    private RedisSentinelConfiguration getSentinelConfiguration(RedisProperties redisProperties) {
        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.master(redisProperties.getMasterName());
        config.setSentinels(createSentinels(redisProperties));
        return config;
    }

    private RedisStandaloneConfiguration getStandaloneConfiguration(RedisProperties redisProperties) {
        String serverAddress = redisProperties.getServerAddress()[0];
        String[] parts = StringUtils.split(serverAddress, ":");
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(parts[0]);
        redisStandaloneConfiguration.setPort(Integer.valueOf(parts[1]));
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        return redisStandaloneConfiguration;
    }

    private List<RedisNode> createSentinels(RedisProperties redisProperties) {
        List<RedisNode> nodes = new ArrayList<RedisNode>();
        for (String node : redisProperties.getServerAddress()) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException(
                        "Invalid redis sentinel " + "property '" + node + "'", ex);
            }
        }
        return nodes;
    }

}
