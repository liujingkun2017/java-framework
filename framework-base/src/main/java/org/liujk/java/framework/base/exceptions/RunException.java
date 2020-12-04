package org.liujk.java.framework.base.exceptions;

import org.liujk.java.framework.base.api.CommonResultCode;
import org.liujk.java.framework.base.api.response.ResultCodeable;
import org.liujk.java.framework.base.enums.CodeMessageable;

import java.io.PrintStream;
import java.io.PrintWriter;

public class RunException extends RuntimeException implements CodeMessageable {

    private ResultCodeable resultCode;

    public RunException() {
        super();
    }

    public RunException(String message) {
        super(message);
    }


    public RunException(Throwable cause) {
        super(cause);
    }

    public RunException(String message, Throwable cause) {
        super(message, cause);
    }

    public RunException(ResultCodeable resultCode) {
        super(resultCode.getMessage());
        setResultCode(resultCode);

    }

    public RunException(ResultCodeable resultCode, String message) {
        super(message);
        setResultCode(resultCode);

    }

    public RunException(ResultCodeable resultCode, Throwable cause) {
        this(resultCode, resultCode.getMessage(), cause);

    }

    public RunException(ResultCodeable resultCode, String message, Throwable cause) {
        super(message, cause);
        setResultCode(resultCode);
    }

    protected RunException(String message, Throwable cause, boolean enableSuppression,
                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    protected RunException(ResultCodeable resultCode, String message, Throwable cause,
                           boolean enableSuppression,
                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        setResultCode(resultCode);
    }


    public ResultCodeable getResultCode() {
        return resultCode;
    }

    protected void setResultCode(ResultCodeable resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String getCode() {
        return resultCode == null ? CommonResultCode.UNKNOWN_EXCEPTION.getCode() : resultCode.getCode();
    }

    @Override
    public String getMessage() {
        Throwable cause = getCause();
        if (null != cause) {
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            return cause.getMessage();
        } else {
            return super.getMessage();
        }
    }

    public String getOriginalMessage() {
        return super.getMessage();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        if (null == getCause()) {
            super.printStackTrace(s);
        } else {
            s.println(this);
            s.println("reason:");
            getCause().printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        if (null == getCause()) {
            super.printStackTrace(s);
        } else {
            s.println(this);
            s.println("reason:");
            getCause().printStackTrace(s);
        }
    }
}
