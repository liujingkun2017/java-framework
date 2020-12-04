
package org.liujk.java.framework.base.utils.lang.validator.impl;



import com.google.common.base.Predicate;

/**
 * 说明：
 * <p>
 * 判断企业统一社会信用代码格式是否正确，支持15位和18位
 *
 */
public class UnifiedSocialCreditIdPredicate implements Predicate<String> {
    public static final UnifiedSocialCreditIdPredicate INSTANCE = new UnifiedSocialCreditIdPredicate ();
    public static final String REGEX_UNIFIED_SOCIAL_CREDIT_IDENTIFIER
            = "^([0-9ABCDEFGHJKLMNPQRTUWXY]{2})([0-9]{6})([0-9ABCDEFGHJKLMNPQRTUWXY]{9})([0-9ABCDEFGHJKLMNPQRTUWXY])$";
    public static final String REGEX_BUSINESS_LICENSE_REGISTRATION_NUMBER = "^[0-9]{15}$";
    /**
     * 当校验码的数值为0~9时，就直接用该校验码的数值作为最终的统一社会信用代码的校验码；如果校验码的数值是10~30，则校验码转换为对应的大写英文字母。对应关系为：A=10、B=11、C=12、D=13、E=14、F=15、G=16、H=17、J=18、K=19、L=20、M=21、N=22、P=23、Q=24、R=25、T=26、U=27、W=28、X=29、Y=30
     */
    private static final String VERIFY_CODES = "0123456789ABCDEFGHJKLMNPQRTUWXY";

    private UnifiedSocialCreditIdPredicate () {
    }

    @Override
    public boolean apply (String unifiedSocialCreditId) {
        if (unifiedSocialCreditId == null
                || (unifiedSocialCreditId.length () != 15 && unifiedSocialCreditId.length () != 18)) {
            // 统一社会信用代码位数不对
            return false;
        }

        char[] unifiedSocialCreditIdArr = unifiedSocialCreditId.toCharArray ();

        // 老的营业执照编号!
        if (unifiedSocialCreditIdArr.length == 15) {
            if (!unifiedSocialCreditId.matches (REGEX_BUSINESS_LICENSE_REGISTRATION_NUMBER)) {
                return false;
            }
            int[] s = new int[15];
            int[] p = new int[16];
            int[] a = new int[15];
            int m = 10;
            p[0] = m;
            for (int i = 0; i < unifiedSocialCreditIdArr.length; i++) {
                a[i] = Integer.parseInt (String.valueOf (unifiedSocialCreditIdArr[i]), 10);
                s[i] = (p[i] % (m + 1)) + a[i];
                if (0 == s[i] % m) {
                    p[i + 1] = 10 * 2;
                } else {
                    p[i + 1] = (s[i] % m) * 2;
                }
            }
            if (1 == (s[14] % m)) {
                // 营业执照编号正确!
                return true;
            } else {
                // 营业执照编号错误!
                return false;
            }
        }
        // 新的18位统一社会信用代码
        else if (unifiedSocialCreditIdArr.length == 18) {
            if (!unifiedSocialCreditId.matches (REGEX_UNIFIED_SOCIAL_CREDIT_IDENTIFIER)) {
                return false;
            }
            // 计算第18位校验位
            int[] ws = new int[]{1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                sum += VERIFY_CODES.indexOf (unifiedSocialCreditIdArr[i]) * ws[i];
            }

            int c18i = 31 - (sum % 31);
            if (c18i == 31) {
                c18i = 0;
            }
            // 替换成对应的字符
            char c18 = VERIFY_CODES.charAt (c18i);

            if (c18 != unifiedSocialCreditIdArr[17]) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

}
