package org.liujk.java.framework.base.utils.lang.validator.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import org.apache.commons.validator.routines.EmailValidator;

import javax.annotation.Nullable;

public class EmailPredicate implements Predicate<String> {

    public static final EmailPredicate INSTANCE = new EmailPredicate();
    private EmailValidator emailValidator = EmailValidator.getInstance(false);

    private EmailPredicate() {

    }

    @Override
    public boolean apply(@Nullable String email) {
        return !Strings.isNullOrEmpty(email) && emailValidator.isValid(email);
    }
}
