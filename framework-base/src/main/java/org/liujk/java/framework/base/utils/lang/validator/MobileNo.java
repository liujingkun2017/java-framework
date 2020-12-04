package org.liujk.java.framework.base.utils.lang.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {MobileNoValidator.class})
public @interface MobileNo {

    String message() default "{validator.MobileNo.message}";

    //是否可以为null，默认不可为null
    boolean nullable() default false;

    boolean blankable() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
