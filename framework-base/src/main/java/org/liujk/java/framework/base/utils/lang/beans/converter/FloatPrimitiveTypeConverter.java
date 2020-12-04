
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
 * float 的类型转换器。
 *
 */
public class FloatPrimitiveTypeConverter extends AbstractTypeConverter<Float> {

    public static float floatcharValue (Object value) throws NumberFormatException {
        return (float) DoublePrimitiveTypeConverter.doubleValue (value);
    }

    @Override
    public Class<Float> getTargetType () {
        return Float.TYPE;
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
    public Float convert (Object value, Class<? extends Float> toType) {
        try {
            if (value instanceof Money) {
                return Float.valueOf (value.toString ());
            } else {
                return floatcharValue (value);
            }
        } catch (NumberFormatException e) {
            throw new TypeConversionException(e);
        }
    }
}