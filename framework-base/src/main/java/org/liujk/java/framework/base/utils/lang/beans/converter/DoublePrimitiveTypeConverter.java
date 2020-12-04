
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
 * double 的类型转换器。
 *
 */
public class DoublePrimitiveTypeConverter extends AbstractTypeConverter<Double> {

    public static double doubleValue (Object value) throws NumberFormatException {
        if (value == null) {
            return 0.0;
        }
        Class<?> c = value.getClass ();
        if (c.getSuperclass () == Number.class) {
            return ((Number) value).doubleValue ();
        }
        if (c == Boolean.class) {
            return ((Boolean) value).booleanValue () ? 1 : 0;
        }
        if (c == Character.class) {
            return ((Character) value).charValue ();
        }
        String s = StringTypeConverter.stringValue (value, true);
        return (s.length () == 0) ? 0.0 : Double.parseDouble (s);
    }

    @Override
    public Class<Double> getTargetType () {
        return Double.TYPE;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        List<Class<?>> classes = new ArrayList<Class<?>> ();
        CollectionUtils.add (classes, PrimitiveUtils.getAllPrimitiveClasses ());
        CollectionUtils.add (classes, PrimitiveUtils.getAllWrapperClasses ());
        classes.add (Object[].class);
        classes.add (Money.class);
        classes.add (Collection.class);
        classes.add (CharSequence.class);
        classes.add (CharSequence[].class);
        return classes;
    }

    @Override
    public Double convert (Object value, Class<? extends Double> toType) {
        try {
            if (value != null && value instanceof Money) {
                Money amount = (Money) value;
                return amount.getCent () / (double) amount.getCentFactor ();
            }
            return doubleValue (value);
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }
}
