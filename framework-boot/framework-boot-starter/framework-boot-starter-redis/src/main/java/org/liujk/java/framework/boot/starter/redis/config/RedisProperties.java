package org.liujk.java.framework.boot.starter.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@Data
@ConfigurationProperties(RedisProperties.PREFIX)
public class RedisProperties {

    public static final String PREFIX = "starter.redis";

    /**
     * 是否启用Redis组件
     */
    private boolean enable = true;

    /**
     * 是否哨兵模式，默认：true
     */
    private boolean sentinel = true;

    /**
     * 是否是集群模式，默认：true
     */
    private boolean cluster = true;

    /**
     * 必填：Sentinel服务地址，如有多台以分号/逗号间隔。
     * 格式：“ip0:port0;ip1:port1...”
     */
    private String[] serverAddress;

    /**
     * 必填：在Sentinel上注册的Redis服务名称，用于区分一组Redis实例
     */
    private String masterName;

    /**
     * 单机模式下的密码设置
     */
    private String password;

    /**
     * database选项
     */
    private int database;

    /**
     * 可选：应用缓存空间名，必须注意，为避免与其它应用发生冲突，建议采用默认名，自定义命名空间一定要特别注意。
     */
    private String namespace;

    /**
     * 可选：创建socket连接的超时时间
     */
    private int timeOut = 2000;

    /**
     * 可选：基于注解的Spring CacheManager，设置缓存的过期时间。默认为0，即不会过期
     * 如果是使用RedisTemplate来显示读写缓存的，需要自己调用expire方法设置每个key的过期时间 单位秒
     */
    private int expireTime = 0;

    /**
     * 是否启用 Publish/Subscribe功能
     * <p>
     * 注意，需要redis 哨兵模式才能启用
     */
    private boolean messageEnable = false;

    /**
     * 外部线程池Spring Bean名称
     * 主要用于 listener 的线程池
     */
    private String threadPoolBeanName;

    /**
     * 连接池属性
     */
    private Pool pool = new Pool();

    /**
     * 连接池属性
     */
    public static class Pool {
        /**
         * 最大连接数
         */
        private int maxTotal = 500;
        /**
         * 最大空闲连接
         */
        private int maxIdle = 8;
        /**
         * 最小空闲连接
         */
        private int minIdle = 2;
        /**
         * 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
         */
        private int maxWaitMillis = 2000;
        /**
         * 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
         */
        private boolean blockWhenExhausted = true;

        /**
         * 是否启用后进先出, 默认true
         */
        private boolean lifo = true;
        /**
         * 在获取连接的时候检查有效性, 默认false
         */
        private boolean testOnBorrow = false;
        /**
         * 在返回连接的时候检查有效性, 默认false
         */
        private boolean testOnReturn = false;
        /**
         * 在空闲时检查有效性, 默认false
         */
        private boolean testWhileIdle = true;
        /**
         * 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
         */
        private int numTestsPerEvictionRun = 3;

        /**
         * 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
         */
        private long timeBetweenEvictionRunsMillis = -1;

        /**
         * 对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
         */
        private long softMinEvictableIdleTimeMillis = -1;
        /**
         * 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
         */
        private long minEvictableIdleTimeMillis = 1800000;

        public int getMaxTotal() {
            return maxTotal;
        }

        public void setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
        }

        public int getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxWaitMillis() {
            return maxWaitMillis;
        }

        public void setMaxWaitMillis(int maxWaitMillis) {
            this.maxWaitMillis = maxWaitMillis;
        }

        public boolean isBlockWhenExhausted() {
            return blockWhenExhausted;
        }

        public void setBlockWhenExhausted(boolean blockWhenExhausted) {
            this.blockWhenExhausted = blockWhenExhausted;
        }

        public boolean isLifo() {
            return lifo;
        }

        public void setLifo(boolean lifo) {
            this.lifo = lifo;
        }

        public boolean isTestOnBorrow() {
            return testOnBorrow;
        }

        public void setTestOnBorrow(boolean testOnBorrow) {
            this.testOnBorrow = testOnBorrow;
        }

        public boolean isTestOnReturn() {
            return testOnReturn;
        }

        public void setTestOnReturn(boolean testOnReturn) {
            this.testOnReturn = testOnReturn;
        }

        public boolean isTestWhileIdle() {
            return testWhileIdle;
        }

        public void setTestWhileIdle(boolean testWhileIdle) {
            this.testWhileIdle = testWhileIdle;
        }

        public int getNumTestsPerEvictionRun() {
            return numTestsPerEvictionRun;
        }

        public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
            this.numTestsPerEvictionRun = numTestsPerEvictionRun;
        }

        public long getTimeBetweenEvictionRunsMillis() {
            return timeBetweenEvictionRunsMillis;
        }

        public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
            this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        }

        public long getSoftMinEvictableIdleTimeMillis() {
            return softMinEvictableIdleTimeMillis;
        }

        public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
            this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
        }

        public long getMinEvictableIdleTimeMillis() {
            return minEvictableIdleTimeMillis;
        }

        public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
            this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        }
    }

    /**
     * 检查配置信息
     */
    public void check() {
        if (enable) {
            Assert.notEmpty(serverAddress, String.format("Redis连接地址[%s.%s]不能为空", PREFIX, "serverAddress"));
            Assert.hasText(masterName, String.format("Redis配置的masterName [%s.%s]不能为空", PREFIX, "masterName"));
        }
    }
}
