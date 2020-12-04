
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
 * {@link Integer}的类型转换器。
 *
 */
public class IntegerTypeConverter extends AbstractTypeConverter<Integer> {

    @Override
    public Class<Integer> getTargetType () {
        return Integer.class;
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
            if (value == null) {
                return null;
            }
            if (value instanceof Money) {
                return Integer.valueOf (value.toString ());
            } else {
                return Integer.valueOf (IntTypeConverter.intValue (value));
            }
        } catch (NumberFormatException e) {
            throw new TypeConversionException(e);
        }
    }
}
