
package org.liujk.java.framework.base.utils.lang.validator.impl;



import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 说明：
 * <p>
 * 台胞证格式验证。
 * <ul>
 * <li>台胞证由8位及8位以上字符组成</li>
 * <li>前中后不能有空格(20170113增加注释)</li>
 * </ul>
 *
 */
public class PassportTaiwanPredicate implements Predicate<String> {
    /**
     * []中是全角空格
     */
    public static final Pattern VALID_PASSPORT_TAIWAN_REGEX = Pattern
            .compile ("^[^\\s　]{8,}$");

    public static final PassportTaiwanPredicate INSTANCE = new PassportTaiwanPredicate ();

    private PassportTaiwanPredicate () {

    }

    @Override
    public boolean apply (String input) {
        if (Strings.isNullOrEmpty (input)) {
            return false;
        }
        Matcher matcher = VALID_PASSPORT_TAIWAN_REGEX.matcher (input);

        return matcher.find ();
    }
}
