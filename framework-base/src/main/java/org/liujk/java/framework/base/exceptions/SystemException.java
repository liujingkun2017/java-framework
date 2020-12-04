package org.liujk.java.framework.base.exceptions;

import org.liujk.java.framework.base.api.CommonResultCode;
import org.liujk.java.framework.base.api.response.ResultCodeable;
import org.liujk.java.framework.base.enums.Status;

public class SystemException extends RunException {

    public static final ResultCodeable DEFAULT_RESULT_CODE = CommonResultCode.SYS_EXCEPTION;

    private Status status = Status.FAIL;

    public SystemException(ResultCodeable resultCode) {
        super(resultCode);
    }

    public SystemException() {
        this(DEFAULT_RESULT_CODE);
    }

    public SystemException(Status status) {
        this(status, DEFAULT_RESULT_CODE);
    }

    public SystemException(String message) {
        this(DEFAULT_RESULT_CODE, message);
    }

    public SystemException(Status status, String message) {
        this(DEFAULT_RESULT_CODE, message);
        setStatus(status);
    }

    public SystemException(Status status, String message, Throwable cause) {
        this(status, DEFAULT_RESULT_CODE, message, cause);
    }

    public SystemException(ResultCodeable resultCode, String message) {
        super(resultCode, message);
    }

    public SystemException(Status status, ResultCodeable resultCode) {
        this(resultCode);
        setStatus(status);

    }

    public SystemException(Status status, Throwable cause) {
        this(status, DEFAULT_RESULT_CODE, cause);
    }

    public SystemException(String message, Throwable cause) {
        this(DEFAULT_RESULT_CODE, message, cause);
    }

    public SystemException(ResultCodeable resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }

    public SystemException(Throwable cause) {
        this(DEFAULT_RESULT_CODE, cause);
    }

    public SystemException(ResultCodeable resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public SystemException(Status status, ResultCodeable resultCode, Throwable cause) {
        this(resultCode, cause);
        setStatus(status);
    }

    public SystemException(Status status, ResultCodeable resultCode, String message) {
        this(resultCode, message);
        setStatus(status);
    }

    public SystemException(Status status, ResultCodeable resultCode, String message, Throwable cause) {
        this(resultCode, message, cause);
        setStatus(status);
    }

    protected SystemException(ResultCodeable resultCode, String message, Throwable cause,
                              boolean enableSuppression, boolean writableStackTrace) {
        super(resultCode, message, cause, enableSuppression, writableStackTrace);
    }

    protected SystemException(Status status, ResultCodeable resultCode, String message,
                              Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this(resultCode, message, cause, enableSuppression, writableStackTrace);
        setStatus(status);
    }

    protected SystemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this(DEFAULT_RESULT_CODE, message, cause, enableSuppression, writableStackTrace);
    }

    protected SystemException(Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this(DEFAULT_RESULT_CODE, message, cause, enableSuppression, writableStackTrace);
        setStatus(status);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
