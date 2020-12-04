
package org.liujk.java.framework.base.utils.lang.beans.converter;


import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.lang.PrimitiveUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 说明：
 * <p>
 * float数组的类型转换器。
 *
 */
public class FloatPrimitiveArrayTypeConverter extends ArrayTypeConverterSupport<float[]> {

    public FloatPrimitiveArrayTypeConverter (TypeConverterManager typeConverterManager) {
        super (typeConverterManager);
    }

    @Override
    public Class<float[]> getTargetType () {
        return float[].class;
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