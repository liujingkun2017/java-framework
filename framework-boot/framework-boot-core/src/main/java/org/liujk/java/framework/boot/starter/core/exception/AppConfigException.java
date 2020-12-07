package org.liujk.java.framework.boot.starter.core.exception;


import org.liujk.java.framework.base.exceptions.AppException;

/**
 * 说明：
 * <p>
 * 应用配置异常
 */
public class AppConfigException extends AppException {
    public AppConfigException(Throwable cause) {
        super(cause);
    }

    public AppConfigException(String message) {
        super(message);
    }

    public AppConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
