package org.liujk.java.framework.base.utils.lang.validator;

import org.liujk.java.framework.base.utils.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumConstraintValidator implements ConstraintValidator<EnumConstraint, String> {

    private String message;
    private Class clazz;
    private String method;

    @Override
    public void initialize(EnumConstraint enumConstraint) {
        message = enumConstraint.message();
        clazz = enumConstraint.value();
        method = enumConstraint.method();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isEmpty(value)) {
            return true;
        }

        //todo
        boolean checkFlag = false;
        if (StringUtils.isNotBlank(method)) {
            try {

            } catch (Exception e) {

            }
        }

        if (!checkFlag) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(StringUtils.defaultIfBlank(message, "{validator.EnumConstraint.message}"))
                    .addConstraintViolation();
            return false;
        }

        return false;
    }
}
