
package org.liujk.java.framework.base.utils.lang.validator.impl;



import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 说明：
 * <p>
 * 经营范围格式验证。
 * <ul>
 * <li>不支持全数字</li>
 * <li>不支持全字母</li>
 * <li>不支持全符号</li>
 * <li>不允许出现 全部一样的字符如：“哈哈哈”</li>
 * <li>不能包含"无"字样</li>
 * </ul>
 *
 */
public class BusinessScopePredicate implements Predicate<String> {

    /**
     * \uFF10-\uFF19 全角 0-9 \uFF21-\uFF3A全角A-Z \uFF41-\uFF5A全角a-z \u65e0无
     */
    public static final Pattern VALID_BUSINESS_SCOPE_REGEX = Pattern.compile (
            "(^[\\d\uFF10-\uFF19]+$)|(^[A-Za-z\uFF21-\uFF3A\uFF41-\uFF5A]+$)|([!\\$\\%\\^*<>\\?\\~`！＄％＾＊＜＞？～｀￥…×])|(^[\u65e0]+$)");

    public static final BusinessScopePredicate INSTANCE = new BusinessScopePredicate ();

    private BusinessScopePredicate () {

    }

    @Override
    public boolean apply (String input) {
        if (Strings.isNullOrEmpty (input)) {
            return false;
        }
        Matcher matcher = VALID_BUSINESS_SCOPE_REGEX.matcher (input);
        Matcher continuousMatcher = PersonRealNamePredicate.VALID_CHAR_CONTINUOUS_REGEX
                .matcher (input);
        boolean hasPreOrSuffBlank = BlankInStringPreOrSuffPredicate.INSTANCE.apply (input);
        return !(matcher.find () || continuousMatcher.find () || hasPreOrSuffBlank);
    }

}
