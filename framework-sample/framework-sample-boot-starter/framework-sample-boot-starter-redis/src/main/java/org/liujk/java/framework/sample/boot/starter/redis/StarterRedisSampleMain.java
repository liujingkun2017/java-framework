package org.liujk.java.framework.sample.boot.starter.redis;

import org.liujk.java.framework.boot.starter.core.FrameworkBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@FrameworkBootApplication(appName = "sample-boot-starter-redis", httpPort = 8080)
public class StarterRedisSampleMain {

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext =
                new SpringApplication(StarterRedisSampleMain.class).run(args);

        System.out.println("启动应用");
    }

}
