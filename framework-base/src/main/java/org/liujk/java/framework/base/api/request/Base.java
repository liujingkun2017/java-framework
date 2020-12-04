package org.liujk.java.framework.base.api.request;



import org.liujk.java.framework.base.api.SerializableObject;
import org.liujk.java.framework.base.api.Validatable;
import org.liujk.java.framework.base.exceptions.ArgumentValidateException;
import org.liujk.java.framework.base.utils.lang.validator.ValidatorFactory;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class Base extends SerializableObject implements Validatable {

    public static final String SID_KEY = "sid";

    /**
     * 通过jsr303规范的注解来校验参数
     *
     * @param groups
     */
    @Override
    public void validate(Class<?>... groups) {
        Set<ConstraintViolation<Base>> constraintViolations = ValidatorFactory.INSTANCE
                .getValidator().validate(this, groups);
        validate(constraintViolations);
    }

    protected <T> void validate(Set<ConstraintViolation<T>> constraintViolations) {
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
