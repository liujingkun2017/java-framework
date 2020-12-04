package org.liujk.java.framework.base.exceptions;

import org.liujk.java.framework.base.api.CommonResultCode;
import org.liujk.java.framework.base.api.response.ResultCodeable;
import org.liujk.java.framework.base.enums.Status;
import org.liujk.java.framework.base.utils.lang.StringUtils;

/**
 * 内部异常，通常为无法识别的异常
 */
public class NestException extends SystemException {

    public static final ResultCodeable DEFAULT_RESULT_CODE = CommonResultCode.NEST_EXCEPTION;

    public NestException() {
        super(DEFAULT_RESULT_CODE);
    }

    public NestException(Status status) {
        super(status, DEFAULT_RESULT_CODE);
    }

    public NestException(String message) {
        super(DEFAULT_RESULT_CODE, message);
    }

    public NestException(ResultCodeable resultCode) {
        super(resultCode);
    }

    public NestException(Status status, String message) {
        super(status, DEFAULT_RESULT_CODE, message);
    }

    public NestException(Throwable cause) {
        super(DEFAULT_RESULT_CODE, cause);
    }

    public NestException(Status status, Throwable cause) {
        super(status, DEFAULT_RESULT_CODE, cause);
    }

    public NestException(ResultCodeable resultCode, String message) {
        super(resultCode, message);
    }

    public NestException(Status status, ResultCodeable resultCode) {
        super(status, resultCode);
    }

    public NestException(Status status, ResultCodeable resultCode, String message) {
        super(status, resultCode, message);
    }

    public NestException(ResultCodeable resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public NestException(Status status, ResultCodeable resultCode, Throwable cause) {
        super(status, resultCode, cause);
    }

    public NestException(String message, Throwable cause) {
        super(DEFAULT_RESULT_CODE, message, cause);
    }

    public NestException(Status status, String message, Throwable cause) {
        super(status, DEFAULT_RESULT_CODE, message, cause);
    }

    public NestException(ResultCodeable resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }

    public NestException(Status status, ResultCodeable resultCode, String message, Throwable cause) {
        super(status, resultCode, message, cause);
    }

    protected NestException(String message, Throwable cause, boolean enableSuppression,
                            boolean writableStackTrace) {
        super(DEFAULT_RESULT_CODE, message, cause, enableSuppression, writableStackTrace);
    }

    protected NestException(Status status, String message, Throwable cause,
                            boolean enableSuppression, boolean writableStackTrace) {
        super(status, DEFAULT_RESULT_CODE, message, cause, enableSuppression, writableStackTrace);
    }

    protected NestException(ResultCodeable resultCode, String message, Throwable cause,
                            boolean enableSuppression, boolean writableStackTrace) {
        super(resultCode, message, cause, enableSuppression, writableStackTrace);
    }

    protected NestException(Status status, ResultCodeable resultCode, String message,
                            Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(status, resultCode, message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessage() {

        Throwable cause = getCause();
        String message = super.getMessage();
        if (cause != null) {
            StringBuilder sb = new StringBuilder();
            if (this.getStackTrace() != null && this.getStackTrace().length > 0) {
                sb.append(this.getStackTrace()[0]).append("\t");
                if (StringUtils.isNotBlank(message)) {
                    sb.append(message);
                    sb.append(";");
                }
                sb.append("nest exception message:").append(cause);
            }
            return sb.toString();
        } else {
            return message;
        }

    }
}
