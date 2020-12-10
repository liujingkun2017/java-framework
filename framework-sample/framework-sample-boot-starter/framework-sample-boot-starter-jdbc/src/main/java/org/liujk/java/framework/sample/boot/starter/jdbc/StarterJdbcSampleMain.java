package org.liujk.java.framework.sample.boot.starter.jdbc;

import org.liujk.java.framework.boot.starter.core.FrameworkBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@FrameworkBootApplication(appName = "sample-boot-starter-jdbc", httpPort = 8080)
public class StarterJdbcSampleMain {

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext
                = new SpringApplication(StarterJdbcSampleMain.class).run(args);

        System.out.println("启动应用");
    }

}
