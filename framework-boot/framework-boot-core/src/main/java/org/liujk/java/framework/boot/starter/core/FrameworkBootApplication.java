package org.liujk.java.framework.boot.starter.core;

import java.lang.annotation.*;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootApplication
public @interface FrameworkBootApplication {

    /**
     * 系统名称
     *
     * @return
     */
    String appName();

    /**
     * 系统http服务端口
     *
     * @return
     */
    int httpPort() default 8080;

}
