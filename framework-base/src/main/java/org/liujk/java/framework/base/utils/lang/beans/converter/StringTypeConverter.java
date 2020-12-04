package org.liujk.java.framework.base.utils.lang.beans.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class StringTypeConverter extends AbstractTypeConverter<String> {

    public static String stringValue(Object value, boolean trim) {
        String result = null;
        if (value == null) {
            result = null;
        } else {
            if (value.getClass().isArray()) {
                StringBuilder builder = new StringBuilder();
                if (value instanceof Object[]) {
                    for (Object o : (Object[]) value) {
                        builder.append(o);
                    }
                } else {
                    int length = Array.getLength(value);
                    for (int i = 0; i < length; i++) {
                        builder.append(Array.get(value, i));
                    }
                }
                result = builder.toString();
            } else if (value.getClass().isEnum()) {
                try {
                    Method method = value.getClass().getMethod("getCode");
                    if (method != null) {
                        result = (String) method.invoke(value);
                    }
                } catch (Exception e) {
                }
            }
            if (result == null) {
                result = value.toString();
            }
            if (trim) {
                result = result.trim();
            }
        }
        return result;
    }

    public static String stringValue(Object value) {
        return stringValue(value, false);
    }

    @Override
    public Class<String> getTargetType() {
        return String.class;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes() {
        return Arrays.<Class<?>>asList(TypeConverterManager.ALL_SOURCE_TYPE_CLASS);
    }

    @Override
    public String convert(Object value, Class<? extends String> toType) {
        return stringValue(value);
    }
}
