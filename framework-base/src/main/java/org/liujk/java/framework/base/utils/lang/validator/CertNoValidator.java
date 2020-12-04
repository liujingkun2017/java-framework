package org.liujk.java.framework.base.utils.lang.validator;

import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.base.utils.lang.validator.impl.CertNoPredicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CertNoValidator implements ConstraintValidator<CertNo, String> {

    private String message;

    @Override
    public void initialize(CertNo certNo) {
        message = certNo.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isEmpty(value)) {
            //null 不做校验
            return true;
        }

        if (!CertNoPredicate.INSTANCE.apply(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{validator.CertNo.message}").addConstraintViolation();
            return false;
        }
        return true;
    }
}
