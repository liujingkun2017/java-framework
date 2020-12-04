package org.liujk.java.framework.base.utils.lang.beans.converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArrayTypeConverter extends ArrayTypeConverterSupport<Object[]> {


    public ArrayTypeConverter(TypeConverterManager typeConverterManager) {
        super(typeConverterManager);
    }

    @Override
    public Class<Object[]> getTargetType() {
        return Object[].class;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes() {
        return Arrays.asList(Object[].class, Collection.class);
    }
}
