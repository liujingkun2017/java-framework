package org.liujk.java.framework.boot.starter.jdbc;

import org.liujk.java.framework.boot.starter.jdbc.config.JDBCProperties;
import org.liujk.java.framework.boot.starter.jdbc.druid.DruidProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties({DruidProperties.class})
@ConditionalOnProperty(prefix = JDBCProperties.PREFIX, name = "enable", matchIfMissing = true)
public class JDBCAutoConfiguration {

    @Autowired
    private DruidProperties druidProperties;

    @Bean
    public DataSource dataSource() {
        return druidProperties.build();
    }

}
