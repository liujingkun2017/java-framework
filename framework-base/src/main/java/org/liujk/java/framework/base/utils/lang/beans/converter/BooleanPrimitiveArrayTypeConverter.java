package org.liujk.java.framework.base.utils.lang.beans.converter;



import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.lang.PrimitiveUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 说明：
 * <p>
 * boolean数组的类型转换器。
 *
 */
public class BooleanPrimitiveArrayTypeConverter extends ArrayTypeConverterSupport<boolean[]> {

    public BooleanPrimitiveArrayTypeConverter (TypeConverterManager typeConverterManager) {
        super (typeConverterManager);
    }

    @Override
    public Class<boolean[]> getTargetType () {
        return boolean[].class;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        List<Class<?>> classes = new ArrayList<Class<?>> ();
        CollectionUtils.add (classes, PrimitiveUtils.getAllPrimitiveArrayClasses ());
        CollectionUtils.add (classes, PrimitiveUtils.getAllWrapperArrayClasses ());
        classes.add (Object[].class);
        classes.add (Collection.class);
        classes.add (CharSequence[].class);
        return classes;
    }

}