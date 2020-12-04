package org.liujk.java.framework.base.utils.lang.beans.converter;

import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.lang.PrimitiveUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 说明：
 * <p>
 * {@link Boolean}的类型转换器。
 *
 */
public class BooleanTypeConverter extends AbstractTypeConverter<Boolean> {

    @Override
    public Class<Boolean> getTargetType () {
        return Boolean.class;
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
        if (value == null) {
            return null;
        }
        return BooleanPrimitiveTypeConverter.booleanValue (value) ? Boolean.TRUE : Boolean.FALSE;
    }
}