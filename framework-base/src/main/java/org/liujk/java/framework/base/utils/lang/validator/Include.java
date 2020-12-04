package org.liujk.java.framework.base.utils.lang.validator;


import org.springframework.core.annotation.AliasFor;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IncludeValidator.class})
public @interface Include {

    String message() default "字符串必须在{value}所指定的值当中，区分大小写({ignoreCase})";

    //包含的字符串
    @AliasFor("include")
    String[] value() default {};

    //包含的字符串
    @AliasFor("value")
    String[] include() default {};

    //是否区分大小写
    boolean ignoreCase() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
