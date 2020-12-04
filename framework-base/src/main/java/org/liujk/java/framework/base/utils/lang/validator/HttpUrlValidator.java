package org.liujk.java.framework.base.utils.lang.validator;

import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.base.utils.lang.validator.impl.HttpUrlPredicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HttpUrlValidator implements ConstraintValidator<HttpUrl, String> {

    private String message;

    @Override
    public void initialize(HttpUrl httpUrl) {
        message = StringUtils.defaultIfBlank(httpUrl.message(), "{validator.HttpUrl.message}");
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isEmpty(value)) {
            return true;
        }
        if (!HttpUrlPredicate.INSTANCE.apply(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
