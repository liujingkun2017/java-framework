package org.liujk.java.framework.base.utils.lang.validator.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.UrlValidator;

import javax.annotation.Nullable;

public class HttpUrlPredicate implements Predicate<String> {

    public static final HttpUrlPredicate INSTANCE = new HttpUrlPredicate();
    private UrlValidator httpUrlValidator;

    private HttpUrlPredicate() {
        String[] schmes = {"http", "https"};
        RegexValidator regexValidator = new RegexValidator("[a-zA-Z0-9][a-zA-Z0-9\\.]{1,61}[a-zA-Z0-9]\\.[a-zA-Z]{2,}");
        httpUrlValidator = new UrlValidator(schmes, regexValidator, 0);
    }

    @Override
    public boolean apply(@Nullable String s) {
        return !Strings.isNullOrEmpty(s) && httpUrlValidator.isValid(s);
    }
}
