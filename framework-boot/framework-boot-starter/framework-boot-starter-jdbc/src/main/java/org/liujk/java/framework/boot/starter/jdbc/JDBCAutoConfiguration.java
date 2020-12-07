package org.liujk.java.framework.boot.starter.jdbc;

import org.liujk.java.framework.boot.starter.jdbc.config.JDBCProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = JDBCProperties.PREFIX, name = "enable", matchIfMissing = true)
public class JDBCAutoConfiguration {

}
