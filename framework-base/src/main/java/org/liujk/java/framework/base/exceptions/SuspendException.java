package org.liujk.java.framework.base.exceptions;

import org.liujk.java.framework.base.api.response.ResultCodeable;
import org.liujk.java.framework.base.enums.Status;

/**
 * 挂起错误，当该异常产生时，不会回滚事务
 */
public class SuspendException extends SystemException {

    public SuspendException() {
        super(Status.PROCESSING);
    }

    public SuspendException(Status status) {
        super(status);
    }

    public SuspendException(String message) {
        super(message);
    }

    public SuspendException(ResultCodeable resultCode) {
        super(resultCode);
    }

    public SuspendException(Status status, String message) {
        super(status, message);
    }

    public SuspendException(Throwable cause) {
        super(cause);
    }

    public SuspendException(Status status, Throwable cause) {
        super(status, cause);
    }

    public SuspendException(ResultCodeable resultCode, String message) {
        super(resultCode, message);
    }

    public SuspendException(Status status, ResultCodeable resultCode) {
        super(status, resultCode);
    }

    public SuspendException(Status status, ResultCodeable resultCode, String message) {
        super(status, resultCode, message);
    }

    public SuspendException(ResultCodeable resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public SuspendException(Status status, ResultCodeable resultCode, Throwable cause) {
        super(status, resultCode, cause);
    }

    public SuspendException(String message, Throwable cause) {
        super(message, cause);
    }

    public SuspendException(Status status, String message, Throwable cause) {
        super(status, message, cause);
    }

    public SuspendException(ResultCodeable resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }

    public SuspendException(Status status, ResultCodeable resultCode, String message, Throwable cause) {
        super(status, resultCode, message, cause);
    }

    public SuspendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SuspendException(Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(status, message, cause, enableSuppression, writableStackTrace);
    }

    public SuspendException(ResultCodeable resultCode, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(resultCode, message, cause, enableSuppression, writableStackTrace);
    }

    public SuspendException(Status status, ResultCodeable resultCode, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(status, resultCode, message, cause, enableSuppression, writableStackTrace);
    }

}
