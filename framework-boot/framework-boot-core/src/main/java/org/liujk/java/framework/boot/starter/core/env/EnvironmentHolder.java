package org.liujk.java.framework.boot.starter.core.env;

import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

public class EnvironmentHolder implements EnvironmentAware {

    private static Environment environment;

    private static void set(Environment env) {
        environment = env;
    }

    public static Environment get() {
        return environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (EnvironmentHolder.environment == null) {
            if (environment != null) {
                set(environment);
            }
        }
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        return environment.getProperty(key, targetType);
    }

    public static String getProperty(String key) {
        return environment == null ? System.getProperty(key) : environment.getProperty(key);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return environment.getProperty(key, targetType, defaultValue);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public static <T> T getProperty(String prefix, String key, Class<T> targetType, T defaultValue) {
        return environment
                .getProperty((StringUtils.isBlank(prefix) ? "" : (prefix + ".")) + resolvePropertyName(prefix, key),
                        targetType, defaultValue);
    }

    public static String getProperty(String prefix, String key, String defaultValue) {
        String value = getProperty(
                (StringUtils.isBlank(prefix) ? "" : (prefix + ".")) + resolvePropertyName(prefix, key));
        return value != null ? value : defaultValue;
    }

    public static <T> T getProperty(String prefix, String key, Class<T> targetType) {
        return environment
                .getProperty((StringUtils.isBlank(prefix) ? "" : (prefix + ".")) + resolvePropertyName(prefix, key),
                        targetType);
    }

    public static String resolvePropertyName(String prefix, String name) {
        Assert.notNull(name, "Property name must not be null");

        String fullPrefix = (StringUtils.isBlank(prefix) ? "" : (prefix + "."));

        if (EnvironmentHolder.containsProperty(fullPrefix + name)) {
            return name;
        }

        String usName = name.replace('.', '_');
        if (!name.equals(usName) && EnvironmentHolder.containsProperty(fullPrefix + usName)) {
            return usName;
        }

        String ucName = name.toUpperCase();
        if (!name.equals(ucName)) {
            if (EnvironmentHolder.containsProperty(fullPrefix + ucName)) {
                return ucName;
            } else {
                String usUcName = ucName.replace('.', '_');
                if (!ucName.equals(usUcName) && EnvironmentHolder.containsProperty(fullPrefix + usUcName)) {
                    return usUcName;
                }
            }
        }

        return name;
    }

    public static boolean containsProperty(String name) {
        return environment.containsProperty(name);
    }

}
