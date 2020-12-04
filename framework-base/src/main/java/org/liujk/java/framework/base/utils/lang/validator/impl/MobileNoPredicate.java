package org.liujk.java.framework.base.utils.lang.validator.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileNoPredicate implements Predicate<String> {

    public static final MobileNoPredicate INSTANCE = new MobileNoPredicate();

    public static final Pattern VALID_MOBILE_NUMBER_REGEX = Pattern.compile(
            "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$", Pattern.CASE_INSENSITIVE);

    private MobileNoPredicate() {

    }

    @Override
    public boolean apply(@Nullable String s) {
        if (Strings.isNullOrEmpty(s)) {
            return false;
        }
        Matcher matcher = VALID_MOBILE_NUMBER_REGEX.matcher(s);
        return matcher.find();
    }
}
