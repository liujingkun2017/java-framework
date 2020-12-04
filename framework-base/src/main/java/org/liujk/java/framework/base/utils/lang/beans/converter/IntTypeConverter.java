
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
 * int 的类型转换器。
 *
 */
public class IntTypeConverter extends AbstractTypeConverter<Integer> {

    public static int intValue (Object value) throws NumberFormatException {
        if (value == null) {
            return 0;
        }
        Class<?> c = value.getClass ();
        if (c.getSuperclass () == Number.class) {
            return ((Number) value).intValue ();
        }
        if (c == Boolean.class) {
            return ((Boolean) value).booleanValue () ? 1 : 0;
        }
        if (c == Character.class) {
            return ((Character) value).charValue ();
        }
        return Integer.parseInt (StringTypeConverter.stringValue (value, true));
    }

    @Override
    public Class<Integer> getTargetType () {
        return Integer.TYPE;
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
        return classes;
    }

    @Override
    public Integer convert (Object value, Class<? extends Integer> toType) {
        try {
            if (value instanceof Money) {
                return Integer.valueOf (value.toString ());
            } else {
                return intValue (value);
            }
        } catch (NumberFormatException e) {
            throw new TypeConversionException(e);
        }
    }
}