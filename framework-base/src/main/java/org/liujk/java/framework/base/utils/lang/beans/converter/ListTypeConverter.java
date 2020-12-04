
package org.liujk.java.framework.base.utils.lang.beans.converter;



import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.lang.ReflectionUtils;
import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.TypeConversionException;
import org.liujk.java.framework.base.utils.lang.beans.reference.InvocationTargetRunTimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 说明：
 * <p>
 * {@link List}的类型转换器。
 *
 */
public class ListTypeConverter extends AbstractTypeConverter<List<?>> {

    @SuppressWarnings("unchecked")
    public static <E extends List<?>> E listValue (Object value, Class<? extends E> listClassType) {
        List<Object> list = null;
        if ((Class<?>) listClassType == List.class) {
            // 使用 ArrayList 作为实现
            list = new ArrayList<Object> ();
        } else { // 如果是具体的类则使用该类的类型
            try {
                list = (List<Object>) ReflectionUtils.createObject (listClassType);
            } catch (InvocationTargetRunTimeException e) {
                throw new TypeConversionException(e.getTargetException ());
            } catch (Exception e) {
                throw new TypeConversionException (e);
            }
        }
        CollectionUtils.add (list, value);
        return (E) list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<List<?>> getTargetType () {
        Class<?> listClass = List.class;
        return (Class<List<?>>) listClass;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        return Arrays.asList (Collection.class, Object[].class);
    }

    @Override
    public List<?> convert (Object value, Class<? extends List<?>> toType) {
        return listValue (value, toType);
    }
}
