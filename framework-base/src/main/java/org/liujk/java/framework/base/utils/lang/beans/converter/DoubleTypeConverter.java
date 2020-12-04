
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
 * {@link Double}的类型转换器。
 *
 */
public class DoubleTypeConverter extends AbstractTypeConverter<Double> {

    @Override
    public Class<Double> getTargetType () {
        return Double.class;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        List<Class<?>> classes = new ArrayList<Class<?>> ();
        CollectionUtils.add (classes, PrimitiveUtils.getAllPrimitiveClasses ());
        CollectionUtils.add (classes, PrimitiveUtils.getAllWrapperClasses ());
        classes.add (Object[].class);
        classes.add (Collection.class);
        classes.add (Money.class);
        classes.add (CharSequence.class);
        classes.add (CharSequence[].class);
        return classes;
    }

    @Override
    public Double convert (Object value, Class<? extends Double> toType) {
        try {
            if (value == null) {
                return null;
            }
            if (value instanceof Money) {
                Money amount = (Money) value;
                return amount.getCent () / (double) amount.getCentFactor ();
            }
            return Double.valueOf (DoublePrimitiveTypeConverter.doubleValue (value));
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }
}
