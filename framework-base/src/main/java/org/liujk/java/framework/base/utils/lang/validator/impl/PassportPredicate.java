
package org.liujk.java.framework.base.utils.lang.validator.impl;



import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 说明：
 * <p>
 * 护照格式验证。
 * <ul>
 * <li>支持字母和数字，只支持半角</li>
 * </ul>
 *
 */
public class PassportPredicate implements Predicate<String> {

    public static final Pattern VALID_PASSPORT_REGEX = Pattern
            .compile ("^[\\dA-Za-z]{1,}$");

    public static final PassportPredicate INSTANCE = new PassportPredicate ();

    private PassportPredicate () {

    }

    @Override
    public boolean apply (String input) {
        if (Strings.isNullOrEmpty (input)) {
            return false;
        }
        Matcher matcher = VALID_PASSPORT_REGEX.matcher (input);
        return matcher.find ();
    }
}
