package org.liujk.java.framework.base.utils.lang.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnumConstraintValidator.class})
public @interface EnumConstraint {

    String message() default "{validator.EnumConstraint.message}";

    //枚举class
    Class value();

    String method() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
