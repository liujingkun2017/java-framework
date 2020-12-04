package org.liujk.java.framework.base.utils.lang.beans.converter;



import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.lang.PrimitiveUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 说明：
 * <p>
 * boolean 的类型转换器。
 *
 */
public class BooleanPrimitiveTypeConverter extends AbstractTypeConverter<Boolean> {

    public static boolean booleanValue (Object value) {
        if (value == null) {
            return false;
        }
        Class<?> c = value.getClass ();
        if (c == Boolean.class) {
            return ((Boolean) value).booleanValue ();
        }
        if (c == Character.class) {
            return ((Character) value).charValue () != 0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue () != 0;
        }
        if (c == String.class) {
            return Boolean.parseBoolean ((String) value);
        }
        return true;
    }

    @Override
    public Class<Boolean> getTargetType () {
        return Boolean.TYPE;
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
    public Boolean convert (Object value, Class<? extends Boolean> toType) {
        return booleanValue (value);
    }
}