package org.liujk.java.framework.boot.starter.web;

import org.liujk.java.framework.boot.starter.web.common.ResponseHeaderFilter;
import org.liujk.java.framework.boot.starter.web.config.WebProperties;
import org.liujk.java.framework.boot.starter.web.config.WebRequestProperties;
import org.liujk.java.framework.boot.starter.web.config.WebResponseProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({WebProperties.class,
        WebRequestProperties.class,
        WebResponseProperties.class})
public class WebStarterAutoConfiguration {

    /**
     * 响应头部处理
     *
     * @param responseHeaderProperties
     * @param webProperties
     * @return
     */
    @Bean
    public ResponseHeaderFilter responseHeaderFilter(WebResponseProperties responseHeaderProperties,
                                                     WebProperties webProperties) {
        ResponseHeaderFilter responseHeaderFilter =
                new ResponseHeaderFilter(responseHeaderProperties,
                        webProperties.getCacheMaxAge());
        return responseHeaderFilter;
    }

}
