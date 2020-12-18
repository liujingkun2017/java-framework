package org.liujk.java.framework.sample.boot.starter.kafka;

import org.liujk.java.framework.boot.starter.core.FrameworkBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@FrameworkBootApplication(appName = "sample-boot-starter-kafka", httpPort = 8080)
public class StarterKafkaSampleMain {

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext =
                new SpringApplication(StarterKafkaSampleMain.class).run(args);

        System.out.println("启动应用");
    }

}
