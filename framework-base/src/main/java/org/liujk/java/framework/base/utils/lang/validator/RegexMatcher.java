package org.liujk.java.framework.base.utils.lang.validator;


import org.springframework.core.annotation.AliasFor;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RegexMatcherValidator.class})
public @interface RegexMatcher {

    String message() default "参数格式不匹配表达式:{value}";

    //匹配的正则表达式
    @AliasFor("regex")
    String[] value() default {};

    //匹配的正则表达式
    @AliasFor("value")
    String[] regex() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
