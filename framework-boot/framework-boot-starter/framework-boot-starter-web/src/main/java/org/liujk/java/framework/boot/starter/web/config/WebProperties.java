package org.liujk.java.framework.boot.starter.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(WebProperties.PREFIX)
public class WebProperties {

    public static final String PREFIX = "starter.web";

    /**
     * http 缓存时间,-1=不设置,0=第二次请求需要和服务器协商,大于0=经过多少秒后才过期
     */
    private int cacheMaxAge = -1;

    public int getCacheMaxAge() {
        return cacheMaxAge;
    }

    public void setCacheMaxAge(int cacheMaxAge) {
        this.cacheMaxAge = cacheMaxAge;
    }
}
