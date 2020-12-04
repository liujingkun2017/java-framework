package org.liujk.java.framework.base.utils.lang.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ByteLengthValidator.class})
public @interface ByteLength {

    public static final String DEFAULT_CHARSET_NAME = "UTF-8";

    String message() default "{validator.ByteLength.message}";

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String charset() default DEFAULT_CHARSET_NAME;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
