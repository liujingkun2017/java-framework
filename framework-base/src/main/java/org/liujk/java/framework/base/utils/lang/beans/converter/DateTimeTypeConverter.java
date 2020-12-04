
package org.liujk.java.framework.base.utils.lang.beans.converter;



import org.joda.time.DateTime;
import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.TypeConversionException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 说明：
 * <p>
 *
 */
public class DateTimeTypeConverter extends AbstractTypeConverter<DateTime> {


    @Override
    public DateTime convert (Object value, Class<? extends DateTime> toType) {
        if (value != null) {
            Date date;
            if (value instanceof Date) {
                date = (Date) value;
            } else {
                try {
                    date = DateTypeConverter.dateValue (value, Date.class);
                } catch (ClassCastException e) {
                    throw new TypeConversionException(e);
                }
            }
            return new DateTime (date.getTime ());
        }
        return null;
    }

    @Override
    public Class<DateTime> getTargetType () {
        return DateTime.class;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        return Arrays.asList (CharSequence.class, String[].class, Date.class);
    }
}
