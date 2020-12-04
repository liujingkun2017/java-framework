
package org.liujk.java.framework.base.utils.lang.beans.converter;


import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.TypeConversionException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * 说明：
 * <p>
 * {@link Enum}的类型转换器。
 *
 */
public class EnumTypeConverter extends AbstractTypeConverter<Enum<? extends Enum<?>>> {

    @SuppressWarnings("unchecked")
    private static Enum<?> enumValue0 (Class<? extends Enum> toClass, Object o) {
        Enum<?> result = null;
        if (o == null) {
            result = null;
        } else if (o instanceof String[]) {
            result = Enum.valueOf (toClass, ((String[]) o)[0]);
        } else if (o instanceof String) {
            try {
                result = Enum.valueOf (toClass, (String) o);
            } catch (IllegalArgumentException e) {
                // 枚举常量不存在，使用getByCode方法获取
                try {
                    Method getByCode = toClass.getMethod ("getByCode", String.class);
                    if (getByCode != null && Modifier.isStatic (getByCode.getModifiers ())) {
                        return (Enum) getByCode.invoke (null, o);
                    }
                } catch (Exception ex) {
                    throw e;
                }
            }

        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E enumValue (Class<E> toClass, Object o) {
        return (E) enumValue0 (toClass, o);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Enum<? extends Enum<?>>> getTargetType () {
        Class<?> enumClass = Enum.class;
        return (Class<Enum<? extends Enum<?>>>) enumClass;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        return Arrays.asList (CharSequence.class, String.class, String[].class);
    }

    @Override
    public Enum<? extends Enum<?>> convert (Object value,
                                            Class<? extends Enum<? extends Enum<?>>> toType) {
        try {
            return (Enum<? extends Enum<?>>) enumValue0 (toType, value);
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }
}
