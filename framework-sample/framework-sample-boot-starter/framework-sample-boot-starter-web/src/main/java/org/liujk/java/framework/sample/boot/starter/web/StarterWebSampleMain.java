package org.liujk.java.framework.sample.boot.starter.web;

import org.liujk.java.framework.boot.starter.core.FrameworkBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@FrameworkBootApplication(appName = "sample-boot-starter-web", httpPort = 8080)
public class StarterWebSampleMain {

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext =
                new SpringApplication(StarterWebSampleMain.class).run(args);

        System.out.println("启动应用");
    }

}
