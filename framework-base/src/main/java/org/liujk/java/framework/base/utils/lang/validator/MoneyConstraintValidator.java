package org.liujk.java.framework.base.utils.lang.validator;

import org.liujk.java.framework.base.exceptions.Exceptions;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MoneyConstraintValidator implements ConstraintValidator<MoneyConstraint, String> {

    private long min;
    private long max;

    @Override
    public void initialize(MoneyConstraint moneyConstraint) {
        min = moneyConstraint.min();
        max = moneyConstraint.max();
        if (min < 0) {
            throw Exceptions.newRuntimeExceptionWithoutStackTrace("金额最小值不能小于零");
        }
        if (max <= min) {
            throw Exceptions.newRuntimeExceptionWithoutStackTrace("金额最大值必须大于最小值");
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        long moneyValue = Long.valueOf(value);
        if (moneyValue < min) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{validator.MoneyConstraint.message.min}")
                    .addConstraintViolation();
            return false;
        }

        if (moneyValue > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{validator.MoneyConstraint.message.max}")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
