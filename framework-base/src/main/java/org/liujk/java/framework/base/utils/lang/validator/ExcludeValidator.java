package org.liujk.java.framework.base.utils.lang.validator;

import org.liujk.java.framework.base.utils.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExcludeValidator implements ConstraintValidator<Exclude, String> {


    //排除的字符串
    private String[] exclude;

    //是否区分大小写
    private boolean ignoreCase = false;

    private String messageTemplate;

    @Override
    public void initialize(Exclude constraintAnnotation) {

        exclude = constraintAnnotation.exclude();
        if (exclude == null || exclude.length == 0) {
            exclude = constraintAnnotation.value();
            messageTemplate = constraintAnnotation.message();
        } else {
            messageTemplate = constraintAnnotation.message().replace("\\{value\\}", "{exclude}");
        }
        ignoreCase = constraintAnnotation.ignoreCase();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isNotBlank(value)) {
            for (String chk : exclude) {
                if ((ignoreCase && value.equalsIgnoreCase(chk))
                        || (!ignoreCase && value.equals(chk))) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(messageTemplate)
                            .addConstraintViolation();
                    return false;
                }
            }
            return true;
        }

        return true;
    }
}
