
package org.liujk.java.framework.base.utils.lang;




import org.liujk.java.framework.base.utils.lang.object.Pair;

import java.util.Arrays;

/**
 * 说明：
 * <p>
 * 字符掩码工具类
 *
 */
public class MaskUtils {
    public static final char SEPARATOR_CHAR_ASTERISK = '*';
    public static final String ALL_ASTERISK = "******";

    /**
     * 把字符串mask
     *
     * @param str 字符串
     *
     * @return mask后的字符串
     */
    public static String mask (String str) {
        if (str == null) {
            return null;
        }
        int len = str.length ();
        if (len == 0) {
            return str;
        }
        if (len == 1) {
            return String.valueOf (SEPARATOR_CHAR_ASTERISK);
        }
        int maskLen;
        int begin;

        if ((len == 15 || len == 18) && StringUtils.isCertNo (str)) {
            // 身份证掩码迁前1后1
            begin = 1;
            maskLen = len - begin - 1;
        } else if ((len == 11) && StringUtils.isMobile (str)) {
            // 手机号码 前3后4
            begin = 3;
            maskLen = len - begin - 4;
        } else if ((len >= 8 || len <= 11) && str.matches ("[A-Z]{1,2}\\d{7,10}")) {
            // 台胞证/回乡证/护照
            // 台胞证上面有两个号码：台胞证号码：0099730503(B) 身份证号码：H125039525
            if (Character.isDigit (str.charAt (1))) {
                begin = 2;
            } else {
                begin = 3;
            }
            maskLen = len - begin - 3;
        } else if ((len == 10 || len == 13) && str.matches ("[A-Z]?\\d{8,10}(\\([A-Z]\\))?")) {
            // 台胞证
            // 台胞证上面有两个号码：台胞证号码：0099730503(B) 身份证号码：H125039525
            begin = 2;
            if (str.charAt (str.length () - 1) == ')') {
                maskLen = len - begin - 4;
            } else {
                maskLen = len - begin - 1;
            }
        } else if ((len == 10) && StringUtils.isOrganizationCode (str)) {
            // 组织机构代码(格式为XXXXXXXX-X,X为字母或数字)
            begin = 1;
            maskLen = len - begin - 3;
        } else if ((len > 3) && StringUtils.isEmail (str)) {
            // 邮箱格式
            len = str.indexOf ('@');
            maskLen = Math.max ((len) / 2, 1);
            begin = (len - maskLen) / 2;
        } else if (len >= 16 && len <= 22) {
            // 卡号位于这个区间，特殊处理，保证前6后4
            maskLen = len - 6 - 4;
            begin = 6;
        } else {
            len = str.length ();
            maskLen = Math.max ((len) / 2, 1);
            begin = (len - maskLen) / 2;

        }
        return mask (str, begin, begin + maskLen);
    }

    /**
     * 掩码中间几位为
     *
     * @param str
     * @param len
     *
     * @return
     */
    public static String mask (String str, int len) {
        if (str == null || str.length () == 0) {
            return str;
        }
        if (len >= str.length ()) {
            char[] mask = repeatAsterisk (str.length ());
            return new String (mask);
        }
        int startIndex = (str.length () - len) / 2;
        return mask (str, startIndex, startIndex + len);
    }

    /**
     * 掩码指定的位数为*
     * <p/>
     * 注意:index从0开始
     *
     * @param str        原字符串
     * @param beginIndex 开始index,从0开始
     * @param endIndex   结束index,掩码不包括此位
     *
     * @return 返回掩码后的字符串
     */
    public static String mask (String str, int beginIndex, int endIndex) {
        if (str == null || str.length () == 0) {
            return str;
        }
        if (str.length () == 1) {
            return String.valueOf (SEPARATOR_CHAR_ASTERISK);
        }

        if (beginIndex < 0) {
            beginIndex = 0;
        }
        if (endIndex > str.length ()) {
            endIndex = str.length ();
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException (subLen);
        }

        // 复制整个str
        char[] chars = str.toCharArray ();
        char[] mask = repeatAsterisk (subLen);
        // 复制mask
        System.arraycopy (mask, 0, chars, beginIndex, subLen);
        // 复制输出
        return new String (chars);
    }

    public static String maskAll (String str) {
        if (str == null || str.length () == 0) {
            return str;
        }
        return ALL_ASTERISK;
    }

    protected static char[] repeatAsterisk (int len) {
        char[] chars = new char[len];
        Arrays.fill (chars, MaskUtils.SEPARATOR_CHAR_ASTERISK);
        return chars;
    }

    /**
     * 第一个保存开始index，第二个保存长度
     *
     * @param len
     *
     * @return
     */
    private static Pair<Integer, Integer> getMaskLength (int len) {
        int maskLen = Math.max ((len) / 2, 1);
        int begin = (len - maskLen) / 2;
        return Pair.build (begin, maskLen);
    }
}
