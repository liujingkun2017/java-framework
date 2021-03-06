package org.liujk.java.framework.base.utils.lang.beans.converter;

import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.TypeConversionException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class BigIntegerTypeConverter extends AbstractTypeConverter<BigInteger> {

    public static BigInteger bigIntValue(Object value) throws NumberFormatException {
        if (value == null) {
            return null;
        }
        Class<?> c = value.getClass();
        if (c == BigInteger.class) {
            return (BigInteger) value;
        }
        if (c == BigDecimal.class) {
            return ((BigDecimal) value).toBigInteger();
        }
        if (c.getSuperclass() == Number.class) {
            return BigInteger.valueOf(((Number) value).longValue());
        }
        if (c == Boolean.class) {
            return BigInteger.valueOf(((Boolean) value).booleanValue() ? 1 : 0);
        }
        if (c == Character.class) {
            return BigInteger.valueOf(((Character) value).charValue());
        }
        return new BigInteger(StringTypeConverter.stringValue(value, true));
    }

    @Override
    public Class<BigInteger> getTargetType() {
        return BigInteger.class;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes() {
        return Arrays.asList(Number.class, Boolean.class, Character.class, CharSequence.class,
                CharSequence[].class, BigDecimal.class);
    }

    @Override
    public BigInteger convert(Object value, Class<? extends BigInteger> toType) {
        try {
            return bigIntValue(value);
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }
}
