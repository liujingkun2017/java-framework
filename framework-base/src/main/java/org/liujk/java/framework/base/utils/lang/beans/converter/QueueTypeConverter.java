
package org.liujk.java.framework.base.utils.lang.beans.converter;


import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.lang.ReflectionUtils;
import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.TypeConversionException;
import org.liujk.java.framework.base.utils.lang.beans.reference.InvocationTargetRunTimeException;

import java.util.*;

/**
 * 说明：
 * <p>
 * {@link Queue}的类型转换器。
 *
 */
public class QueueTypeConverter extends AbstractTypeConverter<Queue<?>> {

    @SuppressWarnings("unchecked")
    public static <E extends Queue<?>> E queueValue (Object value,
                                                     Class<? extends E> queueClassType) {
        Queue<Object> queue = null;
        if ((Class<?>) queueClassType == Queue.class) {
            // 使用 LinkedList 作为实现
            queue = new LinkedList<Object> ();
        } else { // 如果是具体的类则使用该类的类型
            try {
                queue = (Queue<Object>) ReflectionUtils.createObject (queueClassType);
            } catch (InvocationTargetRunTimeException e) {
                throw new TypeConversionException(e.getTargetException ());
            } catch (Exception e) {
                throw new TypeConversionException (e);
            }
        }
        CollectionUtils.add (queue, value);
        return (E) queue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Queue<?>> getTargetType () {
        Class<?> queueClass = Queue.class;
        return (Class<Queue<?>>) queueClass;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        return Arrays.asList (Collection.class, Object[].class);
    }

    @Override
    public Queue<?> convert (Object value, Class<? extends Queue<?>> toType) {
        return queueValue (value, toType);
    }
}
