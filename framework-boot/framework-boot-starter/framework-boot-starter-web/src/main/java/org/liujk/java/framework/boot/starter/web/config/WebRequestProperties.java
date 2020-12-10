package org.liujk.java.framework.boot.starter.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(WebRequestProperties.PREFIX)
public class WebRequestProperties {

    public static final String PREFIX = "starter.web.request";

}
