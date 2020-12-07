package org.liujk.java.framework.boot.starter.jdbc.druid;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.InitializingBean;

public class DruidDataSourceWrapper extends DruidDataSource implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
