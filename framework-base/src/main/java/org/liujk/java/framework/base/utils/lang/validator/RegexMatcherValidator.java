package org.liujk.java.framework.base.utils.lang.validator;

import org.liujk.java.framework.base.utils.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegexMatcherValidator implements ConstraintValidator<RegexMatcher, String> {

    //正则表达式
    private String[] regex;

    private String messageTemplate;

    @Override
    public void initialize(RegexMatcher regexMatcher) {
        regex = regexMatcher.regex();
        if (regex == null || regex.length == 0) {
            regex = regexMatcher.value();
            messageTemplate = regexMatcher.message();
        } else {
            messageTemplate = regexMatcher.message().replaceAll("\\{value\\}", "{regex}");
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isNotBlank(value)) {
            for (String reg : regex) {
                if (value.matches(reg)) {
                    return true;
                }
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(messageTemplate)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
