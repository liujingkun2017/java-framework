
package org.liujk.java.framework.base.utils.lang.validator.impl;


import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 说明：
 * <p>
 * 组织机构代码格式验证。 组织机构代码格式：大于等于9位数字或字母或（-）组合，并支持大小写，且最后一位只可为数字或大写字母，只支持半角
 *
 */
public class OrganizationCodePredicate implements Predicate<String> {

    public static final Pattern VALID_ORGANIZATION_CODE_REGEX = Pattern
            .compile ("^[A-Za-z\\d\\-]{8,}[A-Za-z\\d]$");

    public static final OrganizationCodePredicate INSTANCE = new OrganizationCodePredicate ();

    private OrganizationCodePredicate () {

    }

    @Override
    public boolean apply (String input) {
        if (Strings.isNullOrEmpty (input)) {
            return false;
        }
        Matcher matcher = VALID_ORGANIZATION_CODE_REGEX.matcher (input);
        return matcher.find ();
    }

}
