
package org.liujk.java.framework.base.utils.lang.beans.converter;


import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.lang.PrimitiveUtils;
import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.TypeConversionException;
import org.liujk.java.framework.base.utils.lang.object.Money;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 说明：
 * <p>
 * {@link Long}的类型转换器。
 *
 */
public class LongTypeConverter extends AbstractTypeConverter<Long> {

    @Override
    public Class<Long> getTargetType () {
        return Long.class;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        List<Class<?>> classes = new ArrayList<Class<?>> ();
        CollectionUtils.add (classes, PrimitiveUtils.getAllPrimitiveClasses ());
        CollectionUtils.add (classes, PrimitiveUtils.getAllWrapperClasses ());
        classes.add (Object[].class);
        classes.add (Collection.class);
        classes.add (CharSequence.class);
        classes.add (CharSequence[].class);
        classes.add (Money.class);
        return classes;
    }

    @Override
    public Long convert (Object value, Class<? extends Long> toType) {
        try {
            if (value == null) {
                return null;
            }
            if (value instanceof Money) {
                return ((Money) value).getCent ();
            } else {
                return Long.valueOf (LongPrimitiveTypeConverter.longValue (value));
            }
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }
}
