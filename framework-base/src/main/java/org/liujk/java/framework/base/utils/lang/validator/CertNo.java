package org.liujk.java.framework.base.utils.lang.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 判断身份证格式是否正确，支持15位和18位
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CertNoValidator.class})
public @interface CertNo {

    String message() default "{validator.CertNo.message}";

    Class<?>[] group() default {};

    Class<? extends Payload>[] payload() default {};

}
