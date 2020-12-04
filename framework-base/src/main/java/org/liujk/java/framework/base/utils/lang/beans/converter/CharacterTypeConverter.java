
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
 * {@link Character}的类型转换器。
 *
 */
public class CharacterTypeConverter extends AbstractTypeConverter<Character> {

    @Override
    public Class<Character> getTargetType () {
        return Character.class;
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
            if (value == null) {
                return null;
            }
            return Character.valueOf (CharTypeConverter.charValue (value));
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }
}