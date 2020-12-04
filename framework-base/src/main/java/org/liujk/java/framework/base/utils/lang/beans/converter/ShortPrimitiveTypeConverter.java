
package org.liujk.java.framework.base.utils.lang.beans.converter;


import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.lang.PrimitiveUtils;
import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.TypeConversionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 说明：
 * <p>
 * short 的类型转换器。
 *
 */
public class ShortPrimitiveTypeConverter extends AbstractTypeConverter<Short> {

    public static short shortValue(Object value) throws NumberFormatException {
        return (short) IntTypeConverter.intValue(value);
    }

    @Override
    public Class<Short> getTargetType() {
        return Short.TYPE;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        CollectionUtils.add(classes, PrimitiveUtils.getAllPrimitiveClasses());
        CollectionUtils.add(classes, PrimitiveUtils.getAllWrapperClasses());
        classes.add(Object[].class);
        classes.add(Collection.class);
        classes.add(CharSequence.class);
        classes.add(CharSequence[].class);
        return classes;
    }

    @Override
    public Short convert(Object value, Class<? extends Short> toType) {
        try {
            return shortValue(value);
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }
}
