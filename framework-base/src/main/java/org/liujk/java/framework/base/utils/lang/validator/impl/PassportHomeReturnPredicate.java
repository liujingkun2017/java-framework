
package org.liujk.java.framework.base.utils.lang.validator.impl;



import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 说明：
 * <p>
 * 回乡证格式验证。
 * <ul>
 * 前中后不能存在空格
 * </ul>
 * 现实中回乡证规则增加了种类，该校验只验证了第一个字符意义不大，所以改为前中后不能存在空格。(历史：
 * <li>回乡证支持首字母为M或H开头，后面字符均为数字</li>)
 *
 */
public class PassportHomeReturnPredicate implements Predicate<String> {

    public static final Pattern VALID_PASSPORT_HOME_RETURN_REGEX = Pattern
            .compile ("^[^\\s　]{1,}$");

    public static final PassportHomeReturnPredicate INSTANCE = new PassportHomeReturnPredicate ();

    private PassportHomeReturnPredicate () {

    }

    @Override
    public boolean apply (String input) {
        if (Strings.isNullOrEmpty (input)) {
            return false;
        }
        Matcher matcher = VALID_PASSPORT_HOME_RETURN_REGEX.matcher (input);
        return matcher.find ();

    }
}
