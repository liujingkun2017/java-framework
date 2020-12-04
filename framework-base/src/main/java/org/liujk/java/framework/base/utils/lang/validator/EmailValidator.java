package org.liujk.java.framework.base.utils.lang.validator;

import org.apache.commons.lang3.ArrayUtils;
import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.base.utils.lang.validator.impl.EmailPredicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {

    private String[] mailServerList;
    private String message;

    @Override
    public void initialize(Email email) {
        mailServerList = email.mailServer();
        message = email.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isEmpty(value)) {
            return true;
        }

        if (!EmailPredicate.INSTANCE.apply(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(StringUtils.defaultIfBlank(message, "{validator.Email.message}"))
                    .addConstraintViolation();
            return false;
        }

        if (ArrayUtils.isNotEmpty(mailServerList)) {
            for (String mailServer : mailServerList) {
                if (value.endsWith("@" + mailServer)) {
                    return true;
                }
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(StringUtils.defaultIfBlank(message, "{validator.Email.message}"))
                    .addConstraintViolation();
            return false;
        }

        return false;
    }
}
