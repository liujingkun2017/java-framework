package org.liujk.java.framework.base.utils.lang.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {HttpUrlValidator.class})
public @interface HttpUrl {

    String message() default "非法的http url地址";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
