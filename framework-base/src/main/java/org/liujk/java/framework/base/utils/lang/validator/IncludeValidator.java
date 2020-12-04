package org.liujk.java.framework.base.utils.lang.validator;

import org.liujk.java.framework.base.utils.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IncludeValidator implements ConstraintValidator<Include, String> {


    //包含的字符串
    private String[] include;

    //是否区分大小写
    private boolean ignoreCase = false;

    private String messageTemplate;

    @Override
    public void initialize(Include constraintAnnotation) {

        include = constraintAnnotation.include();
        if (include == null || include.length == 0) {
            include = constraintAnnotation.value();
            messageTemplate = constraintAnnotation.message();
        } else {
            messageTemplate = constraintAnnotation.message().replace("\\{value\\}", "{include}");
        }
        ignoreCase = constraintAnnotation.ignoreCase();

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (StringUtils.isNotBlank(value)) {
            for (String chk : include) {
                if ((ignoreCase && value.equalsIgnoreCase(chk))
                        || (!ignoreCase && value.equals(chk))) {
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
