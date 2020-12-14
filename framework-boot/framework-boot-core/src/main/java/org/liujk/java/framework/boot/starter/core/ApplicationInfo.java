package org.liujk.java.framework.boot.starter.core;

import org.liujk.java.framework.boot.starter.core.exception.AppConfigException;

public class ApplicationInfo {

    /**
     * 应用基本配置
     */
    public static final String APP_NAME = "appName";
    public static final String BASE_PACKAGE = "basePackage";

    /**
     * 获取应用名称
     *
     * @return
     */
    public static String getAppName() {
        String name = "test";// todo  EnvironmentHolder.getProperty(APP_NAME);
        if (name == null) {
            throw new AppConfigException("没有设置应用名称,请设置系统变量" + APP_NAME);
        }
        return name;
    }

    /**
     * 获取基础包名
     *
     * @return
     */
    public static String getBasePackage() {
        return "test";// todo EnvironmentHolder.getProperty(BASE_PACKAGE);
    }

}
