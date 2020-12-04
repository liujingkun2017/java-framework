package org.liujk.java.framework.base.exceptions;

import org.liujk.java.framework.base.api.response.ResultCodeable;

/**
 * 非法参数异常
 */
public class IllegalParameterException extends BizException {

    public IllegalParameterException(ResultCodeable resultCode) {
        super(resultCode);
    }

    public IllegalParameterException(ResultCodeable resultCode, String message) {
        super(resultCode, message);
    }

    public IllegalParameterException(ResultCodeable resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public IllegalParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalParameterException(ResultCodeable resultCode, String message, Throwable cause,
                                     boolean enableSuppression, boolean writableStackTrace) {
        super(resultCode, message, cause, enableSuppression, writableStackTrace);
    }

}
