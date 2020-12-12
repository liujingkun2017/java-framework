package org.liujk.java.framework.boot.starter.web.config;

import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(WebResponseProperties.PREFIX)
public class WebResponseProperties {

    public static final String PREFIX = "starter.web.response";

    private Map<String, String> headers = Maps.newHashMapWithExpectedSize(2);

    // todo
    // 默认响应头部
    public WebResponseProperties() {
        headers.put("x-xss-protection", "1; mode=block");
        headers.put("Cache-Control", "no-cache");
        headers.put("App-Name", "appName");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
