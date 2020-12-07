package org.liujk.java.framework.boot.starter.jdbc.druid;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.liujk.java.framework.boot.starter.jdbc.config.JDBCProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Properties;

/**
 * druid数据源基本配置信息
 */
@Data
@Component
@ConfigurationProperties(prefix = JDBCProperties.PREFIX)
public class DruidProperties {

    public static final int DEFAULT_SLOW_SQL_THRESHOLD = 1000;

    private static final String DEFAULT_VALIDATION_QUERY = "SELECT 1 FROM DUAL";
    public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 1000L * 60L * 5L;
    public static final long DEFAULT_MAX_EVICTABLE_IDLE_TIME_MILLIS = 1000L * 60L * 30L;

    private static final int ORACLE_MAX_ACTIVE = 200;
    private static final int MYSQL_MAX_ACTIVE = 100;

    /**
     * 前缀
     */
    private String prefix = JDBCProperties.PREFIX;

    /**
     * 组件开关
     */
    private boolean enable = true;

    /**
     * 必填：jdbc url
     */
    private String url;

    /**
     * 必填：数据库用户名
     */
    private String username;

    /**
     * 必填：数据库密码
     */
    private String password;

    /**
     * 初始连接数
     */
    private Integer initialSize = 5;

    /**
     * 最小空闲连接数
     */
    private Integer minIdle = 5;

    /**
     * 最大连接数
     */
    private Integer maxActive = 100;

    /**
     * 获取连接等待超时的时间 单位:毫秒
     */
    private Integer maxWait = 10000;

    /**
     * 慢sql日志阈值(毫秒)，超过此值则打印日志
     */
    private Integer slowSqlThreshold = DEFAULT_SLOW_SQL_THRESHOLD;

    /**
     * 大结果集阈值，超过此值则打印日志
     */
    private Integer maxResultThreshold = 1000;

    /**
     * 是否在非线上环境开启打印sql，默认开启
     */
    private boolean showSql = true;

    /**
     * 测试连接查询语句
     */
    private String validationQuery = DEFAULT_VALIDATION_QUERY;

    /**
     * 指明连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除.
     */
    private boolean testWhileIdle = DruidDataSource.DEFAULT_WHILE_IDLE;

    /**
     * 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
     * 注意: 设置为true后如果要生效,validationQuery参数必须设置为非空字符串
     */
    private boolean testOnBorrow = DruidDataSource.DEFAULT_TEST_ON_BORROW;

    /**
     * 是否在归还到池中前进行检验
     * 注意: 设置为true后如果要生效,validationQuery参数必须设置为非空字符串
     */
    private boolean testOnReturn = DruidDataSource.DEFAULT_TEST_ON_RETURN;

    /**
     * 检测需要关闭的空闲连接间隔，单位是毫秒
     */
    private long timeBetweenEvictionRunsMillis = DruidDataSource.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

    /**
     * 是否自动回收超时连接
     */
    private boolean removeAbandoned = false;

    /**
     * 超时时间(以秒数为单位)
     */
    private int removeAbandonedTimeout = 180;

    /**
     * 是否在自动回收超时连接的时候打印连接的超时错误
     */
    private boolean logAbandoned = false;


    private ClassLoader beanClassLoader;

    /**
     * 检查关键信息配置
     */
    public void check() {
        if (enable) {
            Assert.hasText(url, "数据库连接starter.jdbc.url不能为空");
            Assert.hasText(username, "数据库用户名starter.jdbc.username不能为空");
            Assert.hasText(password, "数据库密码starter.jdbc.password不能为空");
        }
    }

    /**
     * 标准化连接的url
     *
     * @param url
     * @return
     */
    public static String normalizeUrl(String url) {
        if (url != null) {
            if (isMysql(url)) {
                if (!url.contains("?")) {
                    url = url
                            +
                            "?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false&autoReconnect=true";

                } else {
                    if (url.contains("allowMultiQueries") && !url.contains("useSSL")) {
                        url = url + "&useSSL=false";
                    }
                    if (!url.contains("autoReconnect")) {
                        url = url + "&autoReconnect=true";
                    }
                }
            } else if (isH2(url)) {
                if (!url.toUpperCase().contains("MODE")) {
                    url = url + ";MODE=MYSQL";
                }
                if (!url.toUpperCase().contains("AUTO_RECONNECT")) {
                    url = url + ";AUTO_RECONNECT=TRUE";
                }
                if (!url.toUpperCase().contains("DB_CLOSE_DELAY")) {
                    url = url + ";DB_CLOSE_DELAY=-1";
                }
            }
        }
        return url;
    }

    /**
     * 是否是mysql
     *
     * @param url
     * @return
     */
    public static boolean isMysql(String url) {
        return url.toLowerCase().startsWith("jdbc:mysql");
    }

    /**
     * 是否是h2
     *
     * @param url
     * @return
     */
    public static boolean isH2(String url) {
        return url.toLowerCase().startsWith("jdbc:h2");
    }

    public DruidDataSourceWrapper build() {
        this.check();
        if (this.beanClassLoader == null) {
            this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        }
        DruidDataSourceWrapper dataSource = new DruidDataSourceWrapper();
        // 基本配置
        dataSource.setDriverClassLoader(this.getBeanClassLoader());
        dataSource.setUrl(this.getUrl());
        dataSource.setUsername(this.getUsername());
        dataSource.setPassword(this.getPassword());

        // 应用程序可以自定义的参数
        dataSource.setInitialSize(this.getInitialSize());
        dataSource.setMinIdle(this.getMinIdle());
        if (isMysql(this.url)) {
            maxActive = Math.max(maxActive, MYSQL_MAX_ACTIVE);
        } else {
            maxActive = Math.max(maxActive, ORACLE_MAX_ACTIVE);
        }
        dataSource.setMaxActive(this.getMaxActive());
        dataSource.setMaxWait(this.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(this.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
        dataSource.setMaxEvictableIdleTimeMillis(DEFAULT_MAX_EVICTABLE_IDLE_TIME_MILLIS);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(100);
        dataSource.setTestWhileIdle(this.isTestWhileIdle());

        // 从连接池中获取连接时测试
        dataSource.setTestOnBorrow(this.isTestOnBorrow());
        dataSource.setTestOnReturn(this.isTestOnReturn());
        dataSource.setValidationQuery(this.getValidationQuery());
        dataSource.setValidationQueryTimeout(5);

        // 是否自动回收超时连接，如果没有开启 testOnBorrow，自动回收应当启用以防止超时被断开的连接
        dataSource.setRemoveAbandoned((!isTestOnBorrow()) ? true : isRemoveAbandoned());
        dataSource.setRemoveAbandonedTimeout(this.getRemoveAbandonedTimeout());
        dataSource.setLogAbandoned(this.isLogAbandoned());

        // 自定义配置
        Properties properties = new Properties();
        dataSource.setConnectProperties(properties);

        return dataSource;
    }

}
