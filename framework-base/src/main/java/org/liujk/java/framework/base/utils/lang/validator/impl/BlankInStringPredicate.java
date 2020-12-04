
package org.liujk.java.framework.base.utils.lang.validator.impl;


import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 说明：
 * <p>
 * 校验字符串中是否存在空格字符。 入参为空或者格式匹配，返回true
 *
 */
public class BlankInStringPredicate implements Predicate<String> {
    /**
     * [] 中是全角空格
     */
    public static final Pattern VALID_BLANKINSTRING_REGEX = Pattern
            .compile ("[\\s　]");

    public static final BlankInStringPredicate INSTANCE = new BlankInStringPredicate ();

    private BlankInStringPredicate () {

    }

    @Override
    public boolean apply (String input) {
        if (Strings.isNullOrEmpty (input)) {
            return true;
        }
        Matcher matcher = VALID_BLANKINSTRING_REGEX.matcher (input);
        return matcher.find ();
    }
}
