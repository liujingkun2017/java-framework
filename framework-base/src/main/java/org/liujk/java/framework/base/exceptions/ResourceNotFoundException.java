package org.liujk.java.framework.base.exceptions;

public class ResourceNotFoundException extends SystemException {

    /**
     * 资源名
     */
    protected Object name;

    protected ResourceNotFoundException() {
        this(null, null, null);
    }

    protected ResourceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.name = null;
    }

    public ResourceNotFoundException(Object name, String message) {
        this(name, message, null);
    }

    public ResourceNotFoundException(Object name) {
        this(name, null, null);
    }

    public ResourceNotFoundException(Object name, String message, Throwable cause) {
        super(message(name) + message, cause);
        this.name = name;
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        this(null, message, cause);
    }

    public ResourceNotFoundException(String message) {
        this(null, message, null);
    }

    public ResourceNotFoundException(Throwable cause) {
        this(null, null, cause);
    }

    private static String message(Object name) {
        if (name == null) {
            return "";
        }
        return "resource " + name + "not found";
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public Object getName() {
        return name;
    }

    @Override
    public String toString() {
        String message = getLocalizedMessage();
        return getClass().getName() + "[name=" + this.name + "]" + (message == null ? "" : message);
    }
}
