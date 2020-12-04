package org.liujk.java.framework.base.utils.lang.validator;

import org.springframework.core.annotation.AliasFor;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ExcludeValidator.class})
public @interface Exclude {

    String message() default "字符串不能出现在所指定的值{value}当中，区分大小写({ignoreCase})";

    //排除的字符串
    @AliasFor("exclude")
    String[] value() default {};

    //排除的字符串
    @AliasFor("value")
    String[] exclude() default {};

    //是否区分大小写
    boolean ignoreCase() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
