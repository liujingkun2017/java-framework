package org.liujk.java.framework.base.utils.lang.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {OptionValidator.class})
public @interface Option {

    String message() default "不正确的可选值";

    int[] value()default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
