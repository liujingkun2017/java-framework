package org.liujk.java.framework.base.utils.lang.beans.converter.exceptions;

import org.liujk.java.framework.base.exceptions.RunException;

public class TypeConversionException extends RunException {

    public TypeConversionException() {
        super();
    }

    public TypeConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeConversionException(String message) {
        super(message);
    }

    public TypeConversionException(Throwable cause) {
        super(cause);
    }
}
