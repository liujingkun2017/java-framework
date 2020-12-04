package org.liujk.java.framework.base.utils.lang.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OptionValidator implements ConstraintValidator<Option, Integer> {

    private int[] options = new int[]{};

    @Override
    public void initialize(Option option) {
        options = option.value();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        if (options.length == 0 || value == null) {
            return true;
        }
        boolean isValid = false;
        for (int i : options) {
            if (i == value) {
                isValid = true;
                break;
            }
        }

        return isValid;
    }
}
