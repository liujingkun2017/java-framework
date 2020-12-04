package org.liujk.java.framework.base.utils.lang.beans.converter.exceptions;

import org.liujk.java.framework.base.exceptions.NoSuchObjectException;

public class ConverterNotFoundException extends NoSuchObjectException {

    private Class<?> targetType;

    public ConverterNotFoundException(Class<?> sourceType, Class<?> targetType) {
        super(sourceType);
        this.targetType = targetType;
    }

    public ConverterNotFoundException(String message, Class<?> sourceType, Class<?> targetType) {
        super(sourceType, message);
        this.targetType = targetType;
    }

    public Class<?> getSourceType() {
        return (Class<?>) this.name;
    }

    public Class<?> getTargetType() {
        return this.targetType;
    }

    @Override
    public String toString() {
        String message = getLocalizedMessage();
        return getClass().getName() + "[sourceType=" + getSourceType() + ",targetType="
                + getTargetType() + "]" + (message == null ? "" : message);
    }
}
