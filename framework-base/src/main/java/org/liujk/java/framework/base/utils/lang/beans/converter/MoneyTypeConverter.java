
package org.liujk.java.framework.base.utils.lang.beans.converter;


import com.google.common.collect.Lists;
import org.liujk.java.framework.base.enums.Currency;
import org.liujk.java.framework.base.utils.lang.object.Money;

import java.util.List;

/**
 * 说明：
 * <p>
 * Money 类型转换器
 *
 */
public class MoneyTypeConverter extends AbstractTypeConverter<Money> {
    @Override
    public Class<Money> getTargetType () {
        return Money.class;
    }

    @Override
    public List<Class<?>> getSupportedSourceTypes () {
        List<Class<?>> classes = Lists.newArrayList ();
        classes.add (String.class);
        classes.add (long.class);
        classes.add (Long.class);
        classes.add (double.class);
        classes.add (Double.class);
        return classes;
    }

    @Override
    public Money convert (Object value, Class<? extends Money> toType) {
        if (value == null) {
            return null;
        }
        Money amount = null;
        if (value instanceof String) {
            amount = new Money ((String) value);
        } else if (value instanceof Long) {
            amount = new Money ((Long) value, Currency.getByCode (Money.DEFAULT_CURRENCY_CODE));
        } else if (value instanceof Double) {
            amount = new Money ((Double) value);
        }

        return amount;
    }
}
