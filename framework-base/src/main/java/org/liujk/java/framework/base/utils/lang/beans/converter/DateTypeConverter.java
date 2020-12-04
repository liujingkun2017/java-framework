
package org.liujk.java.framework.base.utils.lang.beans.converter;


import org.liujk.java.framework.base.utils.lang.DateUtils;
import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.TypeConversionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 说明：
 * <p>
 * {@link Date}的类型转换器。
 *
 */
public class DateTypeConverter extends AbstractTypeConverter<Date> {

    private static String MILLISECOND_FORMAT = ".SSS";

    private static ThreadLocal<SimpleDateFormat[]> threadLocal = new ThreadLocal<SimpleDateFormat[]> ();

    public static Date dateValue (Object value, Class<? extends Date> toType) {
        Date result = null;
        if (value != null && value.getClass ().isArray ()
                && String.class.isAssignableFrom (value.getClass ().getComponentType ())) {
            value = StringTypeConverter.stringValue (value);
        }
        if (value != null && value instanceof String && ((String) value).length () > 0) {
            String sa = (String) value;
            Locale locale = Locale.getDefault ();

            DateFormat df = null;
            if (java.sql.Time.class == toType) {
                df = DateFormat.getTimeInstance (DateFormat.MEDIUM, locale);
            } else if (java.sql.Timestamp.class == toType) {
                Date check = null;
                SimpleDateFormat dtfmt = (SimpleDateFormat) DateFormat
                        .getDateTimeInstance (DateFormat.SHORT, DateFormat.MEDIUM, locale);
                SimpleDateFormat fullfmt = new SimpleDateFormat (
                        dtfmt.toPattern () + MILLISECOND_FORMAT, locale);

                SimpleDateFormat dfmt = (SimpleDateFormat) DateFormat
                        .getDateInstance (DateFormat.SHORT, locale);

                SimpleDateFormat[] fmts = {fullfmt, dtfmt, dfmt};
                for (SimpleDateFormat fmt : fmts) {
                    try {
                        check = fmt.parse (sa);
                        df = fmt;
                        if (check != null) {
                            break;
                        }
                    } catch (ParseException ignore) {
                    }
                }
            } else if (Date.class == toType) {
                Date check = null;

                for (SimpleDateFormat df1 : getDateFormats ()) {
                    try {
                        check = df1.parse (sa);
                        df = df1;
                        if (check != null) {
                            break;
                        }
                    } catch (ParseException ignore) {
                    }
                }
            }
            if (df == null) {
                df = DateFormat.getDateInstance (DateFormat.SHORT, locale);
            }
            try {
                df.setLenient (false);
                result = df.parse (sa);
                if (!(Date.class.equals (toType))) {
                    try {
                        Constructor<? extends Date> constructor = toType
                                .getConstructor (new Class[]{long.class});
                        return constructor
                                .newInstance (new Object[]{Long.valueOf (result.getTime ())});
                    } catch (InvocationTargetException e) {
                        throw new TypeConversionException(e.getTargetException ());
                    } catch (Exception e) {
                        throw new TypeConversionException ("没有默认的构造函数[default(long)]。", e);
                    }
                }
            } catch (ParseException e) {
                throw new TypeConversionException ("无法解析date", e);
            }
        } else if (Date.class.isInstance (value)) {
            result = (Date) value;
        }
        return result;
    }

    private static SimpleDateFormat[] getDateFormats () {
        SimpleDateFormat[] dfs = threadLocal.get ();

        if (dfs == null) {
            Locale locale = Locale.getDefault ();

            // 添加常用的格式
            SimpleDateFormat simple = (SimpleDateFormat) DateUtils
                    .getFormat (DateUtils.DEFAULT_DATE_FORMAT_STRING);
            SimpleDateFormat dtLong = (SimpleDateFormat) DateUtils
                    .getFormat (DateUtils.LONG_DATE_FORMAT_STRING);
            SimpleDateFormat simpleFormat = (SimpleDateFormat) DateUtils
                    .getFormat (DateUtils.SIMPLE_DATE_TO_MINUTE_FORMAT_STRING);
            SimpleDateFormat dtSimple = (SimpleDateFormat) DateUtils
                    .getFormat (DateUtils.SIMPLE_DATE_FORMAT_STRING);
            SimpleDateFormat dtShort = (SimpleDateFormat) DateUtils
                    .getFormat (DateUtils.SHORT_DATE_FORMAT_STRING);
            SimpleDateFormat dtSimpleSlash = (SimpleDateFormat) DateUtils
                    .getFormat (DateUtils.SIMPLE_DATE_FORMAT_STRING_SLASH);

            SimpleDateFormat simpleFull = (SimpleDateFormat) DateUtils.getFormat (
                    DateUtils.DEFAULT_DATE_FORMAT_STRING + DateTypeConverter.MILLISECOND_FORMAT);
            SimpleDateFormat dtLongFull = (SimpleDateFormat) DateUtils.getFormat (
                    DateUtils.LONG_DATE_FORMAT_STRING + DateTypeConverter.MILLISECOND_FORMAT);

            SimpleDateFormat d1 = (SimpleDateFormat) DateFormat
                    .getDateTimeInstance (DateFormat.SHORT, DateFormat.LONG, locale);
            SimpleDateFormat d2 = (SimpleDateFormat) DateFormat
                    .getDateTimeInstance (DateFormat.SHORT, DateFormat.MEDIUM, locale);
            SimpleDateFormat d3 = (SimpleDateFormat) DateFormat
                    .getDateTimeInstance (DateFormat.SHORT, DateFormat.SHORT, locale);
            // 美国语言的格式......
            SimpleDateFormat us = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss");

            dfs = new SimpleDateFormat[]{simple, dtLong, simpleFull, dtLongFull, simpleFormat,
                                         dtSimple, dtShort, dtSimpleSlash, d1, d2, d3, us};
            threadLocal.set (dfs);
        }
        return dfs;
    }

    @Override
    public Class<Date> getTargetType () {
        return Date.class;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        return Arrays.asList (CharSequence.class, String[].class, Date.class);
    }

    @Override
    public Date convert (Object value, Class<? extends Date> toType) {
        try {
            return dateValue (value, toType);
        } catch (TypeConversionException e) {
            throw e;
        } catch (Exception e) {
            throw new TypeConversionException (e);
        }
    }
}