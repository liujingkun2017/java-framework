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
 * byte 的类型转换器。
 *
 */
public class BytePrimitiveTypeConverter extends AbstractTypeConverter<Byte> {

    public static byte byteValue (Object value) throws NumberFormatException {
        return (byte) IntTypeConverter.intValue (value);
    }

    @Override
    public Class<Byte> getTargetType () {
        return Byte.TYPE;
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
    public Byte convert (Object value, Class<? extends Byte> toType) {
        try {
            return byteValue (value);
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }
}