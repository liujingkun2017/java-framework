
package org.liujk.java.framework.base.utils.lang.beans.converter;



import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.TypeConversionException;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 说明：
 * <p>
 *
 * @link Timestamp}的类型转换器。
 */
public class TimestampTypeConverter extends AbstractTypeConverter<Timestamp> {

    @Override
    public Class<Timestamp> getTargetType () {
        return Timestamp.class;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        return Arrays.asList (CharSequence.class, String[].class, Date.class);
    }

    @Override
    public Timestamp convert (Object value, Class<? extends Timestamp> toType) {
        try {
            return (Timestamp) DateTypeConverter.dateValue (value, toType);
        } catch (ClassCastException e) {
            throw new TypeConversionException(e);
        }
    }

}
