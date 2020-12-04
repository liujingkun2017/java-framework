package org.liujk.java.framework.base.utils.lang.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {MoneyConstraintValidator.class})
public @interface MoneyConstraint {

    String message() default "{validator.MoneyConstraint.message}";

    /**
     * 最小值
     * 单位：分
     * 在校验时包含此值
     *
     * @return
     */
    long min() default 0;

    /**
     * 最大值
     * 单位：分
     * 在校验时包含此值
     *
     * @return
     */
    long max() default Long.MAX_VALUE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
