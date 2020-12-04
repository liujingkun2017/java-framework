package org.liujk.java.framework.base.utils.lang.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EmailValidator.class})
public @interface Email {

    String message() default "{validator.Email.message}";

    String[] mailServer() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
