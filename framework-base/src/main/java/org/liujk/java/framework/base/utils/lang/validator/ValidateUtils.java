package org.liujk.java.framework.base.utils.lang.validator;

import org.liujk.java.framework.base.exceptions.ArgumentValidateException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

public class ValidateUtils {

    /**
     * 通过jsr303规范的注解来校验传入对象的参数，当obj==null时，不做校验
     *
     * @param obj 被校验的对象
     * @param <T>
     */
    public static <T> void validate(T obj) {
        validate(obj, null);
    }

    /**
     * 通过jsr303规范的注解来校验传入对象的参数，当obj==null时，不做校验
     *
     * @param obj    被校验的对象
     * @param groups 校验分组
     * @param <T>
     */
    public static <T> void validate(T obj, Class<?>... groups) {
        if (obj == null) {
            return;
        }
        Validator validator = ValidatorFactory.INSTANCE.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = groups == null ? validator.validate(obj) : validator.validate(obj, groups);
        validate(constraintViolations);
    }

    protected static <T> void validate(Set<ConstraintViolation<T>> constraintViolations) {
        ArgumentValidateException exception = null;
        if (constraintViolations != null && !constraintViolations.isEmpty()) {
            exception = new ArgumentValidateException();
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                exception.addError(constraintViolation.getPropertyPath().toString(),
                        constraintViolation.getMessage());
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

}
