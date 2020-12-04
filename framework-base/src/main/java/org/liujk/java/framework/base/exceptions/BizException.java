package org.liujk.java.framework.base.exceptions;

import org.liujk.java.framework.base.api.CommonResultCode;
import org.liujk.java.framework.base.api.response.ResultCodeable;
import org.liujk.java.framework.base.enums.Status;

public class BizException extends SystemException {

    public static final ResultCodeable DEFAULT_RESULT_CODE = CommonResultCode.BIZ_EXCEPTION;

    public BizException() {
        super(DEFAULT_RESULT_CODE);
    }

    public BizException(Status status) {
        super(status, DEFAULT_RESULT_CODE);
    }

    public BizException(String message) {
        super(DEFAULT_RESULT_CODE, message);
    }

    public BizException(ResultCodeable resultCode) {
        super(resultCode);
    }

    public BizException(Status status, String message) {
        super(status, DEFAULT_RESULT_CODE, message);
    }

    public BizException(Throwable cause) {
        super(DEFAULT_RESULT_CODE, cause);
    }

    public BizException(Status status, Throwable cause) {
        super(status, DEFAULT_RESULT_CODE, cause);
    }

    public BizException(ResultCodeable resultCode, String message) {
        super(resultCode, message);
    }

    public BizException(Status status, ResultCodeable resultCode) {
        super(status, resultCode);
    }

    public BizException(Status status, ResultCodeable resultCode, String message) {
        super(status, resultCode, message);
    }

    public BizException(ResultCodeable resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public BizException(Status status, ResultCodeable resultCode, Throwable cause) {
        super(status, resultCode, cause);
    }

    public BizException(String message, Throwable cause) {
        super(DEFAULT_RESULT_CODE, message, cause);
    }

    public BizException(Status status, String message, Throwable cause) {
        super(status, DEFAULT_RESULT_CODE, message, cause);
    }

    public BizException(Status status, ResultCodeable resultCode, String message, Throwable cause) {
        super(status, resultCode, message, cause);
    }

    protected BizException(String message, Throwable cause, boolean enableSuppression,
                           boolean writableStackTrace) {
        super(DEFAULT_RESULT_CODE, message, cause, enableSuppression, writableStackTrace);
    }

    protected BizException(Status status, String message, Throwable cause,
                           boolean enableSuppression, boolean writableStackTrace) {
        super(status, DEFAULT_RESULT_CODE, message, cause, enableSuppression, writableStackTrace);
    }

    protected BizException(ResultCodeable resultCode, String message, Throwable cause,
                           boolean enableSuppression, boolean writableStackTrace) {
        super(resultCode, message, cause, enableSuppression, writableStackTrace);
    }

    protected BizException(Status status, ResultCodeable resultCode, String message,
                           Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(status, resultCode, message, cause, enableSuppression, writableStackTrace);
    }

}
