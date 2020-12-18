package org.liujk.java.framework.boot.starter.kafka.config;


import lombok.Data;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(KafkaAutoProperties.PREFIX)
public class KafkaAutoProperties extends KafkaProperties {

    public static final String PREFIX = "starter.kafka";

    /**
     * 是否启用kafka组件
     */
    private boolean enable = true;

}
