package org.liujk.java.framework.base.utils.lang.validator;

import javax.validation.Validation;
import javax.validation.Validator;

public enum ValidatorFactory {

    INSTANCE {
        @Override
        public Validator getValidator() {
            return factory.getValidator();
        }

        ValidatorFactory factory = (ValidatorFactory) Validation.buildDefaultValidatorFactory();
    };

    public abstract Validator getValidator();

}
