
package org.liujk.java.framework.base.utils.lang.validator.impl;



import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 说明：
 * <p>
 * 营业执照号格式验证。 营业执照格式:前后不能有空格，满足数字、字母、中文、长度不限
 *
 */
public class BusinessNoPredicate implements Predicate<String> {

    /**
     * 非空白自负、非全角的空格和tab \u4E00-\u9FA5 所有汉字
     */
    public static final Pattern VALID_BUSINESS_NO_REGEX = Pattern
            .compile ("(^[\\dA-Za-z\u4E00-\u9FA5]+$)");

    public static final BusinessNoPredicate INSTANCE = new BusinessNoPredicate ();

    private BusinessNoPredicate () {

    }

    @Override
    public boolean apply (String input) {
        if (Strings.isNullOrEmpty (input)) {
            return false;
        }
        Matcher matcher = VALID_BUSINESS_NO_REGEX.matcher (input);
        Matcher preSuffBlankMatcher = BlankInStringPreOrSuffPredicate.VALID_BLANKINSTRINGPREORSUFF_REGEX
                .matcher (input);

        return matcher.find () && !preSuffBlankMatcher.find ();
    }

}
