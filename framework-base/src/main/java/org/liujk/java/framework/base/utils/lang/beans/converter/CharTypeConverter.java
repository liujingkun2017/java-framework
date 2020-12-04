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
 * char 的类型转换器。
 *
 */
public class CharTypeConverter extends AbstractTypeConverter<Character> {

    public static char charValue (Object value) throws NumberFormatException {
        return (char) IntTypeConverter.intValue (value);
    }

    @Override
    public Class<Character> getTargetType () {
        return Character.TYPE;
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
    public Character convert (Object value, Class<? extends Character> toType) {
        try {
            return charValue (value);
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }
}