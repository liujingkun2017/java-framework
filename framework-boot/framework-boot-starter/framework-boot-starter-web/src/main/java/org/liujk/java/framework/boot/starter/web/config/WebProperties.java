package org.liujk.java.framework.boot.starter.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(WebProperties.PREFIX)
public class WebProperties {

    public static final String PREFIX = "starter.web";

    /**
     * 是否启用web组件
     */
    private boolean enable = true;

    /**
     * http 缓存时间,-1=不设置,0=第二次请求需要和服务器协商,大于0=经过多少秒后才过期
     */
    private int cacheMaxAge = -1;

}
