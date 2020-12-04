
package org.liujk.java.framework.base.utils.lang.beans.converter;


import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.lang.ReflectionUtils;
import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.TypeConversionException;
import org.liujk.java.framework.base.utils.lang.beans.reference.InvocationTargetRunTimeException;

import java.util.*;

/**
 * 说明：
 * <p>
 * {@link Set}的类型转换器。
 *
 */
public class SetTypeConverter extends AbstractTypeConverter<Set<?>> {

    @SuppressWarnings("unchecked")
    public static <E extends Set<?>> E setValue (Object value, Class<? extends E> setClassType) {
        Set<Object> set = null;
        if ((Class<?>) setClassType == Set.class) {
            // 使用 HashSet 作为实现
            set = new HashSet<Object> ();
        } else { // 如果是具体的类则使用该类的类型
            try {
                set = (Set<Object>) ReflectionUtils.createObject (setClassType);
            } catch (InvocationTargetRunTimeException e) {
                throw new TypeConversionException(e.getTargetException ());
            } catch (Exception e) {
                throw new TypeConversionException (e);
            }
        }
        CollectionUtils.add (set, value);
        return (E) set;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Set<?>> getTargetType () {
        Class<?> setClass = Set.class;
        return (Class<Set<?>>) setClass;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        return Arrays.asList (Collection.class, Object[].class);
    }

    @Override
    public Set<?> convert (Object value, Class<? extends Set<?>> toType) {
        return setValue (value, toType);
    }
}
