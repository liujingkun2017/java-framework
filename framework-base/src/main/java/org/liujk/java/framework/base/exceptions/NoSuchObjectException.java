package org.liujk.java.framework.base.exceptions;

public class NoSuchObjectException extends ResourceNotFoundException {

    public NoSuchObjectException(Object name, String message, Throwable cause) {
        super(name, message(name) + message, cause);
    }

    public NoSuchObjectException(Object name, String message) {
        super(name, message(name) + message, null);
    }

    public NoSuchObjectException(Object name) {
        super(name, message(name), null);
    }

    public NoSuchObjectException(String message, Throwable cause) {
        super(null, message, cause);
    }

    public NoSuchObjectException(String message) {
        super(null, message, null);
    }

    private static String message(Object name) {
        if (name == null) {
            return "";
        }
        return "object " + name + "not found";
    }

}
