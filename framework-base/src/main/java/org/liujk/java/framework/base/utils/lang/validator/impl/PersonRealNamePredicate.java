
package org.liujk.java.framework.base.utils.lang.validator.impl;


import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import org.liujk.java.framework.base.utils.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 说明：
 * <p>
 * 个人名字格式验证。
 * <ul>
 * <li>不支持全数字</li>
 * <li>不支持非字符串（~！@$%^<>?&*＠＃＄％＾＆＊￥…×）字符</li>
 * <li>不支持相同字符(如"哈哈哈")</li>
 * <li>当姓名为纯中文时，校验前、中、后空格，反之则校验前、后空格</li>
 * <li>不能包含"公司"字样</li>
 * </ul>
 *
 */
public class PersonRealNamePredicate implements Predicate<String> {

    /**
     * \uFF10-\uFF19全角数字 \u516c\u53f8公司
     */
    public static final Pattern VALID_BUSINESS_SCOPE_REGEX = Pattern.compile (
            "(^([\\d\uFF10-\uFF19]+)$)|(^.*\u516c\u53f8.*$)|([!@#\\$\\%\\^\\*~`！＠＃＄％＾＊～｀￥…×])");

    public static final Pattern VALID_CHAR_CONTINUOUS_REGEX = Pattern
            .compile ("^(.)\\1{2,}$");
    /**
     * []中是全角空格 \u4E00-\u9FA5全汉字
     */
    public static final Pattern VALID_CHINESENAME_REGEX = Pattern
            .compile ("(^[\u4E00-\u9FA5\\s]+$)|(^.*[\\s　]+$)|(^[\\s　]+.*$)");

    public static final PersonRealNamePredicate INSTANCE = new PersonRealNamePredicate ();

    private PersonRealNamePredicate () {

    }

    @Override
    public boolean apply (String input) {
        if (Strings.isNullOrEmpty (input)) {
            return false;
        }

        boolean hasBlank = false;
        Matcher matcher = VALID_BUSINESS_SCOPE_REGEX.matcher (input);
        Matcher continuousMatcher = VALID_CHAR_CONTINUOUS_REGEX.matcher (input);

        Matcher chineseNameMatcher = VALID_CHINESENAME_REGEX.matcher (input);
        if (chineseNameMatcher.find ()) {
            hasBlank = StringUtils.hasBlankInString (input);
        }

        return !(matcher.find () || continuousMatcher.find () || hasBlank);
    }
}
