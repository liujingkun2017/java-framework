package org.liujk.java.framework.base.utils.lang.validator;

import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.base.utils.lang.validator.impl.MobileNoPredicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MobileNoValidator implements ConstraintValidator<MobileNo, String> {

    private boolean nullable;
    private boolean blankable;
    private String message;

    @Override
    public void initialize(MobileNo mobileNo) {
        nullable = mobileNo.nullable();
        message = StringUtils.defaultIfBlank(mobileNo.message(),
                "{validator.MobileNo.message}");
        blankable = mobileNo.blankable();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isEmpty(value)) {
            return true;
        }
        if (!MobileNoPredicate.INSTANCE.apply(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
