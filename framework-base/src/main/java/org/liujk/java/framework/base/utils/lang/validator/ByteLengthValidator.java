package org.liujk.java.framework.base.utils.lang.validator;

import org.liujk.java.framework.base.exceptions.Exceptions;
import org.liujk.java.framework.base.utils.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ByteLengthValidator implements ConstraintValidator<ByteLength, String> {

    private int min;
    private int max;
    private String charset;

    @Override
    public void initialize(ByteLength byteLength) {
        min = byteLength.min();
        max = byteLength.max();
        charset = byteLength.charset();
        if (min < 0) {
            throw Exceptions.newRuntimeExceptionWithoutStackTrace("min can not be less than 0");
        }
        if (max <= min) {
            throw Exceptions.newRuntimeExceptionWithoutStackTrace("max must be bigger than min");
        }
        if (StringUtils.isBlank(charset)) {
            charset = ByteLength.DEFAULT_CHARSET_NAME;
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //不做校验
        if (value == null) {
            return true;
        }
        int byteLen;
        try {
            byteLen = value.getBytes(charset).length;
            if (byteLen < min) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{validator.ByteLength.message.min}").addConstraintViolation();
                return false;
            }
            if (byteLen > max) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{validator.ByteLength.message.max}").addConstraintViolation();
                return false;
            }
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{validator.ByteLength.message.charset}").addConstraintViolation();
            return false;
        }
        return true;
    }
}
