
package org.liujk.java.framework.base.utils.lang;


import org.apache.commons.lang3.ArrayUtils;
import org.liujk.java.framework.base.exceptions.PatternSyntaxException;
import org.liujk.java.framework.base.utils.lang.validator.impl.*;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 说明：
 * <p>
 * 字符串处理工具
 *
 */
public class StringUtils {
    /**
     * 空的 {@link String} 数组
     */
    public static final String[] EMPTY_STRINGS = {};
    /**
     * 空字符串。
     */
    public static final String EMPTY_STRING = "";
    private static final Pattern MAIL_PATTERN = Pattern.compile (".+@.+\\.[a-zA-Z]{2,}");
    private static final char SEPARATOR_CHAR_ASTERISK = '*';


    /**
     * 解析单词的解析器。
     */
    private static final WordTokenizer CAMEL_CASE_TOKENIZER = new WordTokenizer () {
        @Override
        protected void startSentence (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    Character
                            .toLowerCase (
                                    ch));
        }

        @Override
        protected void startWord (
                StringBuffer buffer,
                char ch) {
            if (!isDelimiter (
                    buffer.charAt (
                            buffer.length ()
                                    - 1))) {
                buffer.append (
                        Character
                                .toUpperCase (
                                        ch));
            } else {
                buffer.append (
                        Character
                                .toLowerCase (
                                        ch));
            }
        }

        @Override
        protected void inWord (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    Character
                            .toLowerCase (
                                    ch));
        }

        @Override
        protected void startDigitSentence (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }

        @Override
        protected void startDigitWord (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }

        @Override
        protected void inDigitWord (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }

        @Override
        protected void inDelimiter (
                StringBuffer buffer,
                char ch) {
            if (ch != UNDERSCORE) {
                buffer.append (
                        ch);
            }
        }
    };
    private static final WordTokenizer PASCAL_CASE_TOKENIZER = new WordTokenizer () {
        @Override
        protected void startSentence (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    Character
                            .toUpperCase (
                                    ch));
        }

        @Override
        protected void startWord (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    Character
                            .toUpperCase (
                                    ch));
        }

        @Override
        protected void inWord (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    Character
                            .toLowerCase (
                                    ch));
        }

        @Override
        protected void startDigitSentence (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }

        @Override
        protected void startDigitWord (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }

        @Override
        protected void inDigitWord (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }

        @Override
        protected void inDelimiter (
                StringBuffer buffer,
                char ch) {
            if (ch != UNDERSCORE) {
                buffer.append (
                        ch);
            }
        }
    };
    private static final WordTokenizer UPPER_CASE_WITH_UNDERSCORES_TOKENIZER = new WordTokenizer () {
        @Override
        protected void startSentence (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    Character
                            .toUpperCase (
                                    ch));
        }

        @Override
        protected void startWord (
                StringBuffer buffer,
                char ch) {
            if (!isDelimiter (
                    buffer.charAt (
                            buffer.length ()
                                    - 1))) {
                buffer.append (
                        UNDERSCORE);
            }

            buffer.append (
                    Character
                            .toUpperCase (
                                    ch));
        }

        @Override
        protected void inWord (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    Character
                            .toUpperCase (
                                    ch));
        }

        @Override
        protected void startDigitSentence (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }

        @Override
        protected void startDigitWord (
                StringBuffer buffer,
                char ch) {
            if (!isDelimiter (
                    buffer.charAt (
                            buffer.length ()
                                    - 1))) {
                buffer.append (
                        UNDERSCORE);
            }

            buffer.append (
                    ch);
        }

        @Override
        protected void inDigitWord (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }

        @Override
        protected void inDelimiter (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }
    };
    private static final WordTokenizer LOWER_CASE_WITH_UNDERSCORES_TOKENIZER = new WordTokenizer () {
        @Override
        protected void startSentence (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    Character
                            .toLowerCase (
                                    ch));
        }

        @Override
        protected void startWord (
                StringBuffer buffer,
                char ch) {
            if (!isDelimiter (
                    buffer.charAt (
                            buffer.length ()
                                    - 1))) {
                buffer.append (
                        UNDERSCORE);
            }

            buffer.append (
                    Character
                            .toLowerCase (
                                    ch));
        }

        @Override
        protected void inWord (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    Character
                            .toLowerCase (
                                    ch));
        }

        @Override
        protected void startDigitSentence (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }

        @Override
        protected void startDigitWord (
                StringBuffer buffer,
                char ch) {
            if (!isDelimiter (
                    buffer.charAt (
                            buffer.length ()
                                    - 1))) {
                buffer.append (
                        UNDERSCORE);
            }

            buffer.append (
                    ch);
        }

        @Override
        protected void inDigitWord (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }

        @Override
        protected void inDelimiter (
                StringBuffer buffer,
                char ch) {
            buffer.append (
                    ch);
        }
    };

    /**
     * 截取字符
     *
     * @param source 要截取字符串
     * @param len    字符长度
     *
     * @return
     * @see StringUtils#truncate(String, int, String)
     */
    public static String truncate (String source, int len) {
        return truncate (source, len, EMPTY_STRING);
    }

    /**
     * 截取字符。
     *
     * @param source 要省略的字符。
     * @param len    字符长度。
     * @param delim  代替省略字符的字符串。
     *
     * @return 省略后的字符。
     */
    public static String truncate (String source, int len, String delim) {
        if (isEmpty (source) || source.length () < len) {
            return source;
        }

        return source.substring (0, len) + delim;
    }

    /**
     * 二进制截取字符
     *
     * @param source 要截取字符串
     * @param len    字符长度
     *
     * @return
     * @see StringUtils#truncate(String, int, String)
     */
    public static String truncateByByte (String source, int len) {
        return truncateByByte (source, len, EMPTY_STRING);
    }

    /**
     * 二进制形式截取字符，使用字符集UTF-8，一个汉字占用3字节。
     *
     * @param source 要省略的字符。
     * @param len    字符长度。
     * @param delim  代替省略字符的字符串。
     *
     * @return 省略后的字符。
     */
    public static String truncateByByte (String source, int len, String delim) {
        if (isEmpty (source)) {
            return source;
        }

        String target = leftByByte (source, len);

        if (source.equals (target)) {
            return target;
        } else {
            return target + delim;
        }

    }

    /**
     * 将一个字符串指定位置的字符变为大写。
     *
     * @param str   字符串。
     * @param index 指定位置,该数必须大于等于1,小于等于字符串长度。
     *
     * @return 改变后的字符串。
     * @throws IndexOutOfBoundsException 如果 index 大于 <code>str.length()</code>。
     */
    public static String toUpperCase (String str, int index) {
        StringBuilder sb = new StringBuilder (str);
        char s = Character.toUpperCase (sb.charAt (index - 1));
        sb.setCharAt (index - 1, s);
        return sb.toString ();
    }

    /**
     * 将一个字符串指定位置的字符变为小写。
     *
     * @param str   字符串。
     * @param index 指定位置,该数必须大于等于1,小于等于字符串长度。
     *
     * @return 改变后的字符串。
     * @throws IndexOutOfBoundsException 如果 index 大于 <code>str.length()</code>。
     */
    public static String toLowerCase (String str, int index) {
        StringBuilder sb = new StringBuilder (str);
        char s = Character.toLowerCase (sb.charAt (index - 1));
        sb.setCharAt (index - 1, s);
        return sb.toString ();
    }

    /**
     * 将参数转换为 {@link String} 。 如果 param 为 null 则返回 null 。如果参数为 {@link String} 类型则直接转换为
     * {@link String} ，否则调用参数的 {@link Object#toString()} 方法转换为 {@link String} 。
     *
     * @param param 要转换的参数。
     *
     * @return 转换后的参数。
     */
    public static String toString (Object param) {
        return toString (param, null);
    }

    /**
     * 将参数转换为 {@link String} 。 如果 param 为 null 则返回 null 。如果参数为 {@link String} 类型则直接转换为
     * {@link String} ，否则调用参数的 {@link Object#toString()} 方法转换为 {@link String} 。
     *
     * @param param         要转换的参数。
     * @param defaultString 如果param为<code>null</code>的默认返回结果。
     *
     * @return 转换后的参数。
     */
    public static String toString (Object param, String defaultString) {
        if (param == null) {
            return defaultString;
        }
        return param.toString ();
    }

    /**
     * 检查 string 是否为空，如果为空则使用 defaultValue 作为返回值。
     *
     * @param string       要检查的 String。
     * @param defaultValue 如果 string 为空时的默认值。
     *
     * @return string 不为空返回 string ，否则返回 defaultValue。
     */
    public static String checkEmptyString (String string, String defaultValue) {
        if (isEmpty (string)) {
            return defaultValue;
        }
        return string;
    }

    /**
     * 判断 CharSequence 是否有内容。
     *
     * @param charSequence 要判断的CharSequence。
     *
     * @return 当 charSequence 不为 null 与 {@link CharSequence#length()} > 0 时返回 true。
     */
    public static boolean hasLength (CharSequence charSequence) {
        return (charSequence != null && charSequence.length () > 0);
    }

    /**
     * 判断 charSequence 是否有 text。
     *
     * @param charSequence 要判断的CharSequence。
     *
     * @return 当 charSequence 不为 null 与 {@link CharSequence#length()} > 0 并且该 charSequence 的至少一个字符在
     * {@link Character#isWhitespace(char)} 返回 false 时返回 true。
     */
    public static boolean hasText (CharSequence charSequence) {
        if (!hasLength (charSequence)) {
            return false;
        }
        int strLen = charSequence.length ();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace (charSequence.charAt (i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 得到字符串 s 在 字符串 string 中从 beginIndex 处开始到 endIndex - 1 处结束出现的次数。
     *
     * @param string     一个字符串。
     * @param s          需要检测在 string 中出现次数的字符串。
     * @param beginIndex 开始处的索引（包括）。
     * @param endIndex   - 结束处的索引（不包括） 。
     *
     * @return 字符串 s 在 字符串 string 中从 beginIndex 处开始到 endIndex - 1 处结束出现的次数，如果 string 为 null 或者 s 为
     * 空字符串 则返回 0。
     * @throws IllegalArgumentException 当 beginIndex 小于 0 或者 endIndex 大于 string 的长度时。
     */
    public static int occurrence (String string, String s, int beginIndex, int endIndex) {
        if (string == null || isEmpty (s)) {
            return 0;
        }
        return occurrence0 (string, s, beginIndex, endIndex);
    }

    /**
     * 得到字符串 s 在 字符串 string 中从 beginIndex 处开始到末尾出现的次数。
     *
     * @param string     一个字符串。
     * @param s          需要检测在 string 中出现次数的字符串。
     * @param beginIndex 开始处的索引（包括）。
     *
     * @return 字符串 s 在 字符串 string 中从 beginIndex 处开始到末尾出现的次数，如果 string 为 null 或者 s 为 空字符串 则返回 0。
     * @throws IllegalArgumentException 当 beginIndex 小于 0 时。
     */
    public static int occurrence (String string, String s, int beginIndex) {
        if (string == null || isEmpty (s)) {
            return 0;
        }
        return occurrence0 (string, s, beginIndex, string.length ());
    }

    /**
     * 得到字符串 s 在 字符串 string 中出现的次数。
     *
     * @param string 一个字符串。
     * @param s      需要检测在 string 中出现次数的字符串。
     *
     * @return 字符串 s 在 字符串 string 中出现的次数，如果 string 为 null 或者 s 为 空字符串 则返回 0，如果 string 为 空 或者 s 为
     * 空字符串 则返回 0。
     */
    public static int occurrence (String string, String s) {
        if (string == null || isEmpty (s)) {
            return 0;
        }
        return occurrence0 (string, s, 0, string.length ());
    }

    /**
     * 得到字符串 s 在 字符串 string 中从 beginIndex 处开始到 endIndex - 1 处结束出现的次数。
     *
     * @param string     一个字符串。
     * @param s          需要检测在 string 中出现次数的字符串。
     * @param beginIndex 开始处的索引（包括）。
     * @param endIndex   - 结束处的索引（不包括） 。
     *
     * @return 字符串 s 在 字符串 string 中从 beginIndex 处开始到 endIndex - 1 处结束出现的次数。
     * @throws IllegalArgumentException 当 beginIndex 小于 0 或者 大于等于endIndex 或者 endIndex 大于 string 的长度时。
     */
    private static int occurrence0 (String string, String s, int beginIndex, int endIndex) {
        if (endIndex == 0 && beginIndex == 0) {
            return 0;
        }
        Assert.isTrue (beginIndex >= 0 && beginIndex < endIndex, "Must beginIndex >= 0 and beginIndex < endIndex");
        Assert.isTrue (endIndex <= string.length (), "Must endIndex <= string.length");
        int i = 0;
        while (true) {
            beginIndex = string.indexOf (s, beginIndex);
            if (beginIndex == -1 || beginIndex >= endIndex) {
                break;
            }
            i++;
            beginIndex = beginIndex + s.length ();
        }
        return i;
    }

    /**
     * 得到字符串 s 在 字符串 string 中第一次开始连续出现的次数。
     *
     * @param string 一个字符串。
     * @param s      需要检测在 string 中第一次开始连续出现次数的字符串。
     *
     * @return 字符串 s 在 字符串 string 中第一次开始连续出现的次数，如果 string 为 空 或者 s 为 空 则返回 0。
     */
    public static int consecutive (String string, String s) {
        if (string == null || isEmpty (s)) {
            return 0;
        }
        return consecutive0 (string, s, 0, string.length ());
    }

    /**
     * 得到字符串 s 在 字符串 string 中从 beginIndex 处开始到末尾第一次开始连续出现的次数。
     *
     * @param string     一个字符串。
     * @param s          需要检测在 string 中第一次开始连续出现次数的字符串。
     * @param beginIndex 开始处的索引（包括）。
     *
     * @return 字符串 s 在 字符串 string 中从 beginIndex 处开始到末尾第一次开始连续出现的次数，如果 string 为 空 或者 s 为 空字符串 则返回 0。
     * @throws IllegalArgumentException 当 beginIndex 小于 0 时。
     */
    public static int consecutive (String string, String s, int beginIndex) {
        if (string == null || isEmpty (s)) {
            return 0;
        }
        return consecutive0 (string, s, beginIndex, string.length ());
    }

    /**
     * 得到字符串 s 在 字符串 string 中从 beginIndex 处开始到 endIndex - 1 处结束第一次开始连续出现的次数。
     *
     * @param string     一个字符串。
     * @param s          需要检测在 string 中第一次开始连续出现次数的字符串。
     * @param beginIndex 开始处的索引（包括）。
     * @param endIndex   - 结束处的索引（不包括） 。
     *
     * @return 字符串 s 在 字符串 string 中从 beginIndex 处开始到末尾第一次开始连续出现的次数，如果 string 为 空 或者 s 为 空字符串 则返回 0。
     * @throws IllegalArgumentException 当 beginIndex 小于 0 或者 大于等于endIndex 或者 endIndex 大于 string 的长度时。
     */
    public static int consecutive (String string, String s, int beginIndex, int endIndex) {
        if (string == null || isEmpty (s)) {
            return 0;
        }
        return consecutive0 (string, s, beginIndex, endIndex);
    }

    /**
     * 得到字符串 s 在 字符串 string 中从 beginIndex 处开始到 endIndex - 1 处结束第一次开始连续出现的次数。
     *
     * @param string     一个字符串。
     * @param s          需要检测在 string 中第一次开始连续出现次数的字符串。
     * @param beginIndex 开始处的索引（包括）。
     * @param endIndex   - 结束处的索引（不包括） 。
     *
     * @return 字符串 s 在 字符串 string 中从 beginIndex 处开始到 endIndex - 1 处结束第一次开始连续出现的次数。
     * @throws IllegalArgumentException 当 beginIndex 小于 0 或者 大于等于endIndex 或者 endIndex 大于 string 的长度时。
     */
    private static int consecutive0 (String string, String s, int beginIndex, int endIndex) {
        if (endIndex == 0 && beginIndex == 0) {
            return 0;
        }
        Assert.isTrue (beginIndex >= 0 && beginIndex < endIndex, "Must beginIndex >= 0 && beginIndex < endIndex");
        Assert.isTrue (endIndex <= string.length (), "Must endIndex <= string.length");
        int i = 0;
        while (true) {
            beginIndex = string.indexOf (s, beginIndex);
            if (beginIndex == -1 || beginIndex >= endIndex) {
                break;
            } else if ((beginIndex != endIndex - 1)
                    && (s.charAt (0) != string.charAt (beginIndex + 1))) {
                // 如果 s 的第一个字符与下次比较的第一个字符都不匹配，则为不连续
                i++;
                break;
            }
            i++;
            beginIndex = beginIndex + s.length ();
        }
        return i;
    }

    /**
     * 字符串 s 在 字符串 string 中从后往前第一次开始连续出现的次数。
     *
     * @param string 一个字符串。
     * @param s      需要检测在 string 中从后往前第一次开始连续出现次数的字符串。
     *
     * @return 字符串 s 在 字符串 string 中从后往前第一次开始连续出现的次数，如果 string 为 空 或者 s 为 空字符串 则返回 0。
     */
    public static int lastConsecutive (String string, String s) {
        if (string == null || isEmpty (s)) {
            return 0;
        }
        return lastConsecutive0 (string, s, string.length (), 0);
    }

    /**
     * 字符串 s 在 字符串 string 中从后往前 lastBeginIndex - 1 处开始到 0 处结束第一次开始连续出现的次数。
     *
     * @param string         一个字符串。
     * @param s              需要检测在 string 中从后往前第一次开始连续出现次数的字符串。
     * @param lastBeginIndex 从后往前开始处的索引（不包括）。
     *
     * @return 字符串 s 在 字符串 string 中从后往前 lastBeginIndex - 1 处开始到 0 处结束第一次开始连续出现的次数，如果 string 为 空 或者 s
     * 为 空字符串 则返回 0。
     * @throws IllegalArgumentException 当 lastBeginIndex 大于 string 的长度时。
     */
    public static int lastConsecutive (String string, String s, int lastBeginIndex) {
        if (string == null || isEmpty (s)) {
            return 0;
        }
        return lastConsecutive0 (string, s, lastBeginIndex, 0);
    }

    /**
     * 字符串 s 在 字符串 string 中从后往前 lastBeginIndex - 1 处开始到 lastEndIndex 处结束第一次开始连续出现的次数。
     *
     * @param string         一个字符串。
     * @param s              需要检测在 string 中从后往前第一次开始连续出现次数的字符串。
     * @param lastBeginIndex 从后往前开始处的索引（不包括）。
     * @param lastEndIndex   - 从后往前结束处的索引（包括） 。
     *
     * @return 字符串 s 在 字符串 string 中从后往前 lastBeginIndex - 1 处开始到 lastEndIndex 处结束第一次开始连续出现的次数，如果
     * string 为 空 或者 s 为 空字符串 则返回 0。
     * @throws IllegalArgumentException 当 lastBeginIndex 大于 string 的长度 或者 小于等于 lastEndIndex 或者 lastEndIndex 小于0 时。
     */
    public static int lastConsecutive (String string, String s, int lastBeginIndex,
                                       int lastEndIndex) {
        if (string == null || isEmpty (s)) {
            return 0;
        }
        return lastConsecutive0 (string, s, lastBeginIndex, lastEndIndex);
    }

    /**
     * 字符串 s 在 字符串 string 中从后往前 lastBeginIndex - 1 处开始到 lastEndIndex 处结束第一次开始连续出现的次数。
     *
     * @param string         一个字符串。
     * @param s              需要检测在 string 中从后往前第一次开始连续出现次数的字符串。
     * @param lastBeginIndex 从后往前开始处的索引（不包括）。
     * @param lastEndIndex   - 从后往前结束处的索引（包括） 。
     *
     * @return 字符串 s 在 字符串 string 中从后往前 lastBeginIndex - 1 处开始到 lastEndIndex 处结束第一次开始连续出现的次数。
     * @throws IllegalArgumentException 当 lastBeginIndex 大于 string 的长度 或者 小于等于 lastEndIndex 或者 lastEndIndex 小于0 时。
     */
    private static int lastConsecutive0 (String string, String s, int lastBeginIndex,
                                         int lastEndIndex) {
        if (lastBeginIndex == 0 && lastEndIndex == 0) {
            return 0;
        }
        Assert.isTrue (lastBeginIndex <= string.length () && lastBeginIndex > lastEndIndex,
                "参数[从后往前开始处的索引]必须 <= 字符串的长度 且 [从后往前开始处的索引] < [从后往前结束处的索引] ");
        Assert.isTrue (lastEndIndex >= 0, "参数[从后往前结束处的索引]必须 >= 0");
        int i = 0;
        StringBuilder builder = new StringBuilder (string);
        builder.delete (0, lastEndIndex);
        char sEndChar = s.charAt (s.length () - 1);
        while (true) {
            lastBeginIndex = builder.lastIndexOf (s, --lastBeginIndex);
            if (lastBeginIndex == -1) {
                break;
            } else if (lastBeginIndex == 0) {
                i++;
                break;
            } else if (sEndChar != builder.charAt (lastBeginIndex - 1)) {
                // 如果 s 的最后个字符与下次比较的反向第一个字符都不匹配，则为不连续
                i++;
                break;
            }
            i++;
            builder.delete (lastBeginIndex - s.length (), lastBeginIndex);
        }
        return i;
    }

    /**
     * 得到字符串 s 在 string 中出现第 count 次时的索引。
     * <p>
     * 如果 string 为 null 返回 -1 。如果 s 在 string 中出现次数不到 count 次，则返回 -1。
     *
     * @param string 任意字符串。
     * @param s      任意字符串。
     * @param count  指定 s 在 string 中出现的次数。
     *
     * @return 字符串 s 在 string 中出现第 count 次时的索引。
     * @throws IllegalArgumentException 如果 count 小于等于 0。
     */
    public static int indexOf (String string, String s, int count) {
        if (string == null) {
            return -1;
        }
        Assert.isTrue (count > 0, "参数[次数]必须大于0");
        int index = -s.length ();
        for (int i = 0; i < count; i++) {
            index = string.indexOf (s, index + s.length ());
            if (index == -1) {
                return -1;
            }
        }
        return index;
    }

    /**
     * 得到字符串 s 在 string 中从后往前出现第 count 次时的索引。
     * <p>
     * 如果 string 为 null 返回 -1 。如果 s 在 string 中出现次数不到 count 次，则返回 -1。
     *
     * @param string 任意字符串。
     * @param s      任意字符串。
     * @param count  指定 s 在 string 中出现的次数。
     *
     * @return 字符串 s 在 string 中出现第 count 次时的索引。
     * @throws IllegalArgumentException 如果 count 小于等于 0。
     */
    public static int lastIndexOf (String string, String s, int count) {
        if (string == null) {
            return -1;
        }
        Assert.isTrue (count > 0, "参数[次数]必须大于0");
        int index = string.length ();
        for (int i = 0; i < count; i++) {
            index = string.lastIndexOf (s, index - 1);
            if (index == -1) {
                return -1;
            }
        }
        return index;
    }

    /**
     * 返回一个新的字符串，它是此字符串的一个子字符串。该子字符串始于指定索引处的字符，一直到此字符串末尾。
     * <p>
     * 该方法与 {@link String#substring(int)} 不同的是，该方法生成的 {@link String} 内部的 char[] 与原字符串无关。
     *
     * @param string     要执行取子的字符串。
     * @param beginIndex beginIndex 开始处的索引（包括）。
     *
     * @return 指定的子字符串，如果 string 为 null 则返回 null 。
     * @throws IndexOutOfBoundsException 如果 beginIndex 为负或大于此 String 对象的长度。
     * @see String#substring(int)
     */
    public static String substring (String string, int beginIndex) {
        if (string == null) {
            return null;
        }
        return string.substring (beginIndex);
    }

    /**
     * 返回一个新字符串，它是此字符串的一个子字符串。该子字符串从指定的 beginIndex 处开始，一直到索引 endIndex - 1 处的字符。因此，该子字符串的长度为
     * endIndex-beginIndex。
     * <p>
     * 该方法与 {@link String#substring(int, int)} 不同的是，该方法生成的 {@link String} 内部的 char[] 与原字符串无关。
     *
     * @param string     要执行取子的字符串。
     * @param beginIndex 开始处的索引（包括）。
     * @param endIndex   结束处的索引（不包括）。
     *
     * @return 指定的子字符串，如果 string 为 null 则返回 null 。
     * @throws IndexOutOfBoundsException 如果 beginIndex 为负，或 endIndex 大于此 String 对象的长度，或 beginIndex 大于 endIndex。
     * @see String#substring(int, int)
     */
    public static String substring (String string, int beginIndex, int endIndex) {
        if (string == null) {
            return null;
        }
        return string.substring (beginIndex, endIndex);
    }

    /**
     * 按字节左截取字符串
     * <p>
     *
     * <pre>
     * StringUtils.leftByByte(null, 1) = null;
     * StringUtils.leftByByte("", 1) = "";
     * StringUtils.leftByByte("灰太狼", 1) = "";
     * StringUtils.leftByByte("灰太狼", 2) = "";
     * StringUtils.leftByByte("灰太狼", 3) = "灰";
     * StringUtils.leftByByte("灰太狼", 4) = "灰";
     * StringUtils.leftByByte("灰太狼", 5) = "灰";
     * StringUtils.leftByByte("灰太狼", 6) = "灰太";
     * StringUtils.leftByByte("a1灰太狼", 1) = "a";
     * StringUtils.leftByByte("a1灰太狼", 2) = "a1";
     * StringUtils.leftByByte("a1灰太狼", 3) = "a1";
     * StringUtils.leftByByte("a1灰太狼", 4) = "a1";
     * StringUtils.leftByByte("a1灰太狼", 5) = "a1灰";
     * StringUtils.leftByByte("a1灰太狼", 6) = "a1灰";
     * </pre>
     *
     * @param str 被截取的字符串
     * @param len 截取字符串的字节长度 默认按UTF-8字符集编码截取 默认按UTF-8字符集编码截取
     *
     * @return
     */
    public static String leftByByte (String str, int len) {
        final String DEFAULT_CHAR_SET = "UTF-8";
        return leftByByte (str, len, DEFAULT_CHAR_SET);
    }

    /**
     * 按字节左截取字符串
     *
     * <pre>
     * StringUtils.leftByByte(null, 1) = null;
     * StringUtils.leftByByte("", 1) = "";
     * StringUtils.leftByByte("灰太狼", 1, "UTF-8") = "";
     * StringUtils.leftByByte("灰太狼", 2, "UTF-8") = "";
     * StringUtils.leftByByte("灰太狼", 3, "UTF-8") = "灰";
     * StringUtils.leftByByte("灰太狼", 4, "UTF-8") = "灰";
     * StringUtils.leftByByte("灰太狼", 5, "UTF-8") = "灰";
     * StringUtils.leftByByte("灰太狼", 6, "UTF-8") = "灰太";
     * StringUtils.leftByByte("a1灰太狼", 1, "UTF-8") = "a";
     * StringUtils.leftByByte("a1灰太狼", 2, "UTF-8") = "a1";
     * StringUtils.leftByByte("a1灰太狼", 3, "UTF-8") = "a1";
     * StringUtils.leftByByte("a1灰太狼", 4, "UTF-8") = "a1";
     * StringUtils.leftByByte("a1灰太狼", 5, "UTF-8") = "a1灰";
     * StringUtils.leftByByte("a1灰太狼", 6, "UTF-8") = "a1灰";
     *
     * StringUtils.leftByByte("a1灰太狼", 1, "GBK") = "a";
     * StringUtils.leftByByte("a1灰太狼", 2, "GBK") = "a1";
     * StringUtils.leftByByte("a1灰太狼", 3, "GBK") = "a1";
     * StringUtils.leftByByte("a1灰太狼", 4, "GBK") = "a1灰";
     * StringUtils.leftByByte("a1灰太狼", 5, "GBK") = "a1灰";
     * StringUtils.leftByByte("a1灰太狼", 6, "GBK") = "a1灰太";
     * </pre>
     *
     * @param str     被截取的字符串
     * @param len     截取字符串的字节长度
     * @param charset UTF-8:3个字节为一个中文字符； GBK:2个字节为一个中文字符
     *
     * @return
     */
    public static String leftByByte (String str, int len, String charset) {
        // 原始字符不为null，也不是空字符串
        if (str != null && !"".equals (str)) {
            // 将原始字符串转换为GBK编码格式
            String strValue = str;
            try {
                strValue = new String (str.getBytes (charset), charset);
                String strSubStrTemp;
                int intPos;
                int intPosI;

                if (len < strValue.getBytes (charset).length) {
                    intPos = (len + 1) / 2;
                    intPosI = intPos;
                    strSubStrTemp = strValue.substring (0, intPos);
                    while (strSubStrTemp.getBytes (charset).length < len) {
                        intPosI = (intPosI + 1) / 2;
                        intPos += intPosI;
                        strSubStrTemp = strValue.substring (0, intPos);
                    }

                    strValue = strSubStrTemp.substring (0,
                            (intPos > strSubStrTemp.length () ? strSubStrTemp.length ()
                                    : intPos));
                    while (strValue.getBytes (charset).length > len) {
                        strValue = strValue.substring (0, strValue.length () - 1);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace ();
            }
            return strValue;
        }
        return str;
    }

    /**
     * 按字节右截取字符串
     * <p>
     *
     * <pre>
     * StringUtils.rightByByte(null, 3) = null;
     * StringUtils.rightByByte("", 1) = "";
     * StringUtils.rightByByte("灰太狼", 1) = "";
     * StringUtils.rightByByte("灰太狼", 2) = "";
     * StringUtils.rightByByte("灰太狼", 3) = "狼";
     * StringUtils.rightByByte("灰太狼", 4) = "狼";
     * StringUtils.rightByByte("灰太狼", 5) = "狼";
     * StringUtils.rightByByte("灰太狼", 6) = "太狼";
     * StringUtils.rightByByte("a1灰太狼1a", 1) = "a";
     * StringUtils.rightByByte("a1灰太狼1a", 2) = "1a";
     * StringUtils.rightByByte("a1灰太狼1a", 3) = "1a";
     * StringUtils.rightByByte("a1灰太狼1a", 4) = "1a";
     * StringUtils.rightByByte("a1灰太狼1a", 5) = "狼1a";
     * StringUtils.rightByByte("a1灰太狼1a", 6) = "狼1a";
     * </pre>
     *
     * @param str 被截取的字符串
     * @param len 截取字符串的字节长度 默认按UTF-8字符集编码截取 默认按UTF-8字符集编码截取
     *
     * @return
     */
    public static String rightByByte (String str, int len) {
        final String DEFAULT_CHAR_SET = "UTF-8";
        return rightByByte (str, len, DEFAULT_CHAR_SET);
    }

    /**
     * 按字节右截取字符串
     *
     * <pre>
     * StringUtils.rightByByte(null, 3) = null;
     * StringUtils.rightByByte("", 1) = "";
     * StringUtils.rightByByte("灰太狼", 1) = "";
     * StringUtils.rightByByte("灰太狼", 2) = "";
     * StringUtils.rightByByte("灰太狼", 3) = "狼";
     * StringUtils.rightByByte("灰太狼", 4) = "狼";
     * StringUtils.rightByByte("灰太狼", 5) = "狼";
     * StringUtils.rightByByte("灰太狼", 6) = "太狼";
     * StringUtils.rightByByte("a1灰太狼1a", 1) = "a";
     * StringUtils.rightByByte("a1灰太狼1a", 2) = "1a";
     * StringUtils.rightByByte("a1灰太狼1a", 3) = "1a";
     * StringUtils.rightByByte("a1灰太狼1a", 4) = "1a";
     * StringUtils.rightByByte("a1灰太狼1a", 5) = "狼1a";
     * StringUtils.rightByByte("a1灰太狼1a", 6) = "狼1a";
     * StringUtils.rightByByte("a1灰太狼1a", 1, "GBK") = "a";
     * StringUtils.rightByByte("a1灰太狼1a", 2, "GBK") = "1a";
     * StringUtils.rightByByte("a1灰太狼1a", 3, "GBK") = "1a";
     * StringUtils.rightByByte("a1灰太狼1a", 4, "GBK") = "狼1a";
     * StringUtils.rightByByte("a1灰太狼1a", 5, "GBK") = "狼1a";
     * StringUtils.rightByByte("a1灰太狼1a", 6, "GBK") = "太狼1a";
     * </pre>
     *
     * @param str
     * @param len
     * @param charset
     *
     * @return
     */
    public static String rightByByte (String str, int len, String charset) {
        return (reverse (leftByByte (reverse (str), len, charset)));
    }

    /**
     * 按字节截取字符串
     *
     * @param str        原始字符串
     * @param beginIndex 开始位置
     * @param endIndex   结束位置
     *
     * @return 截取后的字符串
     */
    public static String substringByByte (String str, int beginIndex, int endIndex) {
        final String DEFAULT_CHAR_SET = "GBK";
        // 原始字符不为null，也不是空字符串
        if (str != null && !"".equals (str)) {
            // 将原始字符串转换为GBK编码格式
            String strValue = str;
            try {
                strValue = new String (str.getBytes (DEFAULT_CHAR_SET), DEFAULT_CHAR_SET);
                String strSubStrTemp = "";
                int intPos;
                int intPosI;
                if (beginIndex > 0) {
                    intPos = (beginIndex + 1) / 2;
                    intPosI = intPos;
                    strSubStrTemp = strValue.substring (0, intPos);
                    while (strSubStrTemp.getBytes ().length < beginIndex) {
                        intPosI = (intPosI + 1) / 2;
                        intPos += intPosI;
                        intPos = Math.min (intPos, strValue.length ());
                        strSubStrTemp = strValue.substring (0, intPos);
                    }
                }
                if (!"".equals (strSubStrTemp)) {
                    strValue = strValue.substring (strSubStrTemp.length ());
                }
                endIndex = endIndex - beginIndex;
                if (endIndex < strValue.getBytes (DEFAULT_CHAR_SET).length) {
                    intPos = (endIndex + 1) / 2;
                    intPosI = intPos;
                    strSubStrTemp = strValue.substring (0, intPos);
                    while (strSubStrTemp.getBytes (DEFAULT_CHAR_SET).length < endIndex) {
                        intPosI = (intPosI + 1) / 2;
                        intPos += intPosI;
                        intPos = Math.min (intPos, strValue.length ());
                        strSubStrTemp = strValue.substring (0, intPos);
                    }

                    strValue = strSubStrTemp.substring (0,
                            (intPos > strSubStrTemp.length () ? strSubStrTemp.length ()
                                    : intPos));
                    while (strValue.getBytes (DEFAULT_CHAR_SET).length > endIndex) {
                        strValue = strValue.substring (0, strValue.length () - 1);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace ();
            }
            return strValue;
        }
        return str;
    }

    /*------------------------------------------------------------------------------------
     *                         以下是 StringUtil 的代码复制合并，新增方法请在此注释之上                                             -
     * ----------------------------------------------------------------------------------*/

    /*
     * ================ =常量和singleton= ================
     */

    /**
     * 根据给定的正则表达式的匹配来拆分 value 为一个 {@link Set}。
     * <p>
     * 该方法调用 <code>stringSplitToSet(value, regex, true)</code> 完成。
     * <p>
     * 该方法返回的 Set 为 {@link LinkedHashSet} 。
     *
     * @param value 需要被拆分的字符串。
     * @param regex 定界正则表达式 。
     *
     * @return 字符串 Set ，根据给定正则表达式的匹配来拆分 value ，从而生成此 Set 。
     * @throws PatternSyntaxException 如果 regex 表示的正则表达式无效。
     * @see LinkedHashSet
     * @see #stringSplitToSet(String, String, boolean)
     */
    public static Set<String> stringSplitToSet (String value, String regex) {
        return stringSplitToSet (value, regex, true);
    }

    /**
     * 根据给定的正则表达式的匹配来拆分 value 为一个 {@link Set}。
     * <p>
     * 该方法返回的 Set 为 {@link LinkedHashSet} 。
     *
     * @param value             需要被拆分的字符串。
     * @param regex             定界正则表达式 。
     * @param ignoreEmptyTokens 如果为 true 则忽略 长度为零 的字符串 。
     *
     * @return 字符串 Set ，根据给定正则表达式的匹配来拆分 value ，从而生成此 Set 。
     * @throws PatternSyntaxException 如果 regex 表示的正则表达式无效。
     * @see LinkedHashSet
     */
    public static Set<String> stringSplitToSet (String value, String regex,
                                                boolean ignoreEmptyTokens) {
        if (value == null) {
            return new LinkedHashSet<String> (6);
        }
        Set<String> set = new LinkedHashSet<String> ();
        stringSplitToCollection (value, regex, set, ignoreEmptyTokens);
        return set;
    }

    /**
     * 根据给定的正则表达式的匹配来拆分 value 为一个 {@link List}。
     * <p>
     * 该方法调用 <code>stringSplitToList(value, regex, true)</code> 完成。
     * <p>
     * 该方法返回的 List 为 {@link ArrayList} 。
     *
     * @param value 需要被拆分的字符串。
     * @param regex 定界正则表达式 。
     *
     * @return 字符串 List ，根据给定正则表达式的匹配来拆分 value ，从而生成此 List 。
     * @throws PatternSyntaxException 如果 regex 表示的正则表达式无效。
     * @see ArrayList
     * @see #stringSplitToList(String, String, boolean)
     */
    public static List<String> stringSplitToList (String value, String regex) {
        return stringSplitToList (value, regex, true);
    }

    /*
     * ==========================================================================
     */
    /* 判空函数。 */
    /*                                                                              */
    /* 以下方法用来判定一个字符串是否为： */
    /* 1. null */
    /* 2. empty - "" */
    /* 3. blank - "全部是空白" - 空白由Character.isWhitespace所定义。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 根据给定的正则表达式的匹配来拆分 value 为一个 {@link List}。
     * <p>
     * 该方法返回的 List 为 {@link ArrayList} 。
     *
     * @param value             需要被拆分的字符串。
     * @param regex             定界正则表达式 。
     * @param ignoreEmptyTokens 如果为 true 则忽略 长度为零 的字符串 。
     *
     * @return 字符串 List ，根据给定正则表达式的匹配来拆分 value ，从而生成此 List 。
     * @throws PatternSyntaxException 如果 regex 表示的正则表达式无效。
     * @see ArrayList
     */
    public static List<String> stringSplitToList (String value, String regex,
                                                  boolean ignoreEmptyTokens) {
        if (value == null) {
            return new ArrayList<String> (5);
        }
        List<String> list = new ArrayList<String> ();
        stringSplitToCollection (value, regex, list, ignoreEmptyTokens);
        return list;
    }

    private static void stringSplitToCollection (String value, String regex, Collection<String> set,
                                                 boolean ignoreEmptyTokens) {
        String[] split = value.split (regex);
        for (String aSplit : split) {
            if (!ignoreEmptyTokens || aSplit.length () > 0) {
                set.add (aSplit);
            }
        }
    }

    /**
     * 根据给定的正则表达式的匹配来拆分 value 为一个 String[]。
     * <p>
     * 该方法调用 <code>stringSplitToArray(value, regex, true)</code> 完成。
     *
     * @param value 需要被拆分的字符串。
     * @param regex 定界正则表达式 。
     *
     * @return 字符串数组 ，根据给定正则表达式的匹配来拆分 value ，从而生成此 数组 。
     * @throws PatternSyntaxException 如果 regex 表示的正则表达式无效。
     */
    public static String[] stringSplitToArray (String value, String regex) {
        return stringSplitToArray (value, regex, true);
    }

    /**
     * 根据给定的正则表达式的匹配来拆分 value 为一个 String[]。
     *
     * @param value             需要被拆分的字符串。
     * @param regex             定界正则表达式 。
     * @param ignoreEmptyTokens 如果为 true 则忽略 长度为零 的字符串 。
     *
     * @return 字符串数组 ，根据给定正则表达式的匹配来拆分 value ，从而生成此 数组 。
     * @throws PatternSyntaxException 如果 regex 表示的正则表达式无效。
     */
    public static String[] stringSplitToArray (String value, String regex,
                                               boolean ignoreEmptyTokens) {
        if (value == null) {
            return EMPTY_STRINGS;
        }
        List<String> list = stringSplitToList (value, regex, ignoreEmptyTokens);
        if (list.isEmpty ()) {
            return EMPTY_STRINGS;
        }
        return list.toArray (new String[list.size ()]);
    }

    /**
     * 检查字符串是否为<code>null</code>或空字符串<code>""</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果为空, 则返回<code>true</code>
     */
    public static boolean isEmpty (String str) {
        return ((str == null) || (str.length () == 0));
    }

    /**
     * 检查字符串是否是手机号码
     *
     * @param mobile
     *
     * @return
     */
    public static boolean isMobile (String mobile) {
        return MobileNoPredicate.INSTANCE.apply (mobile);
    }

    /**
     * 检查字符串是否是邮件地址
     *
     * @param address
     *
     * @return
     */
    public static boolean isEmail (String address) {
        if (StringUtils.isBlank (address)) {
            return false;
        }
        return EmailPredicate.INSTANCE.apply (address);
    }

    /**
     * 台胞证格式校验。
     * <ul>
     * <li>台胞证由8位及8位以上字符组成</li>
     * <li>切前中后不能有空格(20170113增加注释)</li>
     * </ul>
     *
     * @param taiwanPassport
     *
     * @return 符合条件, 返回<code>true</code>
     */
    public static boolean isTaiwanPassport (String taiwanPassport) {
        if (StringUtils.isBlank (taiwanPassport)) {
            return false;
        }
        return PassportTaiwanPredicate.INSTANCE.apply (taiwanPassport);
    }

    /**
     * 回乡证格式校验。
     * <ul>
     * 前中后不能存在空格。
     * </ul>
     * modify by wanglei 20170113: 现实中回乡证规则增加了种类，该校验只验证了第一个字符意义不大，所以改为前中后不能存在空格。
     *
     * @param homeReturnPassport
     *
     * @return 符合条件, 返回<code>true</code>
     */
    public static boolean isHomeReturnPassport (String homeReturnPassport) {
        if (StringUtils.isBlank (homeReturnPassport)) {
            return false;
        }
        return PassportHomeReturnPredicate.INSTANCE.apply (homeReturnPassport);
    }

    /**
     * 护照格式校验。
     * <ul>
     * <li>支持字母和数字，只支持半角</li>
     * </ul>
     *
     * @param passport
     *
     * @return 符合条件, 返回<code>true</code>
     */
    public static boolean isPassport (String passport) {
        if (StringUtils.isBlank (passport)) {
            return false;
        }
        return PassportPredicate.INSTANCE.apply (passport);
    }

    /**
     * 身份证格式校验。 支持15位和18位。
     *
     * @param certNo
     *
     * @return 符合条件, 返回<code>true</code>
     */
    public static boolean isCertNo (String certNo) {
        if (StringUtils.isBlank (certNo)) {
            return false;
        }
        return CertNoPredicate.INSTANCE.apply (certNo);
    }

    /**
     * 营业执照号格式验证。 营业执照格式:前后不能有空格，满足数字、字母、中文、长度不限
     *
     * @param businessNo
     *
     * @return 符合条件, 返回<code>true</code>
     */
    public static boolean isBusinessNo (String businessNo) {
        if (StringUtils.isBlank (businessNo)) {
            return false;
        }
        return BusinessNoPredicate.INSTANCE.apply (businessNo);
    }

    /**
     * 组织机构代码格式验证。 组织机构代码格式：大于等于9位数字或字母或（-）组合，并支持大小写，且最后一位只可为数字或大写字母，只支持半角
     *
     * @param organizationCode
     *
     * @return 符合条件, 返回<code>true</code>
     */
    public static boolean isOrganizationCode (String organizationCode) {
        if (StringUtils.isBlank (organizationCode)) {
            return false;
        }
        return OrganizationCodePredicate.INSTANCE.apply (organizationCode);
    }

    /**
     * 经营范围格式校验。
     * <ul>
     * <li>不支持全数字</li>
     * <li>不支持全字母</li>
     * <li>不支持全符号</li>
     * <li>不允许出现 全部一样的字符如：“哈哈哈”</li>
     * <li>不能包含"无"字样</li>
     * </ul>
     *
     * @param businessScope
     *
     * @return 符合条件, 返回<code>true</code>
     */
    public static boolean isBusinessScope (String businessScope) {
        if (StringUtils.isBlank (businessScope)) {
            return false;
        }
        return BusinessScopePredicate.INSTANCE.apply (businessScope);
    }

    /**
     * 地址格式校验。
     * <ul>
     * <li>不支持全数字</li>
     * <li>不支持非字符串（!$%^*<>?~`！＄％＾＊＜＞？～｀￥…《》×）字符</li>
     * <li>不支持相同字符(如"哈哈哈")</li>
     * </ul>
     *
     * @param address
     *
     * @return 符合条件, 返回<code>true</code>
     */
    public static boolean isAddress (String address) {
        if (StringUtils.isBlank (address)) {
            return false;
        }
        return AddressPredicate.INSTANCE.apply (address);
    }

    /**
     * 企业名字格式校验。
     * <ul>
     * <li>不支持全数字</li>
     * <li>不支持邮箱格式的名称</li>
     * <li>不支持名称里面包含"测试"、"反共产党"、"验证"</li>
     * <li>不支持非字符串（•~！@$%^<>?&*）字符</li>
     * <li>不支持相同字符(如"哈哈哈哈")</li>
     * </ul>
     *
     * @param enterpriseRealName
     *
     * @return 符合条件, 返回<code>true</code>
     */
    public static boolean isEnterpriseRealName (String enterpriseRealName) {
        if (StringUtils.isBlank (enterpriseRealName)) {
            return false;
        }
        return EnterpriseRealNamePredicate.INSTANCE.apply (enterpriseRealName);
    }

    /**
     * 个人名字格式验证。
     * <ul>
     * <li>不支持全数字</li>
     * <li>不支持非字符串（~！@$%^<>?&*＠＃＄％＾＆＊￥…×）字符</li>
     * <li>不支持相同字符(如"哈哈哈")</li>
     * <li>当姓名为纯中文时，校验前、中、后空格，反之则校验前、后空格</li>
     * <li>不能包含"公司"字样</li>
     * </ul>
     *
     * @param personRealName
     *
     * @return 符合条件, 返回<code>true</code>
     */
    public static boolean isPersonRealName (String personRealName) {
        if (StringUtils.isBlank (personRealName)) {
            return false;
        }
        return PersonRealNamePredicate.INSTANCE.apply (personRealName);
    }

    /**
     * 检查字符串是否不是<code>null</code>和空字符串<code>""</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果不为空, 则返回<code>true</code>
     */
    public static boolean isNotEmpty (String str) {
        return ((str != null) && (str.length () > 0));
    }

    /**
     * 检查字符串是否是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果为空白, 则返回<code>true</code>
     */
    public static boolean isBlank (String str) {
        int length;

        if ((str == null) || ((length = str.length ()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace (str.charAt (i))) {
                return false;
            }
        }

        return true;
    }

    /*
     * ========================================================================== ==
     */
    /* 默认值函数。 */
    /*                                                                              */
    /* 当字符串为null、empty或blank时，将字符串转换成指定的默认字符串。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 检查字符串是否不是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果为空白, 则返回<code>true</code>
     */
    public static boolean isNotBlank (String str) {
        int length;

        if ((str == null) || ((length = str.length ()) == 0)) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace (str.charAt (i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 参数中有一个参数为null或者空白，返回true
     *
     * @param str
     *
     * @return
     */
    public static boolean hasBlank (String... str) {
        if (str == null) {
            return true;
        }
        for (int i = 0; i < str.length; i++) {
            if (isBlank (str[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验字符串中是否存在空格字符。 入参为空或者格式匹配或者是空串，返回true
     *
     * @param str
     *
     * @return
     */
    public static boolean hasBlankInString (String str) {

        return BlankInStringPredicate.INSTANCE.apply (str);
    }

    /**
     * 校验字符串中是否存在前后空格字符。 入参为空或者格式匹配或者是空串，返回true
     *
     * @param str
     *
     * @return
     */
    public static boolean hasPreOrSuffBlankInString (String str) {

        return BlankInStringPreOrSuffPredicate.INSTANCE.apply (str);
    }

    /**
     * 如果字符串是<code>null</code>，则返回空字符串<code>""</code>，否则返回字符串本身。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.defaultIfNull(null)  = ""
     * StringUtils.defaultIfNull("")    = ""
     * StringUtils.defaultIfNull("  ")  = "  "
     * StringUtils.defaultIfNull("bat") = "bat"
     * </pre>
     *
     * @param str 要转换的字符串
     *
     * @return 字符串本身或空字符串<code>""</code>
     */
    public static String defaultIfNull (String str) {
        return (str == null) ? EMPTY_STRING : str;
    }

    /**
     * 如果字符串是<code>null</code>，则返回指定默认字符串，否则返回字符串本身。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.defaultIfNull(null, "default")  = "default"
     * StringUtils.defaultIfNull("", "default")    = ""
     * StringUtils.defaultIfNull("  ", "default")  = "  "
     * StringUtils.defaultIfNull("bat", "default") = "bat"
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     *
     * @return 字符串本身或指定的默认字符串
     */
    public static String defaultIfNull (String str, String defaultStr) {
        return (str == null) ? defaultStr : str;
    }

    /*
     * ========================================================================== ==
     */
    /* 去空白（或指定字符）的函数。 */
    /*                                                                              */
    /* 以下方法用来除去一个字串中的空白或指定字符。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 如果字符串是<code>null</code>或空字符串<code>""</code>，则返回空字符串<code>""</code> ，否则返回字符串本身。
     * <p/>
     * <p>
     * 此方法实际上和<code>defaultIfNull(String)</code>等效。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.defaultIfEmpty(null)  = ""
     * StringUtils.defaultIfEmpty("")    = ""
     * StringUtils.defaultIfEmpty("  ")  = "  "
     * StringUtils.defaultIfEmpty("bat") = "bat"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要转换的字符串
     *
     * @return 字符串本身或空字符串<code>""</code>
     */
    public static String defaultIfEmpty (String str) {
        return (str == null) ? EMPTY_STRING : str;
    }

    /**
     * 如果字符串是<code>null</code>或空字符串<code>""</code>，则返回指定默认字符串，否则返回字符串本身。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.defaultIfEmpty(null, "default")  = "default"
     * StringUtils.defaultIfEmpty("", "default")    = "default"
     * StringUtils.defaultIfEmpty("  ", "default")  = "  "
     * StringUtils.defaultIfEmpty("bat", "default") = "bat"
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     *
     * @return 字符串本身或指定的默认字符串
     */
    public static String defaultIfEmpty (String str, String defaultStr) {
        return ((str == null) || (str.length () == 0)) ? defaultStr : str;
    }

    /**
     * 如果字符串是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符，则返回空字符串 <code>""</code>，否则返回字符串本身。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.defaultIfBlank(null)  = ""
     * StringUtils.defaultIfBlank("")    = ""
     * StringUtils.defaultIfBlank("  ")  = ""
     * StringUtils.defaultIfBlank("bat") = "bat"
     * </pre>
     *
     * @param str 要转换的字符串
     *
     * @return 字符串本身或空字符串<code>""</code>
     */
    public static String defaultIfBlank (String str) {
        return isBlank (str) ? EMPTY_STRING : str;
    }

    /**
     * 如果字符串是<code>null</code>或空字符串<code>""</code>，则返回指定默认字符串，否则返回字符串本身。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.defaultIfBlank(null, "default")  = "default"
     * StringUtils.defaultIfBlank("", "default")    = "default"
     * StringUtils.defaultIfBlank("  ", "default")  = "default"
     * StringUtils.defaultIfBlank("bat", "default") = "bat"
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     *
     * @return 字符串本身或指定的默认字符串
     */
    public static String defaultIfBlank (String str, String defaultStr) {
        return isBlank (str) ? defaultStr : str;
    }


    /**
     * 范围给定字符串数组第一个不为空的字符串
     *
     * @param strs
     *
     * @return
     */
    public static String defaultIfBlank (String... strs) {
        if (strs == null || strs.length == 0) {
            return EMPTY_STRING;
        }
        for (String str : strs) {
            if (isNotBlank (str)) {
                return str;
            }
        }
        return EMPTY_STRING;
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>，依然返回<code>null</code>。
     * <p/>
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白，
     * 因而可以除去英文字符集之外的其它空白，如中文空格。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.trim(null)          = null
     * StringUtils.trim("")            = ""
     * StringUtils.trim("     ")       = ""
     * StringUtils.trim("abc")         = "abc"
     * StringUtils.trim("    abc    ") = "abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要处理的字符串
     *
     * @return 除去空白的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trim (String str) {
        return trim (str, null, 0);
    }

    /**
     * 除去字符串头尾部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.trim(null, *)          = null
     * StringUtils.trim("", *)            = ""
     * StringUtils.trim("abc", null)      = "abc"
     * StringUtils.trim("  abc", null)    = "abc"
     * StringUtils.trim("abc  ", null)    = "abc"
     * StringUtils.trim(" abc ", null)    = "abc"
     * StringUtils.trim("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     *
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trim (String str, String stripChars) {
        return trim (str, stripChars, 0);
    }

    /**
     * 除去字符串头部的空白，如果字符串是<code>null</code>，则返回<code>null</code>。
     * <p/>
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白，
     * 因而可以除去英文字符集之外的其它空白，如中文空格。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.trimStart(null)         = null
     * StringUtils.trimStart("")           = ""
     * StringUtils.trimStart("abc")        = "abc"
     * StringUtils.trimStart("  abc")      = "abc"
     * StringUtils.trimStart("abc  ")      = "abc  "
     * StringUtils.trimStart(" abc ")      = "abc "
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要处理的字符串
     *
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimStart (String str) {
        return trim (str, null, -1);
    }

    /**
     * 除去字符串头部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.trimStart(null, *)          = null
     * StringUtils.trimStart("", *)            = ""
     * StringUtils.trimStart("abc", "")        = "abc"
     * StringUtils.trimStart("abc", null)      = "abc"
     * StringUtils.trimStart("  abc", null)    = "abc"
     * StringUtils.trimStart("abc  ", null)    = "abc  "
     * StringUtils.trimStart(" abc ", null)    = "abc "
     * StringUtils.trimStart("yxabc  ", "xyz") = "abc  "
     * </pre>
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     *
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trimStart (String str, String stripChars) {
        return trim (str, stripChars, -1);
    }

    /**
     * 除去字符串尾部的空白，如果字符串是<code>null</code>，则返回<code>null</code>。
     * <p/>
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白，
     * 因而可以除去英文字符集之外的其它空白，如中文空格。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.trimEnd(null)       = null
     * StringUtils.trimEnd("")         = ""
     * StringUtils.trimEnd("abc")      = "abc"
     * StringUtils.trimEnd("  abc")    = "  abc"
     * StringUtils.trimEnd("abc  ")    = "abc"
     * StringUtils.trimEnd(" abc ")    = " abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要处理的字符串
     *
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimEnd (String str) {
        return trim (str, null, 1);
    }

    /**
     * 除去字符串尾部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.trimEnd(null, *)          = null
     * StringUtils.trimEnd("", *)            = ""
     * StringUtils.trimEnd("abc", "")        = "abc"
     * StringUtils.trimEnd("abc", null)      = "abc"
     * StringUtils.trimEnd("  abc", null)    = "  abc"
     * StringUtils.trimEnd("abc  ", null)    = "abc"
     * StringUtils.trimEnd(" abc ", null)    = " abc"
     * StringUtils.trimEnd("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     *
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trimEnd (String str, String stripChars) {
        return trim (str, stripChars, 1);
    }

    /**
     * 除去字符串头尾部的空白，如果结果字符串是空字符串<code>""</code>，则返回<code>null</code>。
     * <p/>
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白，
     * 因而可以除去英文字符集之外的其它空白，如中文空格。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.trimToNull(null)          = null
     * StringUtils.trimToNull("")            = null
     * StringUtils.trimToNull("     ")       = null
     * StringUtils.trimToNull("abc")         = "abc"
     * StringUtils.trimToNull("    abc    ") = "abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要处理的字符串
     *
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimToNull (String str) {
        return trimToNull (str, null);
    }

    /*
     * ========================================================================== ==
     */
    /* 比较函数。 */
    /*                                                                              */
    /* 以下方法用来比较两个字符串是否相同。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 除去字符串头尾部的空白，如果结果字符串是空字符串<code>""</code>，则返回<code>null</code>。
     * <p/>
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白，
     * 因而可以除去英文字符集之外的其它空白，如中文空格。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.trim(null, *)          = null
     * StringUtils.trim("", *)            = null
     * StringUtils.trim("abc", null)      = "abc"
     * StringUtils.trim("  abc", null)    = "abc"
     * StringUtils.trim("abc  ", null)    = "abc"
     * StringUtils.trim(" abc ", null)    = "abc"
     * StringUtils.trim("  abcyx", "xyz") = "  abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     *
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimToNull (String str, String stripChars) {
        String result = trim (str, stripChars);

        if ((result == null) || (result.length () == 0)) {
            return null;
        }

        return result;
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>，则返回空字符串<code>""</code>。
     * <p/>
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白，
     * 因而可以除去英文字符集之外的其它空白，如中文空格。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.trimToEmpty(null)          = ""
     * StringUtils.trimToEmpty("")            = ""
     * StringUtils.trimToEmpty("     ")       = ""
     * StringUtils.trimToEmpty("abc")         = "abc"
     * StringUtils.trimToEmpty("    abc    ") = "abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要处理的字符串
     *
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimToEmpty (String str) {
        return trimToEmpty (str, null);
    }

    /*
     * ========================================================================== ==
     */
    /* 字符串类型判定函数。 */
    /*                                                                              */
    /* 判定字符串的类型是否为：字母、数字、空白等 */
    /*
     * ========================================================================== ==
     */

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>，则返回空字符串<code>""</code>。
     * <p/>
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白，
     * 因而可以除去英文字符集之外的其它空白，如中文空格。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.trim(null, *)          = ""
     * StringUtils.trim("", *)            = ""
     * StringUtils.trim("abc", null)      = "abc"
     * StringUtils.trim("  abc", null)    = "abc"
     * StringUtils.trim("abc  ", null)    = "abc"
     * StringUtils.trim(" abc ", null)    = "abc"
     * StringUtils.trim("  abcyx", "xyz") = "  abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要处理的字符串
     *
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimToEmpty (String str, String stripChars) {
        String result = trim (str, stripChars);

        if (result == null) {
            return EMPTY_STRING;
        }

        return result;
    }

    /**
     * 除去字符串头尾部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.trim(null, *)          = null
     * StringUtils.trim("", *)            = ""
     * StringUtils.trim("abc", null)      = "abc"
     * StringUtils.trim("  abc", null)    = "abc"
     * StringUtils.trim("abc  ", null)    = "abc"
     * StringUtils.trim(" abc ", null)    = "abc"
     * StringUtils.trim("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * @param mode       <code>-1</code>表示trimStart，<code>0</code>表示trim全部， <code>1</code>表示trimEnd
     *
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    private static String trim (String str, String stripChars, int mode) {
        if (str == null) {
            return null;
        }

        int length = str.length ();
        int start = 0;
        int end = length;

        // 扫描字符串头部
        if (mode <= 0) {
            if (stripChars == null) {
                while ((start < end) && (Character.isWhitespace (str.charAt (start)))) {
                    start++;
                }
            } else if (stripChars.length () == 0) {
                return str;
            } else {
                while ((start < end) && (stripChars.indexOf (str.charAt (start)) != -1)) {
                    start++;
                }
            }
        }

        // 扫描字符串尾部
        if (mode >= 0) {
            if (stripChars == null) {
                while ((start < end) && (Character.isWhitespace (str.charAt (end - 1)))) {
                    end--;
                }
            } else if (stripChars.length () == 0) {
                return str;
            } else {
                while ((start < end) && (stripChars.indexOf (str.charAt (end - 1)) != -1)) {
                    end--;
                }
            }
        }

        if ((start > 0) || (end < length)) {
            return str.substring (start, end);
        }

        return str;
    }

    /**
     * 比较两个字符串（大小写敏感）。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     *
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equals (String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equals (str2);
    }

    /**
     * 比较两个字符串（大小写不敏感）。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.equalsIgnoreCase(null, null)   = true
     * StringUtils.equalsIgnoreCase(null, "abc")  = false
     * StringUtils.equalsIgnoreCase("abc", null)  = false
     * StringUtils.equalsIgnoreCase("abc", "abc") = true
     * StringUtils.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     *
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equalsIgnoreCase (String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equalsIgnoreCase (str2);
    }

    /**
     * 判断字符串是否只包含unicode字母。
     * <p/>
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.isAlpha(null)   = false
     * StringUtils.isAlpha("")     = true
     * StringUtils.isAlpha("  ")   = false
     * StringUtils.isAlpha("abc")  = true
     * StringUtils.isAlpha("ab2c") = false
     * StringUtils.isAlpha("ab-c") = false
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果字符串非<code>null</code>并且全由unicode字母组成，则返回<code>true</code>
     */
    public static boolean isAlpha (String str) {
        if (str == null) {
            return false;
        }

        int length = str.length ();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetter (str.charAt (i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否只包含unicode字母和空格<code>' '</code>。
     * <p/>
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.isAlphaSpace(null)   = false
     * StringUtils.isAlphaSpace("")     = true
     * StringUtils.isAlphaSpace("  ")   = true
     * StringUtils.isAlphaSpace("abc")  = true
     * StringUtils.isAlphaSpace("ab c") = true
     * StringUtils.isAlphaSpace("ab2c") = false
     * StringUtils.isAlphaSpace("ab-c") = false
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果字符串非<code>null</code>并且全由unicode字母和空格组成，则返回<code>true</code>
     */
    public static boolean isAlphaSpace (String str) {
        if (str == null) {
            return false;
        }

        int length = str.length ();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetter (str.charAt (i)) && (str.charAt (i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否只包含unicode字母和数字。
     * <p/>
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.isAlphanumeric(null)   = false
     * StringUtils.isAlphanumeric("")     = true
     * StringUtils.isAlphanumeric("  ")   = false
     * StringUtils.isAlphanumeric("abc")  = true
     * StringUtils.isAlphanumeric("ab c") = false
     * StringUtils.isAlphanumeric("ab2c") = true
     * StringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果字符串非<code>null</code>并且全由unicode字母数字组成，则返回<code>true</code>
     */
    public static boolean isAlphanumeric (String str) {
        if (str == null) {
            return false;
        }

        int length = str.length ();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetterOrDigit (str.charAt (i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否只包含unicode字母数字和空格<code>' '</code>。
     * <p/>
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.isAlphanumericSpace(null)   = false
     * StringUtils.isAlphanumericSpace("")     = true
     * StringUtils.isAlphanumericSpace("  ")   = true
     * StringUtils.isAlphanumericSpace("abc")  = true
     * StringUtils.isAlphanumericSpace("ab c") = true
     * StringUtils.isAlphanumericSpace("ab2c") = true
     * StringUtils.isAlphanumericSpace("ab-c") = false
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果字符串非<code>null</code>并且全由unicode字母数字和空格组成，则返回<code>true</code>
     */
    public static boolean isAlphanumericSpace (String str) {
        if (str == null) {
            return false;
        }

        int length = str.length ();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetterOrDigit (str.charAt (i)) && (str.charAt (i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /*
     * ========================================================================== ==
     */
    /* 大小写转换。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 判断字符串是否只包含unicode数字。
     * <p/>
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = true
     * StringUtils.isNumeric("  ")   = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果字符串非<code>null</code>并且全由unicode数字组成，则返回<code>true</code>
     */
    public static boolean isNumeric (String str) {
        if (str == null) {
            return false;
        }

        int length = str.length ();

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit (str.charAt (i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否为整数,包括小数点后面两个0的情况
     *
     * @param s
     *
     * @return
     */
    public static boolean isInteger (String s) {
        return NumberUtils.isInt (s);
    }

    /**
     * 判断字符串是否只包含unicode数字和空格<code>' '</code>。
     * <p/>
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.isNumericSpace(null)   = false
     * StringUtils.isNumericSpace("")     = true
     * StringUtils.isNumericSpace("  ")   = true
     * StringUtils.isNumericSpace("123")  = true
     * StringUtils.isNumericSpace("12 3") = true
     * StringUtils.isNumericSpace("ab2c") = false
     * StringUtils.isNumericSpace("12-3") = false
     * StringUtils.isNumericSpace("12.3") = false
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果字符串非<code>null</code>并且全由unicode数字和空格组成，则返回<code>true</code>
     */
    public static boolean isNumericSpace (String str) {
        if (str == null) {
            return false;
        }

        int length = str.length ();

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit (str.charAt (i)) && (str.charAt (i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    public static boolean isDateStr (String str) {
        if (isEmpty (str)) {
            return false;
        }
        return DateUtils.isDate (str);
    }

    /**
     * @param str
     * @param matcher
     *
     * @return
     * @name isMatches
     * @date 2011-6-20
     * @description 判断字符串是否匹配正则表达式
     */
    public static boolean isMatches (String str, String matcher) {
        return isMatches (str, matcher, false);
    }

    /**
     * @param str           需要判断的字符串
     * @param matcher       正则表达式
     * @param blnIgnoreCase 是否忽略大小写
     *
     * @return 判断字符串是否匹配正则表达式
     */
    public static boolean isMatches (String str, String matcher, boolean blnIgnoreCase) {
        if (isEmpty (str)) {
            return false;
        }
        String strValue = null == str ? "" : str;
        if (blnIgnoreCase) {
            strValue = str.toUpperCase ();
        }
        Pattern p = Pattern.compile (matcher);
        Matcher m = p.matcher (strValue);
        return m.matches ();
    }

    /**
     * 判断字符串是否只包含unicode空白。
     * <p/>
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.isWhitespace(null)   = false
     * StringUtils.isWhitespace("")     = true
     * StringUtils.isWhitespace("  ")   = true
     * StringUtils.isWhitespace("abc")  = false
     * StringUtils.isWhitespace("ab2c") = false
     * StringUtils.isWhitespace("ab-c") = false
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果字符串非<code>null</code>并且全由unicode空白组成，则返回<code>true</code>
     */
    public static boolean isWhitespace (String str) {
        if (str == null) {
            return false;
        }

        int length = str.length ();

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace (str.charAt (i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将字符串转换成大写。
     * <p/>
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.toUpperCase(null)  = null
     * StringUtils.toUpperCase("")    = ""
     * StringUtils.toUpperCase("aBc") = "ABC"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要转换的字符串
     *
     * @return 大写字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toUpperCase (String str) {
        if (str == null) {
            return null;
        }

        return str.toUpperCase ();
    }

    /**
     * 将字符串转换成小写。
     * <p/>
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.toLowerCase(null)  = null
     * StringUtils.toLowerCase("")    = ""
     * StringUtils.toLowerCase("aBc") = "abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要转换的字符串
     *
     * @return 大写字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toLowerCase (String str) {
        if (str == null) {
            return null;
        }

        return str.toLowerCase ();
    }

    /**
     * 将字符串的首字符转成大写（<code>Character.toTitleCase</code>），其它字符不变。
     * <p/>
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize("")    = ""
     * StringUtils.capitalize("cat") = "Cat"
     * StringUtils.capitalize("cAt") = "CAt"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要转换的字符串
     *
     * @return 首字符为大写的字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String capitalize (String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length ()) == 0)) {
            return str;
        }

        return new StringBuffer (strLen).append (Character.toTitleCase (str.charAt (0)))
                .append (str.substring (1)).toString ();
    }

    /**
     * 将字符串的首字符转成小写，其它字符不变。
     * <p/>
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.uncapitalize(null)  = null
     * StringUtils.uncapitalize("")    = ""
     * StringUtils.uncapitalize("Cat") = "cat"
     * StringUtils.uncapitalize("CAT") = "cAT"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要转换的字符串
     *
     * @return 首字符为小写的字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String uncapitalize (String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length ()) == 0)) {
            return str;
        }

        return new StringBuffer (strLen).append (Character.toLowerCase (str.charAt (0)))
                .append (str.substring (1)).toString ();
    }

    /**
     * 反转字符串的大小写。
     * <p/>
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.swapCase(null)                 = null
     * StringUtils.swapCase("")                   = ""
     * StringUtils.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要转换的字符串
     *
     * @return 大小写被反转的字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String swapCase (String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length ()) == 0)) {
            return str;
        }

        StringBuffer buffer = new StringBuffer (strLen);

        char ch = 0;

        for (int i = 0; i < strLen; i++) {
            ch = str.charAt (i);

            if (Character.isUpperCase (ch)) {
                ch = Character.toLowerCase (ch);
            } else if (Character.isTitleCase (ch)) {
                ch = Character.toLowerCase (ch);
            } else if (Character.isLowerCase (ch)) {
                ch = Character.toUpperCase (ch);
            }

            buffer.append (ch);
        }

        return buffer.toString ();
    }

    /**
     * 将字符串转换成camel case。
     * <p/>
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.toCamelCase(null)  = null
     * StringUtils.toCamelCase("")    = ""
     * StringUtils.toCamelCase("aBc") = "aBc"
     * StringUtils.toCamelCase("aBc def") = "aBcDef"
     * StringUtils.toCamelCase("aBc def_ghi") = "aBcDefGhi"
     * StringUtils.toCamelCase("aBc def_ghi 123") = "aBcDefGhi123"
     * </pre>
     * <p/>
     * </p>
     * <p/>
     * <p>
     * 此方法会保留除了下划线和空白以外的所有分隔符。
     * </p>
     *
     * @param str 要转换的字符串
     *
     * @return camel case字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toCamelCase (String str) {
        return CAMEL_CASE_TOKENIZER.parse (str);
    }

    /**
     * 将字符串转换成pascal case。
     * <p/>
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.toPascalCase(null)  = null
     * StringUtils.toPascalCase("")    = ""
     * StringUtils.toPascalCase("aBc") = "ABc"
     * StringUtils.toPascalCase("aBc def") = "ABcDef"
     * StringUtils.toPascalCase("aBc def_ghi") = "ABcDefGhi"
     * StringUtils.toPascalCase("aBc def_ghi 123") = "aBcDefGhi123"
     * </pre>
     * <p/>
     * </p>
     * <p/>
     * <p>
     * 此方法会保留除了下划线和空白以外的所有分隔符。
     * </p>
     *
     * @param str 要转换的字符串
     *
     * @return pascal case字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toPascalCase (String str) {
        return PASCAL_CASE_TOKENIZER.parse (str);
    }

    /**
     * 将字符串转换成下划线分隔的大写字符串。
     * <p/>
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.toUpperCaseWithUnderscores(null)  = null
     * StringUtils.toUpperCaseWithUnderscores("")    = ""
     * StringUtils.toUpperCaseWithUnderscores("aBc") = "A_BC"
     * StringUtils.toUpperCaseWithUnderscores("aBc def") = "A_BC_DEF"
     * StringUtils.toUpperCaseWithUnderscores("aBc def_ghi") = "A_BC_DEF_GHI"
     * StringUtils.toUpperCaseWithUnderscores("aBc def_ghi 123") = "A_BC_DEF_GHI_123"
     * StringUtils.toUpperCaseWithUnderscores("__a__Bc__") = "__A__BC__"
     * </pre>
     * <p/>
     * </p>
     * <p/>
     * <p>
     * 此方法会保留除了空白以外的所有分隔符。
     * </p>
     *
     * @param str 要转换的字符串
     *
     * @return 下划线分隔的大写字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toUpperCaseWithUnderscores (String str) {
        return UPPER_CASE_WITH_UNDERSCORES_TOKENIZER.parse (str);
    }

    /**
     * 将字符串转换成下划线分隔的小写字符串。
     * <p/>
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.toLowerCaseWithUnderscores(null)  = null
     * StringUtils.toLowerCaseWithUnderscores("")    = ""
     * StringUtils.toLowerCaseWithUnderscores("aBc") = "a_bc"
     * StringUtils.toLowerCaseWithUnderscores("aBc def") = "a_bc_def"
     * StringUtils.toLowerCaseWithUnderscores("aBc def_ghi") = "a_bc_def_ghi"
     * StringUtils.toLowerCaseWithUnderscores("aBc def_ghi 123") = "a_bc_def_ghi_123"
     * StringUtils.toLowerCaseWithUnderscores("__a__Bc__") = "__a__bc__"
     * </pre>
     * <p/>
     * </p>
     * <p/>
     * <p>
     * 此方法会保留除了空白以外的所有分隔符。
     * </p>
     *
     * @param str 要转换的字符串
     *
     * @return 下划线分隔的小写字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toLowerCaseWithUnderscores (String str) {
        return LOWER_CASE_WITH_UNDERSCORES_TOKENIZER.parse (str);
    }

    /**
     * 将字符串按空白字符分割。
     * <p/>
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.split(null)       = null
     * StringUtils.split("")         = []
     * StringUtils.split("abc def")  = ["abc", "def"]
     * StringUtils.split("abc  def") = ["abc", "def"]
     * StringUtils.split(" abc ")    = ["abc"]
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要分割的字符串
     *
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split (String str) {
        return split (str, null, -1);
    }

    /*
     * ========================================================================== ==
     */
    /* 字符串分割函数。 */
    /*                                                                              */
    /* 将字符串按指定分隔符分割。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 将字符串按指定字符分割。
     * <p/>
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("a.b.c", '.')    = ["a", "b", "c"]
     * StringUtils.split("a..b.c", '.')   = ["a", "b", "c"]
     * StringUtils.split("a:b:c", '.')    = ["a:b:c"]
     * StringUtils.split("a b c", ' ')    = ["a", "b", "c"]
     * </pre>
     * <p/>
     * </p>
     *
     * @param str           要分割的字符串
     * @param separatorChar 分隔符
     *
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String[] split (String str, char separatorChar) {
        if (str == null) {
            return null;
        }

        int length = str.length ();

        if (length == 0) {
            return StringUtils.EMPTY_STRINGS;
        }

        List list = new ArrayList ();
        int i = 0;
        int start = 0;
        boolean match = false;

        while (i < length) {
            if (str.charAt (i) == separatorChar) {
                if (match) {
                    list.add (str.substring (start, i));
                    match = false;
                }

                start = ++i;
                continue;
            }

            match = true;
            i++;
        }

        if (match) {
            list.add (str.substring (start, i));
        }

        return (String[]) list.toArray (new String[list.size ()]);
    }

    /**
     * 将字符串按指定字符分割。
     * <p/>
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.split(null, *)                = null
     * StringUtils.split("", *)                  = []
     * StringUtils.split("abc def", null)        = ["abc", "def"]
     * StringUtils.split("abc def", " ")         = ["abc", "def"]
     * StringUtils.split("abc  def", " ")        = ["abc", "def"]
     * StringUtils.split(" ab:  cd::ef  ", ":")  = ["ab", "cd", "ef"]
     * StringUtils.split("abc.def", "")          = ["abc.def"]
     * </pre>
     * <p/>
     * </p>
     *
     * @param str            要分割的字符串
     * @param separatorChars 分隔符
     *
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split (String str, String separatorChars) {
        return split (str, separatorChars, -1);
    }

    /**
     * 将字符串按指定字符分割。
     * <p/>
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.split(null, *, *)                 = null
     * StringUtils.split("", *, *)                   = []
     * StringUtils.split("ab cd ef", null, 0)        = ["ab", "cd", "ef"]
     * StringUtils.split("  ab   cd ef  ", null, 0)  = ["ab", "cd", "ef"]
     * StringUtils.split("ab:cd::ef", ":", 0)        = ["ab", "cd", "ef"]
     * StringUtils.split("ab:cd:ef", ":", 2)         = ["ab", "cdef"]
     * StringUtils.split("abc.def", "", 2)           = ["abc.def"]
     * </pre>
     * <p/>
     * </p>
     *
     * @param str            要分割的字符串
     * @param separatorChars 分隔符
     * @param max            返回的数组的最大个数，如果小于等于0，则表示无限制
     *
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String[] split (String str, String separatorChars, int max) {
        if (str == null) {
            return null;
        }

        int length = str.length ();

        if (length == 0) {
            return StringUtils.EMPTY_STRINGS;
        }

        List list = new ArrayList ();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;

        if (separatorChars == null) {
            // null表示使用空白作为分隔符
            while (i < length) {
                if (Character.isWhitespace (str.charAt (i))) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add (str.substring (start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        } else if (separatorChars.length () == 1) {
            // 优化分隔符长度为1的情形
            char sep = separatorChars.charAt (0);

            while (i < length) {
                if (str.charAt (i) == sep) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add (str.substring (start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        } else {
            // 一般情形
            while (i < length) {
                if (separatorChars.indexOf (str.charAt (i)) >= 0) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add (str.substring (start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        }

        if (match) {
            list.add (str.substring (start, i));
        }

        return (String[]) list.toArray (new String[list.size ()]);
    }

    /**
     * 将数组中的元素连接成一个字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.join(null)            = null
     * StringUtils.join([])              = ""
     * StringUtils.join([null])          = ""
     * StringUtils.join(["a", "b", "c"]) = "abc"
     * StringUtils.join([null, "", "a"]) = "a"
     * </pre>
     *
     * @param array 要连接的数组
     *
     * @return 连接后的字符串，如果原数组为<code>null</code>，则返回<code>null</code>
     */
    public static String join (Object[] array) {
        return join (array, null);
    }

    /*
     * ========================================================================== ==
     */
    /* 字符串连接函数。 */
    /*                                                                              */
    /* 将多个对象按指定分隔符连接成字符串。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 将数组中的元素连接成一个字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.join(null, *)               = null
     * StringUtils.join([], *)                 = ""
     * StringUtils.join([null], *)             = ""
     * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringUtils.join(["a", "b", "c"], null) = "abc"
     * StringUtils.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array     要连接的数组
     * @param separator 分隔符
     *
     * @return 连接后的字符串，如果原数组为<code>null</code>，则返回<code>null</code>
     */
    public static String join (Object[] array, char separator) {
        if (array == null) {
            return null;
        }

        int arraySize = array.length;
        int bufSize = (arraySize == 0) ? 0
                : ((((array[0] == null) ? 16 : array[0].toString ().length ()) + 1) * arraySize);
        StringBuffer buf = new StringBuffer (bufSize);

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append (separator);
            }

            if (array[i] != null) {
                buf.append (array[i]);
            }
        }

        return buf.toString ();
    }

    /**
     * 将数组中的元素连接成一个字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = ""
     * StringUtils.join([null], *)              = ""
     * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null)  = "abc"
     * StringUtils.join(["a", "b", "c"], "")    = "abc"
     * StringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array     要连接的数组
     * @param separator 分隔符
     *
     * @return 连接后的字符串，如果原数组为<code>null</code>，则返回<code>null</code>
     */
    public static String join (Object[] array, String separator) {
        if (array == null) {
            return null;
        }

        if (separator == null) {
            separator = EMPTY_STRING;
        }

        int arraySize = array.length;

        // ArraySize == 0: Len = 0
        // ArraySize > 0: Len = NofStrings *(len(firstString) + len(separator))
        // (估计大约所有的字符串都一样长)
        int bufSize = (arraySize == 0) ? 0
                : (arraySize * (((array[0] == null) ? 16 : array[0].toString ().length ())
                + separator.length ()));

        StringBuffer buf = new StringBuffer (bufSize);

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append (separator);
            }

            if (array[i] != null) {
                buf.append (array[i]);
            }
        }

        return buf.toString ();
    }

    /**
     * 将<code>Iterator</code>中的元素连接成一个字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = ""
     * StringUtils.join([null], *)              = ""
     * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null)  = "abc"
     * StringUtils.join(["a", "b", "c"], "")    = "abc"
     * StringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param iterator  要连接的<code>Iterator</code>
     * @param separator 分隔符
     *
     * @return 连接后的字符串，如果原数组为<code>null</code>，则返回<code>null</code>
     */
    @SuppressWarnings("rawtypes")
    public static String join (Iterator iterator, char separator) {
        if (iterator == null) {
            return null;
        }

        StringBuffer buf = new StringBuffer (256); // Java默认值是16, 可能偏小

        while (iterator.hasNext ()) {
            Object obj = iterator.next ();

            if (obj != null) {
                buf.append (obj);
            }

            if (iterator.hasNext ()) {
                buf.append (separator);
            }
        }

        return buf.toString ();
    }

    /**
     * 将<code>Iterator</code>中的元素连接成一个字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = ""
     * StringUtils.join([null], *)              = ""
     * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null)  = "abc"
     * StringUtils.join(["a", "b", "c"], "")    = "abc"
     * StringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param iterator  要连接的<code>Iterator</code>
     * @param separator 分隔符
     *
     * @return 连接后的字符串，如果原数组为<code>null</code>，则返回<code>null</code>
     */
    public static String join (Iterator<?> iterator, String separator) {
        if (iterator == null) {
            return null;
        }

        StringBuffer buf = new StringBuffer (256); // Java默认值是16, 可能偏小

        while (iterator.hasNext ()) {
            Object obj = iterator.next ();

            if (obj != null) {
                buf.append (obj);
            }

            if ((separator != null) && iterator.hasNext ()) {
                buf.append (separator);
            }
        }

        return buf.toString ();
    }

    /**
     * @return
     * @name findString
     * @date 2011-7-7
     * @description 根据正则表达是，查找满足正则表达式的字符串
     */
    public static String find (String str, String matcher) {
        Pattern p = Pattern.compile (matcher);
        Matcher m = p.matcher (str);
        if (m.find ()) {
            return m.group ();
        } else {
            return null;
        }
    }

    /*
     * ========================================================================== ==
     */
    /* 字符串查找函数 —— 字符或字符串。 */
    /*                                                                              */
    /* 在字符串中查找指定字符或字符串。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 查找满足条件的所有字符串
     *
     * @param str
     * @param matcher
     *
     * @return
     */
    public static List<String> findAll (String str, String matcher) {
        Pattern p = Pattern.compile (matcher);
        Matcher m = p.matcher (str);
        if (m.find ()) {
            List<String> findStrs = new ArrayList ();
            do {
                findStrs.add (m.group ());
            } while (m.find (m.end ()));
            return findStrs;
        }
        return null;
    }

    /**
     * 在字符串中查找指定字符，并返回第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.indexOf(null, *)         = -1
     * StringUtils.indexOf("", *)           = -1
     * StringUtils.indexOf("aabaabaa", 'a') = 0
     * StringUtils.indexOf("aabaabaa", 'b') = 2
     * </pre>
     *
     * @param str        要扫描的字符串
     * @param searchChar 要查找的字符
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int indexOf (String str, char searchChar) {
        if ((str == null) || (str.length () == 0)) {
            return -1;
        }

        return str.indexOf (searchChar);
    }

    /**
     * 在字符串中查找指定字符，并返回第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.indexOf(null, *, *)          = -1
     * StringUtils.indexOf("", *, *)            = -1
     * StringUtils.indexOf("aabaabaa", 'b', 0)  = 2
     * StringUtils.indexOf("aabaabaa", 'b', 3)  = 5
     * StringUtils.indexOf("aabaabaa", 'b', 9)  = -1
     * StringUtils.indexOf("aabaabaa", 'b', -1) = 2
     * </pre>
     *
     * @param str        要扫描的字符串
     * @param searchChar 要查找的字符
     * @param startPos   开始搜索的索引值，如果小于0，则看作0
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int indexOf (String str, char searchChar, int startPos) {
        if ((str == null) || (str.length () == 0)) {
            return -1;
        }

        return str.indexOf (searchChar, startPos);
    }

    /**
     * 在字符串中查找指定字符串，并返回第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.indexOf(null, *)          = -1
     * StringUtils.indexOf(*, null)          = -1
     * StringUtils.indexOf("", "")           = 0
     * StringUtils.indexOf("aabaabaa", "a")  = 0
     * StringUtils.indexOf("aabaabaa", "b")  = 2
     * StringUtils.indexOf("aabaabaa", "ab") = 1
     * StringUtils.indexOf("aabaabaa", "")   = 0
     * </pre>
     *
     * @param str       要扫描的字符串
     * @param searchStr 要查找的字符串
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int indexOf (String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        return str.indexOf (searchStr);
    }

    /**
     * 在字符串中查找指定字符集合中的字符，并返回第一个匹配的起始索引。 如果字符串为<code>null</code>，则返回 <code>-1</code>。
     * 如果字符集合为<code>null</code>或空，也返回<code>-1</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.indexOfAny(null, *)                = -1
     * StringUtils.indexOfAny("", *)                  = -1
     * StringUtils.indexOfAny(*, null)                = -1
     * StringUtils.indexOfAny(*, [])                  = -1
     * StringUtils.indexOfAny("zzabyycdxx",['z','a']) = 0
     * StringUtils.indexOfAny("zzabyycdxx",['b','y']) = 3
     * StringUtils.indexOfAny("aba", ['z'])           = -1
     * </pre>
     *
     * @param str         要扫描的字符串
     * @param searchChars 要搜索的字符集合
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int indexOfAny (String str, char[] searchChars) {
        if ((str == null) || (str.length () == 0) || (searchChars == null)
                || (searchChars.length == 0)) {
            return -1;
        }

        for (int i = 0; i < str.length (); i++) {
            char ch = str.charAt (i);

            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 在字符串中查找指定字符集合中的字符，并返回第一个匹配的起始索引。 如果字符串为<code>null</code>，则返回 <code>-1</code>。
     * 如果字符集合为<code>null</code>或空，也返回<code>-1</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.indexOfAny(null, *)            = -1
     * StringUtils.indexOfAny("", *)              = -1
     * StringUtils.indexOfAny(*, null)            = -1
     * StringUtils.indexOfAny(*, "")              = -1
     * StringUtils.indexOfAny("zzabyycdxx", "za") = 0
     * StringUtils.indexOfAny("zzabyycdxx", "by") = 3
     * StringUtils.indexOfAny("aba","z")          = -1
     * </pre>
     *
     * @param str         要扫描的字符串
     * @param searchChars 要搜索的字符集合
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int indexOfAny (String str, String searchChars) {
        if ((str == null) || (str.length () == 0) || (searchChars == null)
                || (searchChars.length () == 0)) {
            return -1;
        }

        for (int i = 0; i < str.length (); i++) {
            char ch = str.charAt (i);

            for (int j = 0; j < searchChars.length (); j++) {
                if (searchChars.charAt (j) == ch) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 在字符串中查找指定字符串集合中的字符串，并返回第一个匹配的起始索引。 如果字符串为<code>null</code>，则返回 <code>-1</code>。
     * 如果字符串集合为<code>null</code>或空，也返回<code>-1</code>。
     * 如果字符串集合包括<code>""</code>，并且字符串不为<code>null</code>，则返回 <code>str.length()</code>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.indexOfAny(null, *)                     = -1
     * StringUtils.indexOfAny(*, null)                     = -1
     * StringUtils.indexOfAny(*, [])                       = -1
     * StringUtils.indexOfAny("zzabyycdxx", ["ab","cd"])   = 2
     * StringUtils.indexOfAny("zzabyycdxx", ["cd","ab"])   = 2
     * StringUtils.indexOfAny("zzabyycdxx", ["mn","op"])   = -1
     * StringUtils.indexOfAny("zzabyycdxx", ["zab","aby"]) = 1
     * StringUtils.indexOfAny("zzabyycdxx", [""])          = 0
     * StringUtils.indexOfAny("", [""])                    = 0
     * StringUtils.indexOfAny("", ["a"])                   = -1
     * </pre>
     *
     * @param str        要扫描的字符串
     * @param searchStrs 要搜索的字符串集合
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int indexOfAny (String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }

        int sz = searchStrs.length;

        // String's can't have a MAX_VALUEth index.
        int ret = Integer.MAX_VALUE;

        int tmp = 0;

        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];

            if (search == null) {
                continue;
            }

            tmp = str.indexOf (search);

            if (tmp == -1) {
                continue;
            }

            if (tmp < ret) {
                ret = tmp;
            }
        }

        return (ret == Integer.MAX_VALUE) ? (-1) : ret;
    }

    /**
     * 在字符串中查找不在指定字符集合中的字符，并返回第一个匹配的起始索引。 如果字符串为<code>null</code>，则返回 <code>-1</code>。
     * 如果字符集合为<code>null</code>或空，也返回<code>-1</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.indexOfAnyBut(null, *)             = -1
     * StringUtils.indexOfAnyBut("", *)               = -1
     * StringUtils.indexOfAnyBut(*, null)             = -1
     * StringUtils.indexOfAnyBut(*, [])               = -1
     * StringUtils.indexOfAnyBut("zzabyycdxx",'za')   = 3
     * StringUtils.indexOfAnyBut("zzabyycdxx", 'by')  = 0
     * StringUtils.indexOfAnyBut("aba", 'ab')         = -1
     * </pre>
     *
     * @param str         要扫描的字符串
     * @param searchChars 要搜索的字符集合
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int indexOfAnyBut (String str, char[] searchChars) {
        if ((str == null) || (str.length () == 0) || (searchChars == null)
                || (searchChars.length == 0)) {
            return -1;
        }

        outer:
        for (int i = 0; i < str.length (); i++) {
            char ch = str.charAt (i);

            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    continue outer;
                }
            }

            return i;
        }

        return -1;
    }

    /**
     * 在字符串中查找不在指定字符集合中的字符，并返回第一个匹配的起始索引。 如果字符串为<code>null</code>，则返回 <code>-1</code>。
     * 如果字符集合为<code>null</code>或空，也返回<code>-1</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.indexOfAnyBut(null, *)            = -1
     * StringUtils.indexOfAnyBut("", *)              = -1
     * StringUtils.indexOfAnyBut(*, null)            = -1
     * StringUtils.indexOfAnyBut(*, "")              = -1
     * StringUtils.indexOfAnyBut("zzabyycdxx", "za") = 3
     * StringUtils.indexOfAnyBut("zzabyycdxx", "by") = 0
     * StringUtils.indexOfAnyBut("aba","ab")         = -1
     * </pre>
     *
     * @param str         要扫描的字符串
     * @param searchChars 要搜索的字符集合
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int indexOfAnyBut (String str, String searchChars) {
        if ((str == null) || (str.length () == 0) || (searchChars == null)
                || (searchChars.length () == 0)) {
            return -1;
        }

        for (int i = 0; i < str.length (); i++) {
            if (searchChars.indexOf (str.charAt (i)) < 0) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 从字符串尾部开始查找指定字符，并返回第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回 <code>-1</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)         = -1
     * StringUtils.lastIndexOf("", *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a') = 7
     * StringUtils.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param str        要扫描的字符串
     * @param searchChar 要查找的字符
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int lastIndexOf (String str, char searchChar) {
        if ((str == null) || (str.length () == 0)) {
            return -1;
        }

        return str.lastIndexOf (searchChar);
    }

    /**
     * 从字符串尾部开始查找指定字符，并返回第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回 <code>-1</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *, *)          = -1
     * StringUtils.lastIndexOf("", *,  *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'b', 8)  = 5
     * StringUtils.lastIndexOf("aabaabaa", 'b', 4)  = 2
     * StringUtils.lastIndexOf("aabaabaa", 'b', 0)  = -1
     * StringUtils.lastIndexOf("aabaabaa", 'b', 9)  = 5
     * StringUtils.lastIndexOf("aabaabaa", 'b', -1) = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a', 0)  = 0
     * </pre>
     *
     * @param str        要扫描的字符串
     * @param searchChar 要查找的字符
     * @param startPos   从指定索引开始向前搜索
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int lastIndexOf (String str, char searchChar, int startPos) {
        if ((str == null) || (str.length () == 0)) {
            return -1;
        }

        return str.lastIndexOf (searchChar, startPos);
    }

    /**
     * 从字符串尾部开始查找指定字符串，并返回第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回 <code>-1</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)         = -1
     * StringUtils.lastIndexOf("", *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a') = 7
     * StringUtils.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param str       要扫描的字符串
     * @param searchStr 要查找的字符串
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int lastIndexOf (String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        return str.lastIndexOf (searchStr);
    }

    /**
     * 从字符串尾部开始查找指定字符串集合中的字符串，并返回第一个匹配的起始索引。 如果字符串为<code>null</code>，则返回 <code>-1</code>。
     * 如果字符串集合为<code>null</code>或空，也返回<code>-1</code>。
     * 如果字符串集合包括<code>""</code>，并且字符串不为<code>null</code>，则返回 <code>str.length()</code>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.lastIndexOfAny(null, *)                   = -1
     * StringUtils.lastIndexOfAny(*, null)                   = -1
     * StringUtils.lastIndexOfAny(*, [])                     = -1
     * StringUtils.lastIndexOfAny(*, [null])                 = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["ab","cd"]) = 6
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["cd","ab"]) = 6
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn",""])   = 10
     * </pre>
     *
     * @param str        要扫描的字符串
     * @param searchStrs 要搜索的字符串集合
     *
     * @return 第一个匹配的索引值。如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     */
    public static int lastIndexOfAny (String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }

        int searchStrsLength = searchStrs.length;
        int index = -1;
        int tmp = 0;

        for (int i = 0; i < searchStrsLength; i++) {
            String search = searchStrs[i];

            if (search == null) {
                continue;
            }

            tmp = str.lastIndexOf (search);

            if (tmp > index) {
                index = tmp;
            }
        }

        return index;
    }

    /**
     * 检查字符串中是否包含指定的字符。如果字符串为<code>null</code>，将返回<code>false</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.contains(null, *)    = false
     * StringUtils.contains("", *)      = false
     * StringUtils.contains("abc", 'a') = true
     * StringUtils.contains("abc", 'z') = false
     * </pre>
     *
     * @param str        要扫描的字符串
     * @param searchChar 要查找的字符
     *
     * @return 如果找到，则返回<code>true</code>
     */
    public static boolean contains (String str, char searchChar) {
        if ((str == null) || (str.length () == 0)) {
            return false;
        }

        return str.indexOf (searchChar) >= 0;
    }

    /**
     * 检查字符串中是否包含指定的字符串。如果字符串为<code>null</code>，将返回<code>false</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "")   = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
     * </pre>
     *
     * @param str       要扫描的字符串
     * @param searchStr 要查找的字符串
     *
     * @return 如果找到，则返回<code>true</code>
     */
    public static boolean contains (String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return false;
        }

        return str.indexOf (searchStr) >= 0;
    }

    /**
     * 检查字符串是是否只包含指定字符集合中的字符。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>false</code>。 如果字符集合为<code>null</code>
     * 则返回<code>false</code>。 但是空字符串永远返回<code>true</code>.
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.containsOnly(null, *)       = false
     * StringUtils.containsOnly(*, null)       = false
     * StringUtils.containsOnly("", *)         = true
     * StringUtils.containsOnly("ab", '')      = false
     * StringUtils.containsOnly("abab", 'abc') = true
     * StringUtils.containsOnly("ab1", 'abc')  = false
     * StringUtils.containsOnly("abz", 'abc')  = false
     * </pre>
     *
     * @param str   要扫描的字符串
     * @param valid 要查找的字符串
     *
     * @return 如果找到，则返回<code>true</code>
     */
    public static boolean containsOnly (String str, char[] valid) {
        if ((valid == null) || (str == null)) {
            return false;
        }

        if (str.length () == 0) {
            return true;
        }

        if (valid.length == 0) {
            return false;
        }

        return indexOfAnyBut (str, valid) == -1;
    }

    /**
     * 检查字符串是是否只包含指定字符集合中的字符。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>false</code>。 如果字符集合为<code>null</code>
     * 则返回<code>false</code>。 但是空字符串永远返回<code>true</code>.
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.containsOnly(null, *)       = false
     * StringUtils.containsOnly(*, null)       = false
     * StringUtils.containsOnly("", *)         = true
     * StringUtils.containsOnly("ab", "")      = false
     * StringUtils.containsOnly("abab", "abc") = true
     * StringUtils.containsOnly("ab1", "abc")  = false
     * StringUtils.containsOnly("abz", "abc")  = false
     * </pre>
     *
     * @param str   要扫描的字符串
     * @param valid 要查找的字符串
     *
     * @return 如果找到，则返回<code>true</code>
     */
    public static boolean containsOnly (String str, String valid) {
        if ((str == null) || (valid == null)) {
            return false;
        }

        return containsOnly (str, valid.toCharArray ());
    }

    /**
     * 检查字符串是是否不包含指定字符集合中的字符。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>false</code>。 如果字符集合为<code>null</code> 则返回<code>true</code>。
     * 但是空字符串永远返回<code>true</code>.
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.containsNone(null, *)       = true
     * StringUtils.containsNone(*, null)       = true
     * StringUtils.containsNone("", *)         = true
     * StringUtils.containsNone("ab", '')      = true
     * StringUtils.containsNone("abab", 'xyz') = true
     * StringUtils.containsNone("ab1", 'xyz')  = true
     * StringUtils.containsNone("abz", 'xyz')  = false
     * </pre>
     *
     * @param str     要扫描的字符串
     * @param invalid 要查找的字符串
     *
     * @return 如果找到，则返回<code>true</code>
     */
    public static boolean containsNone (String str, char[] invalid) {
        if ((str == null) || (invalid == null)) {
            return true;
        }

        int strSize = str.length ();
        int validSize = invalid.length;

        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt (i);

            for (int j = 0; j < validSize; j++) {
                if (invalid[j] == ch) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 检查字符串是是否不包含指定字符集合中的字符。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>false</code>。 如果字符集合为<code>null</code> 则返回<code>true</code>。
     * 但是空字符串永远返回<code>true</code>.
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.containsNone(null, *)       = true
     * StringUtils.containsNone(*, null)       = true
     * StringUtils.containsNone("", *)         = true
     * StringUtils.containsNone("ab", "")      = true
     * StringUtils.containsNone("abab", "xyz") = true
     * StringUtils.containsNone("ab1", "xyz")  = true
     * StringUtils.containsNone("abz", "xyz")  = false
     * </pre>
     *
     * @param str          要扫描的字符串
     * @param invalidChars 要查找的字符串
     *
     * @return 如果找到，则返回<code>true</code>
     */
    public static boolean containsNone (String str, String invalidChars) {
        if ((str == null) || (invalidChars == null)) {
            return true;
        }

        return containsNone (str, invalidChars.toCharArray ());
    }

    /**
     * 取得指定子串在字符串中出现的次数。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>或空，则返回<code>0</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.countMatches(null, *)       = 0
     * StringUtils.countMatches("", *)         = 0
     * StringUtils.countMatches("abba", null)  = 0
     * StringUtils.countMatches("abba", "")    = 0
     * StringUtils.countMatches("abba", "a")   = 2
     * StringUtils.countMatches("abba", "ab")  = 1
     * StringUtils.countMatches("abba", "xxx") = 0
     * </pre>
     * <p/>
     * </p>
     *
     * @param str    要扫描的字符串
     * @param subStr 子字符串
     *
     * @return 子串在字符串中出现的次数，如果字符串为<code>null</code>或空，则返回<code>0</code>
     */
    public static int countMatches (String str, String subStr) {
        if ((str == null) || (str.length () == 0) || (subStr == null) || (subStr.length () == 0)) {
            return 0;
        }

        int count = 0;
        int index = 0;

        while ((index = str.indexOf (subStr, index)) != -1) {
            count++;
            index += subStr.length ();
        }

        return count;
    }

    /**
     * 压缩字符串。 如果开始位置大于字符串长度，则返回空字符串。 负的索引代表从尾部开始计算。如果字符串为<code>null</code>，则返回 <code>null</code>。
     * <p>
     *
     * <pre>
     * StringUtils.compressString(null, *)   = null
     * StringUtils.compressString("", *)     = ""
     * StringUtils.compressString("abc", 0)  = "abc"
     * StringUtils.compressString("abc", 2)  = "c"
     * StringUtils.compressString("abc", 4)  = ""
     * StringUtils.compressString("abc", -2) = "bc"
     * StringUtils.compressString("abc", -4) = "abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str   字符串
     * @param start 起始索引，如果为负数，表示从尾部查找
     *
     * @return 压缩后的字符串，如果原始串为<code>null</code>，则返回<code>null</code>
     */
    public static String compressString (String str, int start) {
        if (str == null) {
            return null;
        }

        if (start < 0) {
            start = str.length () + start;
        }

        if (start < 0) {
            start = 0;
        }

        if (start > str.length ()) {
            return EMPTY_STRING;
        }

        return str.substring (start);
    }

    /*
     * ========================================================================== ==
     */
    /* 取子串函数。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 压缩字符串。 如果开始位置大于字符串长度，则返回空字符串，如果结束位置大于字符串长度，则取开始位置到原字符串的末尾。
     * 负的索引代表从尾部开始计算。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p>
     *
     * <pre>
     * StringUtils.compressString(null, *, *)    = null
     * StringUtils.compressString("", * ,  *)    = "";
     * StringUtils.compressString("abc", 0, 2)   = "ab"
     * StringUtils.compressString("abc", 2, 0)   = ""
     * StringUtils.compressString("abc", 4, 7)   = ""
     * StringUtils.compressString("abc", 2, 4)   = "c"
     * StringUtils.compressString("abc", 4, 6)   = ""
     * StringUtils.compressString("abc", 2, 2)   = ""
     * StringUtils.compressString("abc", -2, -1) = "b"
     * StringUtils.compressString("abc", -4, 2)  = "ab"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str   字符串
     * @param start 起始索引，如果为负数，表示从尾部计算
     * @param end   结束索引（不含），如果为负数，表示从尾部计算
     *
     * @return 压缩后的字符串，如果原始串为<code>null</code>，则返回<code>null</code>
     */
    public static String compressString (String str, int start, int end) {
        if (str == null) {
            return null;
        }

        if (end < 0) {
            end = str.length () + end;
        }

        if (start < 0) {
            start = str.length () + start;
        }

        if (end > str.length ()) {
            end = str.length ();
        }

        if (start > end) {
            return EMPTY_STRING;
        }

        if (start < 0) {
            start = 0;
        }

        if (end < 0) {
            end = 0;
        }

        return str.substring (start, end);
    }

    /**
     * 取得长度为指定字符数的最左边的子串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.left(null, *)    = null
     * StringUtils.left(*, -ve)     = ""
     * StringUtils.left("", *)      = ""
     * StringUtils.left("abc", 0)   = ""
     * StringUtils.left("abc", 2)   = "ab"
     * StringUtils.left("abc", 4)   = "abc"
     * </pre>
     *
     * @param str 字符串
     * @param len 最左子串的长度
     *
     * @return 子串，如果原始字串为<code>null</code>，则返回<code>null</code>
     */
    public static String left (String str, int len) {
        if (str == null) {
            return null;
        }

        if (len < 0) {
            return EMPTY_STRING;
        }

        if (str.length () <= len) {
            return str;
        } else {
            return str.substring (0, len);
        }
    }

    /**
     * 取得长度为指定字符数的最右边的子串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.right(null, *)    = null
     * StringUtils.right(*, -ve)     = ""
     * StringUtils.right("", *)      = ""
     * StringUtils.right("abc", 0)   = ""
     * StringUtils.right("abc", 2)   = "bc"
     * StringUtils.right("abc", 4)   = "abc"
     * </pre>
     *
     * @param str 字符串
     * @param len 最右子串的长度
     *
     * @return 子串，如果原始字串为<code>null</code>，则返回<code>null</code>
     */
    public static String right (String str, int len) {
        if (str == null) {
            return null;
        }

        if (len < 0) {
            return EMPTY_STRING;
        }

        if (str.length () <= len) {
            return str;
        } else {
            return str.substring (str.length () - len);
        }
    }

    /**
     * 判断字符串是否在给定字符串中 区分大小写
     *
     * @param str
     * @param checkLists
     *
     * @return
     */
    public static boolean inList (String str, String... checkLists) {
        if (ArrayUtils.isEmpty (checkLists)) {
            return false;
        } else {
            for (String tmp : checkLists) {
                if (equals (str, tmp)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断字符串是否在给定字符串中 忽略大小写
     *
     * @param str
     * @param checkLists
     *
     * @return
     */
    public static boolean inListIgnoreCase (String str, String... checkLists) {
        if (ArrayUtils.isEmpty (checkLists)) {
            return false;
        } else {
            for (String tmp : checkLists) {
                if (equalsIgnoreCase (str, tmp)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 取得从指定索引开始计算的、长度为指定字符数的子串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.mid(null, *, *)    = null
     * StringUtils.mid(*, *, -ve)     = ""
     * StringUtils.mid("", 0, *)      = ""
     * StringUtils.mid("abc", 0, 2)   = "ab"
     * StringUtils.mid("abc", 0, 4)   = "abc"
     * StringUtils.mid("abc", 2, 4)   = "c"
     * StringUtils.mid("abc", 4, 2)   = ""
     * StringUtils.mid("abc", -2, 2)  = "ab"
     * </pre>
     *
     * @param str 字符串
     * @param pos 起始索引，如果为负数，则看作<code>0</code>
     * @param len 子串的长度，如果为负数，则看作长度为<code>0</code>
     *
     * @return 子串，如果原始字串为<code>null</code>，则返回<code>null</code>
     */
    public static String mid (String str, int pos, int len) {
        if (str == null) {
            return null;
        }

        if ((len < 0) || (pos > str.length ())) {
            return EMPTY_STRING;
        }

        if (pos < 0) {
            pos = 0;
        }

        if (str.length () <= (pos + len)) {
            return str.substring (pos);
        } else {
            return str.substring (pos, pos + len);
        }
    }

    /**
     * 左侧填充字符串
     * <p>
     *
     * <pre>
     * StringUtils.lpad(null, 5, '0')    = 00000
     * StringUtils.lpad('A', 5, '0')    = 0000A
     * StringUtils.lpad('AAAAAA', 5, '0')    = AAAAAA
     * </pre>
     *
     * @param str     待填充字符串
     * @param len     填充后的位数
     * @param padding 填充字符
     */
    public static String lpad (String str, int len, char padding) {
        if (str == null || str.length () < len) {
            StringBuilder sb = new StringBuilder (len);
            int toPadLen = str == null ? len : len - str.length ();
            sb.append (fillCharr (padding, toPadLen));
            sb.append (str == null ? "" : str);
            return sb.toString ();
        } else {
            return str;
        }
    }

    /**
     * 右侧填充字符串
     * <p>
     *
     * <pre>
     * StringUtils.rpad(null, 5, '0')    = 00000
     * StringUtils.rpad('A', 5, '0')    = A0000
     * StringUtils.rpad('AAAAAA', 5, '0')    = AAAAAA
     * </pre>
     *
     * @param str     待填充字符串
     * @param len     填充后的位数
     * @param padding 填充字符
     */
    public static String rpad (String str, int len, char padding) {
        if (str == null || str.length () < len) {
            StringBuilder sb = new StringBuilder (len);
            sb.append (str == null ? "" : str);
            int toPadLen = str == null ? len : len - str.length ();
            sb.append (fillCharr (padding, toPadLen));
            return sb.toString ();
        } else {
            return str;
        }
    }

    /**
     * 获取重复字符的字符串
     * <p>
     *
     * <pre>
     * StringUtils.newRepeatCharString(  '0',5)    = '00000'
     * StringUtils.newRepeatCharString(  '0',0)    = ''
     * </pre>
     *
     * @param c
     * @param len
     *
     * @return
     */
    public static String newRepeatCharString (char c, int len) {
        return new String (fillCharr (c, len));
    }

    protected static char[] fillCharr (char c, int len) {
        char[] chars = new char[len];
        if (len > 0) {
            Arrays.fill (chars, c);
        }
        return chars;
    }

    /**
     * 掩码中间几位为*,mask位数自动计算
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.mask(null)    = null
     * StringUtils.mask("")      = ""
     * StringUtils.mask("1")     = "*"
     * StringUtils.mask("11")   = "*1"
     * StringUtils.mask("111")   = "1*1"
     * StringUtils.mask("1111")   = "1**1"
     * StringUtils.mask("11111")   = "1**11"
     * StringUtils.mask("111111")  = "1***11"
     * StringUtils.mask("11111111111")  = "111*****111"
     * </pre>
     *
     * @param str
     *
     * @return
     * @author bohr.qiu
     * @see MaskUtils#mask(String)
     */
    public static String mask (String str) {
        return MaskUtils.mask (str);
    }

    /**
     * *掩码中间几位为*
     *
     * @param str
     * @param len mask位数
     *
     * @return
     * @author bohr.qiu
     * @see StringUtils#mask(String, int)
     */
    public static String mask (String str, int len) {
        return MaskUtils.mask (str, len);
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
     * @see MaskUtils#mask(String, int, int)
     */
    public static String mask (String str, int beginIndex, int endIndex) {
        return MaskUtils.mask (str, beginIndex, endIndex);
    }

    /**
     * 取得第一个出现的分隔子串之前的子串。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>null</code>。 如果分隔子串为<code>null</code> 或未找到该子串，则返回原字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.substringBefore(null, *)      = null
     * StringUtils.substringBefore("", *)        = ""
     * StringUtils.substringBefore("abc", "a")   = ""
     * StringUtils.substringBefore("abcba", "b") = "a"
     * StringUtils.substringBefore("abc", "c")   = "ab"
     * StringUtils.substringBefore("abc", "d")   = "abc"
     * StringUtils.substringBefore("abc", "")    = ""
     * StringUtils.substringBefore("abc", null)  = "abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str       字符串
     * @param separator 要搜索的分隔子串
     *
     * @return 子串，如果原始串为<code>null</code>，则返回<code>null</code>
     */
    public static String substringBefore (String str, String separator) {
        if ((str == null) || (separator == null) || (str.length () == 0)) {
            return str;
        }

        if (separator.length () == 0) {
            return EMPTY_STRING;
        }

        int pos = str.indexOf (separator);

        if (pos == -1) {
            return str;
        }

        return str.substring (0, pos);
    }
    /*
     * ========================================================================== ==
     */
    /* 搜索并取子串函数。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 取得第一个出现的分隔子串之后的子串。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>null</code>。 如果分隔子串为<code>null</code> 或未找到该子串，则返回原字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.substringAfter(null, *)      = null
     * StringUtils.substringAfter("", *)        = ""
     * StringUtils.substringAfter(*, null)      = ""
     * StringUtils.substringAfter("abc", "a")   = "bc"
     * StringUtils.substringAfter("abcba", "b") = "cba"
     * StringUtils.substringAfter("abc", "c")   = ""
     * StringUtils.substringAfter("abc", "d")   = ""
     * StringUtils.substringAfter("abc", "")    = "abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str       字符串
     * @param separator 要搜索的分隔子串
     *
     * @return 子串，如果原始串为<code>null</code>，则返回<code>null</code>
     */
    public static String substringAfter (String str, String separator) {
        if ((str == null) || (str.length () == 0)) {
            return str;
        }

        if (separator == null) {
            return EMPTY_STRING;
        }

        int pos = str.indexOf (separator);

        if (pos == -1) {
            return EMPTY_STRING;
        }

        return str.substring (pos + separator.length ());
    }

    /**
     * 取得最后一个的分隔子串之前的子串。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>null</code>。 如果分隔子串为<code>null</code> 或未找到该子串，则返回原字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.substringBeforeLast(null, *)      = null
     * StringUtils.substringBeforeLast("", *)        = ""
     * StringUtils.substringBeforeLast("abcba", "b") = "abc"
     * StringUtils.substringBeforeLast("abc", "c")   = "ab"
     * StringUtils.substringBeforeLast("a", "a")     = ""
     * StringUtils.substringBeforeLast("a", "z")     = "a"
     * StringUtils.substringBeforeLast("a", null)    = "a"
     * StringUtils.substringBeforeLast("a", "")      = "a"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str       字符串
     * @param separator 要搜索的分隔子串
     *
     * @return 子串，如果原始串为<code>null</code>，则返回<code>null</code>
     */
    public static String substringBeforeLast (String str, String separator) {
        if ((str == null) || (separator == null) || (str.length () == 0)
                || (separator.length () == 0)) {
            return str;
        }

        int pos = str.lastIndexOf (separator);

        if (pos == -1) {
            return str;
        }

        return str.substring (0, pos);
    }

    /**
     * 取得最后一个的分隔子串之后的子串。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>null</code>。 如果分隔子串为<code>null</code> 或未找到该子串，则返回原字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
     * </pre>
     * <p/>
     * </p>
     *
     * @param str       字符串
     * @param separator 要搜索的分隔子串
     *
     * @return 子串，如果原始串为<code>null</code>，则返回<code>null</code>
     */
    public static String substringAfterLast (String str, String separator) {
        if ((str == null) || (str.length () == 0)) {
            return str;
        }

        if ((separator == null) || (separator.length () == 0)) {
            return EMPTY_STRING;
        }

        int pos = str.lastIndexOf (separator);

        if ((pos == -1) || (pos == (str.length () - separator.length ()))) {
            return EMPTY_STRING;
        }

        return str.substring (pos + separator.length ());
    }

    /**
     * 取得指定分隔符的前两次出现之间的子串。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>null</code>。 如果分隔子串为<code>null</code> ，则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.substringBetween(null, *)            = null
     * StringUtils.substringBetween("", "")             = ""
     * StringUtils.substringBetween("", "tag")          = null
     * StringUtils.substringBetween("tagabctag", null)  = null
     * StringUtils.substringBetween("tagabctag", "")    = ""
     * StringUtils.substringBetween("tagabctag", "tag") = "abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 字符串
     * @param tag 要搜索的分隔子串
     *
     * @return 子串，如果原始串为<code>null</code>或未找到分隔子串，则返回<code>null</code>
     */
    public static String substringBetween (String str, String tag) {
        return substringBetween (str, tag, tag, 0);
    }

    /**
     * 取得两个分隔符之间的子串。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>null</code>。 如果分隔子串为<code>null</code> ，则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.substringBetween(null, *, *)          = null
     * StringUtils.substringBetween("", "", "")          = ""
     * StringUtils.substringBetween("", "", "tag")       = null
     * StringUtils.substringBetween("", "tag", "tag")    = null
     * StringUtils.substringBetween("yabcz", null, null) = null
     * StringUtils.substringBetween("yabcz", "", "")     = ""
     * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
     * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str   字符串
     * @param open  要搜索的分隔子串1
     * @param close 要搜索的分隔子串2
     *
     * @return 子串，如果原始串为<code>null</code>或未找到分隔子串，则返回<code>null</code>
     */
    public static String substringBetween (String str, String open, String close) {
        return substringBetween (str, open, close, 0);
    }

    /**
     * 取得两个分隔符之间的子串。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>null</code>。 如果分隔子串为<code>null</code> ，则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.substringBetween(null, *, *)          = null
     * StringUtils.substringBetween("", "", "")          = ""
     * StringUtils.substringBetween("", "", "tag")       = null
     * StringUtils.substringBetween("", "tag", "tag")    = null
     * StringUtils.substringBetween("yabcz", null, null) = null
     * StringUtils.substringBetween("yabcz", "", "")     = ""
     * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
     * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str       字符串
     * @param open      要搜索的分隔子串1
     * @param close     要搜索的分隔子串2
     * @param fromIndex 从指定index处搜索
     *
     * @return 子串，如果原始串为<code>null</code>或未找到分隔子串，则返回<code>null</code>
     */
    public static String substringBetween (String str, String open, String close, int fromIndex) {
        if ((str == null) || (open == null) || (close == null)) {
            return null;
        }

        int start = str.indexOf (open, fromIndex);

        if (start != -1) {
            int end = str.indexOf (close, start + open.length ());

            if (end != -1) {
                return str.substring (start + open.length (), end);
            }
        }

        return null;
    }

    /**
     * 删除所有在<code>Character.isWhitespace(char)</code>中所定义的空白。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.deleteWhitespace(null)         = null
     * StringUtils.deleteWhitespace("")           = ""
     * StringUtils.deleteWhitespace("abc")        = "abc"
     * StringUtils.deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     *
     * @param str 要处理的字符串
     *
     * @return 去空白后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String deleteWhitespace (String str) {
        if (str == null) {
            return null;
        }

        int sz = str.length ();
        StringBuffer buffer = new StringBuffer (sz);

        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace (str.charAt (i))) {
                buffer.append (str.charAt (i));
            }
        }

        return buffer.toString ();
    }

    /*
     * ========================================================================== ==
     */
    /* 删除字符。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 替换指定的子串，只替换第一个出现的子串。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>，如果指定子串为<code>null</code> ，则返回原字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.replaceOnce(null, *, *)        = null
     * StringUtils.replaceOnce("", *, *)          = ""
     * StringUtils.replaceOnce("aba", null, null) = "aba"
     * StringUtils.replaceOnce("aba", null, null) = "aba"
     * StringUtils.replaceOnce("aba", "a", null)  = "aba"
     * StringUtils.replaceOnce("aba", "a", "")    = "ba"
     * StringUtils.replaceOnce("aba", "a", "z")   = "zba"
     * </pre>
     * <p/>
     * </p>
     *
     * @param text 要扫描的字符串
     * @param repl 要搜索的子串
     * @param with 替换字符串
     *
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replaceOnce (String text, String repl, String with) {
        return replace (text, repl, with, 1);
    }

    /*
     * ========================================================================== ==
     */
    /* 替换子串。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 替换指定的子串，替换所有出现的子串。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>，如果指定子串为<code>null</code> ，则返回原字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.replace(null, *, *)        = null
     * StringUtils.replace("", *, *)          = ""
     * StringUtils.replace("aba", null, null) = "aba"
     * StringUtils.replace("aba", null, null) = "aba"
     * StringUtils.replace("aba", "a", null)  = "aba"
     * StringUtils.replace("aba", "a", "")    = "b"
     * StringUtils.replace("aba", "a", "z")   = "zbz"
     * </pre>
     * <p/>
     * </p>
     *
     * @param text 要扫描的字符串
     * @param repl 要搜索的子串
     * @param with 替换字符串
     *
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replace (String text, String repl, String with) {
        return replace (text, repl, with, -1);
    }

    /**
     * 替换指定的子串，替换指定的次数。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>，如果指定子串为<code>null</code> ，则返回原字符串。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.replace(null, *, *, *)         = null
     * StringUtils.replace("", *, *, *)           = ""
     * StringUtils.replace("abaa", null, null, 1) = "abaa"
     * StringUtils.replace("abaa", null, null, 1) = "abaa"
     * StringUtils.replace("abaa", "a", null, 1)  = "abaa"
     * StringUtils.replace("abaa", "a", "", 1)    = "baa"
     * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
     * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     * <p/>
     * </p>
     *
     * @param text 要扫描的字符串
     * @param repl 要搜索的子串
     * @param with 替换字符串
     * @param max  maximum number of values to replace, or <code>-1</code> if no maximum
     *
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replace (String text, String repl, String with, int max) {
        if ((text == null) || (repl == null) || (with == null) || (repl.length () == 0)
                || (max == 0)) {
            return text;
        }

        StringBuffer buf = new StringBuffer (text.length ());
        int start = 0;
        int end = 0;

        while ((end = text.indexOf (repl, start)) != -1) {
            buf.append (text.substring (start, end)).append (with);
            start = end + repl.length ();

            if (--max == 0) {
                break;
            }
        }

        buf.append (text.substring (start));
        return buf.toString ();
    }

    /**
     * 将字符串中所有指定的字符，替换成另一个。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)        = null
     * StringUtils.replaceChars("", *, *)          = ""
     * StringUtils.replaceChars("abcba", 'b', 'y') = "aycya"
     * StringUtils.replaceChars("abcba", 'z', 'y') = "abcba"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str         要扫描的字符串
     * @param searchChar  要搜索的字符
     * @param replaceChar 替换字符
     *
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replaceChars (String str, char searchChar, char replaceChar) {
        if (str == null) {
            return null;
        }

        return str.replace (searchChar, replaceChar);
    }

    /**
     * 将字符串中所有指定的字符，替换成另一个。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>。如果搜索字符串为<code>null</code> 或空，则返回原字符串。
     * </p>
     * <p/>
     * <p>
     * 例如： <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code> 。
     * </p>
     * <p/>
     * <p>
     * 通常搜索字符串和替换字符串是等长的，如果搜索字符串比替换字符串长，则多余的字符将被删除。 如果搜索字符串比替换字符串短，则缺少的字符将被忽略。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)           = null
     * StringUtils.replaceChars("", *, *)             = ""
     * StringUtils.replaceChars("abc", null, *)       = "abc"
     * StringUtils.replaceChars("abc", "", *)         = "abc"
     * StringUtils.replaceChars("abc", "b", null)     = "ac"
     * StringUtils.replaceChars("abc", "b", "")       = "ac"
     * StringUtils.replaceChars("abcba", "bc", "yz")  = "ayzya"
     * StringUtils.replaceChars("abcba", "bc", "y")   = "ayya"
     * StringUtils.replaceChars("abcba", "bc", "yzx") = "ayzya"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str          要扫描的字符串
     * @param searchChars  要搜索的字符串
     * @param replaceChars 替换字符串
     *
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replaceChars (String str, String searchChars, String replaceChars) {
        if ((str == null) || (str.length () == 0) || (searchChars == null)
                || (searchChars.length () == 0)) {
            return str;
        }

        char[] chars = str.toCharArray ();
        int len = chars.length;
        boolean modified = false;

        for (int i = 0, isize = searchChars.length (); i < isize; i++) {
            char searchChar = searchChars.charAt (i);

            if ((replaceChars == null) || (i >= replaceChars.length ())) {
                // 删除
                int pos = 0;

                for (int j = 0; j < len; j++) {
                    if (chars[j] != searchChar) {
                        chars[pos++] = chars[j];
                    } else {
                        modified = true;
                    }
                }

                len = pos;
            } else {
                // 替换
                for (int j = 0; j < len; j++) {
                    if (chars[j] == searchChar) {
                        chars[j] = replaceChars.charAt (i);
                        modified = true;
                    }
                }
            }
        }

        if (!modified) {
            return str;
        }

        return new String (chars, 0, len);
    }

    /**
     * 删除字符串中的空格
     *
     * @param text 要扫描的字符串
     *
     * @return
     */
    public static String removeBlankChar (String text) {
        return removeChars (text, ' ');
    }

    /**
     * 删除字符串中指定字符
     *
     * @param text  要扫描的字符串
     * @param chars 要删除的字符
     *
     * @return
     */
    public static String removeChars (String text, char... chars) {
        if ((text == null) || (chars == null || chars.length == 0)) {
            return text;
        }

        StringBuffer buf = new StringBuffer (text.length ());
        char[] source = text.toCharArray ();
        if (chars.length == 1) {
            char r = chars[0];
            for (char c : source) {
                if (c != r) {
                    buf.append (c);
                }
            }
        } else {
            for (char c : source) {
                boolean found = false;
                for (char r : chars) {
                    if (c == r) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    buf.append (c);
                }
            }
        }
        return buf.toString ();
    }

    /**
     * 将指定的子串用另一指定子串覆盖。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>null</code>。 负的索引值将被看作<code>0</code> ，越界的索引值将被设置成字符串的长度相同的值。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.overlay(null, *, *, *)            = null
     * StringUtils.overlay("", "abc", 0, 0)          = "abc"
     * StringUtils.overlay("abcdef", null, 2, 4)     = "abef"
     * StringUtils.overlay("abcdef", "", 2, 4)       = "abef"
     * StringUtils.overlay("abcdef", "", 4, 2)       = "abef"
     * StringUtils.overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
     * StringUtils.overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
     * StringUtils.overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
     * StringUtils.overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
     * StringUtils.overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
     * StringUtils.overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
     * </pre>
     * <p/>
     * </p>
     *
     * @param str     要扫描的字符串
     * @param overlay 用来覆盖的字符串
     * @param start   起始索引
     * @param end     结束索引
     *
     * @return 被覆盖后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String overlay (String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }

        if (overlay == null) {
            overlay = EMPTY_STRING;
        }

        int len = str.length ();

        if (start < 0) {
            start = 0;
        }

        if (start > len) {
            start = len;
        }

        if (end < 0) {
            end = 0;
        }

        if (end > len) {
            end = len;
        }

        if (start > end) {
            int temp = start;

            start = end;
            end = temp;
        }

        return new StringBuffer ((len + start) - end + overlay.length () + 1)
                .append (str.substring (0, start)).append (overlay).append (str.substring (end))
                .toString ();
    }

    /**
     * 删除字符串末尾的换行符。如果字符串不以换行结尾，则什么也不做。
     * <p/>
     * <p>
     * 换行符有三种情形：&quot;<code>\n</code>&quot;、&quot;<code>\r</code>&quot;、&quot;
     * <code>\r\n</code>&quot;。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.chomp(null)          = null
     * StringUtils.chomp("")            = ""
     * StringUtils.chomp("abc \r")      = "abc "
     * StringUtils.chomp("abc\n")       = "abc"
     * StringUtils.chomp("abc\r\n")     = "abc"
     * StringUtils.chomp("abc\r\n\r\n") = "abc\r\n"
     * StringUtils.chomp("abc\n\r")     = "abc\n"
     * StringUtils.chomp("abc\n\rabc")  = "abc\n\rabc"
     * StringUtils.chomp("\r")          = ""
     * StringUtils.chomp("\n")          = ""
     * StringUtils.chomp("\r\n")        = ""
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要处理的字符串
     *
     * @return 不以换行结尾的字符串，如果原始字串为<code>null</code>，则返回<code>null</code>
     */
    public static String chomp (String str) {
        if ((str == null) || (str.length () == 0)) {
            return str;
        }

        if (str.length () == 1) {
            char ch = str.charAt (0);

            if ((ch == '\r') || (ch == '\n')) {
                return EMPTY_STRING;
            } else {
                return str;
            }
        }

        int lastIdx = str.length () - 1;
        char last = str.charAt (lastIdx);

        if (last == '\n') {
            if (str.charAt (lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else if (last == '\r') {
        } else {
            lastIdx++;
        }

        return str.substring (0, lastIdx);
    }

    /*
     * ========================================================================== ==
     */
    /* Perl风格的chomp和chop函数。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 删除字符串末尾的指定字符串。如果字符串不以该字符串结尾，则什么也不做。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.chomp(null, *)         = null
     * StringUtils.chomp("", *)           = ""
     * StringUtils.chomp("foobar", "bar") = "foo"
     * StringUtils.chomp("foobar", "baz") = "foobar"
     * StringUtils.chomp("foo", "foo")    = ""
     * StringUtils.chomp("foo ", "foo")   = "foo "
     * StringUtils.chomp(" foo", "foo")   = " "
     * StringUtils.chomp("foo", "foooo")  = "foo"
     * StringUtils.chomp("foo", "")       = "foo"
     * StringUtils.chomp("foo", null)     = "foo"
     * </pre>
     *
     * @param str       要处理的字符串
     * @param separator 要删除的字符串
     *
     * @return 不以指定字符串结尾的字符串，如果原始字串为<code>null</code>，则返回<code>null</code>
     */
    public static String chomp (String str, String separator) {
        if ((str == null) || (str.length () == 0) || (separator == null)) {
            return str;
        }

        if (str.endsWith (separator)) {
            return str.substring (0, str.length () - separator.length ());
        }

        return str;
    }

    /**
     * 删除最后一个字符。
     * <p/>
     * <p>
     * 如果字符串以<code>\r\n</code>结尾，则同时删除它们。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.chop(null)          = null
     * StringUtils.chop("")            = ""
     * StringUtils.chop("abc \r")      = "abc "
     * StringUtils.chop("abc\n")       = "abc"
     * StringUtils.chop("abc\r\n")     = "abc"
     * StringUtils.chop("abc")         = "ab"
     * StringUtils.chop("abc\nabc")    = "abc\nab"
     * StringUtils.chop("a")           = ""
     * StringUtils.chop("\r")          = ""
     * StringUtils.chop("\n")          = ""
     * StringUtils.chop("\r\n")        = ""
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要处理的字符串
     *
     * @return 删除最后一个字符的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String chop (String str) {
        if (str == null) {
            return null;
        }

        int strLen = str.length ();

        if (strLen < 2) {
            return EMPTY_STRING;
        }

        int lastIdx = strLen - 1;
        String ret = str.substring (0, lastIdx);
        char last = str.charAt (lastIdx);

        if (last == '\n') {
            if (ret.charAt (lastIdx - 1) == '\r') {
                return ret.substring (0, lastIdx - 1);
            }
        }

        return ret;
    }

    /**
     * 将指定字符串重复n遍。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.repeat(null, 2)   = null
     * StringUtils.repeat("", 0)     = ""
     * StringUtils.repeat("", 2)     = ""
     * StringUtils.repeat("a", 3)    = "aaa"
     * StringUtils.repeat("ab", 2)   = "abab"
     * StringUtils.repeat("abcd", 2) = "abcdabcd"
     * StringUtils.repeat("a", -2)   = ""
     * </pre>
     *
     * @param str    要重复的字符串
     * @param repeat 重复次数，如果小于<code>0</code>，则看作<code>0</code>
     *
     * @return 重复n次的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String repeat (String str, int repeat) {
        if (str == null) {
            return null;
        }

        if (repeat <= 0) {
            return EMPTY_STRING;
        }

        int inputLength = str.length ();

        if ((repeat == 1) || (inputLength == 0)) {
            return str;
        }

        int outputLength = inputLength * repeat;

        switch (inputLength) {
            case 1:

                char ch = str.charAt (0);
                char[] output1 = new char[outputLength];

                for (int i = repeat - 1; i >= 0; i--) {
                    output1[i] = ch;
                }

                return new String (output1);

            case 2:

                char ch0 = str.charAt (0);
                char ch1 = str.charAt (1);
                char[] output2 = new char[outputLength];

                for (int i = (repeat * 2) - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }

                return new String (output2);

            default:

                StringBuffer buf = new StringBuffer (outputLength);

                for (int i = 0; i < repeat; i++) {
                    buf.append (str);
                }

                return buf.toString ();
        }
    }

    /*
     * ========================================================================== ==
     */
    /* 重复/对齐字符串。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 扩展并左对齐字符串，用空格<code>' '</code>填充右边。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.alignLeft(null, *)   = null
     * StringUtils.alignLeft("", 3)     = "   "
     * StringUtils.alignLeft("bat", 3)  = "bat"
     * StringUtils.alignLeft("bat", 5)  = "bat  "
     * StringUtils.alignLeft("bat", 1)  = "bat"
     * StringUtils.alignLeft("bat", -1) = "bat"
     * </pre>
     *
     * @param str  要对齐的字符串
     * @param size 扩展字符串到指定宽度
     *
     * @return 扩展后的字符串，如果字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String alignLeft (String str, int size) {
        return alignLeft (str, size, ' ');
    }

    /**
     * 扩展并左对齐字符串，用指定字符填充右边。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.alignLeft(null, *, *)     = null
     * StringUtils.alignLeft("", 3, 'z')     = "zzz"
     * StringUtils.alignLeft("bat", 3, 'z')  = "bat"
     * StringUtils.alignLeft("bat", 5, 'z')  = "batzz"
     * StringUtils.alignLeft("bat", 1, 'z')  = "bat"
     * StringUtils.alignLeft("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str     要对齐的字符串
     * @param size    扩展字符串到指定宽度
     * @param padChar 填充字符
     *
     * @return 扩展后的字符串，如果字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String alignLeft (String str, int size, char padChar) {
        if (str == null) {
            return null;
        }

        int pads = size - str.length ();

        if (pads <= 0) {
            return str;
        }

        return alignLeft (str, size, String.valueOf (padChar));
    }

    /**
     * 扩展并左对齐字符串，用指定字符串填充右边。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.alignLeft(null, *, *)      = null
     * StringUtils.alignLeft("", 3, "z")      = "zzz"
     * StringUtils.alignLeft("bat", 3, "yz")  = "bat"
     * StringUtils.alignLeft("bat", 5, "yz")  = "batyz"
     * StringUtils.alignLeft("bat", 8, "yz")  = "batyzyzy"
     * StringUtils.alignLeft("bat", 1, "yz")  = "bat"
     * StringUtils.alignLeft("bat", -1, "yz") = "bat"
     * StringUtils.alignLeft("bat", 5, null)  = "bat  "
     * StringUtils.alignLeft("bat", 5, "")    = "bat  "
     * </pre>
     *
     * @param str    要对齐的字符串
     * @param size   扩展字符串到指定宽度
     * @param padStr 填充字符串
     *
     * @return 扩展后的字符串，如果字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String alignLeft (String str, int size, String padStr) {
        if (str == null) {
            return null;
        }

        if ((padStr == null) || (padStr.length () == 0)) {
            padStr = " ";
        }

        int padLen = padStr.length ();
        int strLen = str.length ();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        if (pads == padLen) {
            return str.concat (padStr);
        } else if (pads < padLen) {
            return str.concat (padStr.substring (0, pads));
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray ();

            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }

            return str.concat (new String (padding));
        }
    }

    /**
     * 扩展并右对齐字符串，用空格<code>' '</code>填充左边。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.alignRight(null, *)   = null
     * StringUtils.alignRight("", 3)     = "   "
     * StringUtils.alignRight("bat", 3)  = "bat"
     * StringUtils.alignRight("bat", 5)  = "  bat"
     * StringUtils.alignRight("bat", 1)  = "bat"
     * StringUtils.alignRight("bat", -1) = "bat"
     * </pre>
     *
     * @param str  要对齐的字符串
     * @param size 扩展字符串到指定宽度
     *
     * @return 扩展后的字符串，如果字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String alignRight (String str, int size) {
        return alignRight (str, size, ' ');
    }

    /**
     * 扩展并右对齐字符串，用指定字符填充左边。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.alignRight(null, *, *)     = null
     * StringUtils.alignRight("", 3, 'z')     = "zzz"
     * StringUtils.alignRight("bat", 3, 'z')  = "bat"
     * StringUtils.alignRight("bat", 5, 'z')  = "zzbat"
     * StringUtils.alignRight("bat", 1, 'z')  = "bat"
     * StringUtils.alignRight("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str     要对齐的字符串
     * @param size    扩展字符串到指定宽度
     * @param padChar 填充字符
     *
     * @return 扩展后的字符串，如果字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String alignRight (String str, int size, char padChar) {
        if (str == null) {
            return null;
        }

        int pads = size - str.length ();

        if (pads <= 0) {
            return str;
        }

        return alignRight (str, size, String.valueOf (padChar));
    }

    /**
     * 扩展并右对齐字符串，用指定字符串填充左边。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.alignRight(null, *, *)      = null
     * StringUtils.alignRight("", 3, "z")      = "zzz"
     * StringUtils.alignRight("bat", 3, "yz")  = "bat"
     * StringUtils.alignRight("bat", 5, "yz")  = "yzbat"
     * StringUtils.alignRight("bat", 8, "yz")  = "yzyzybat"
     * StringUtils.alignRight("bat", 1, "yz")  = "bat"
     * StringUtils.alignRight("bat", -1, "yz") = "bat"
     * StringUtils.alignRight("bat", 5, null)  = "  bat"
     * StringUtils.alignRight("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str    要对齐的字符串
     * @param size   扩展字符串到指定宽度
     * @param padStr 填充字符串
     *
     * @return 扩展后的字符串，如果字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String alignRight (String str, int size, String padStr) {
        if (str == null) {
            return null;
        }

        if ((padStr == null) || (padStr.length () == 0)) {
            padStr = " ";
        }

        int padLen = padStr.length ();
        int strLen = str.length ();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        if (pads == padLen) {
            return padStr.concat (str);
        } else if (pads < padLen) {
            return padStr.substring (0, pads).concat (str);
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray ();

            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }

            return new String (padding).concat (str);
        }
    }

    /**
     * 扩展并居中字符串，用空格<code>' '</code>填充两边。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.center(null, *)   = null
     * StringUtils.center("", 4)     = "    "
     * StringUtils.center("ab", -1)  = "ab"
     * StringUtils.center("ab", 4)   = " ab "
     * StringUtils.center("abcd", 2) = "abcd"
     * StringUtils.center("a", 4)    = " a  "
     * </pre>
     *
     * @param str  要对齐的字符串
     * @param size 扩展字符串到指定宽度
     *
     * @return 扩展后的字符串，如果字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String center (String str, int size) {
        return center (str, size, ' ');
    }

    /**
     * 扩展并居中字符串，用指定字符填充两边。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, ' ')     = "    "
     * StringUtils.center("ab", -1, ' ')  = "ab"
     * StringUtils.center("ab", 4, ' ')   = " ab "
     * StringUtils.center("abcd", 2, ' ') = "abcd"
     * StringUtils.center("a", 4, ' ')    = " a  "
     * StringUtils.center("a", 4, 'y')    = "yayy"
     * </pre>
     *
     * @param str     要对齐的字符串
     * @param size    扩展字符串到指定宽度
     * @param padChar 填充字符
     *
     * @return 扩展后的字符串，如果字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String center (String str, int size, char padChar) {
        if ((str == null) || (size <= 0)) {
            return str;
        }

        int strLen = str.length ();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        str = alignRight (str, strLen + (pads / 2), padChar);
        str = alignLeft (str, size, padChar);
        return str;
    }

    /**
     * 扩展并居中字符串，用指定字符串填充两边。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, " ")     = "    "
     * StringUtils.center("ab", -1, " ")  = "ab"
     * StringUtils.center("ab", 4, " ")   = " ab "
     * StringUtils.center("abcd", 2, " ") = "abcd"
     * StringUtils.center("a", 4, " ")    = " a  "
     * StringUtils.center("a", 4, "yz")   = "yayz"
     * StringUtils.center("abc", 7, null) = "  abc  "
     * StringUtils.center("abc", 7, "")   = "  abc  "
     * </pre>
     *
     * @param str    要对齐的字符串
     * @param size   扩展字符串到指定宽度
     * @param padStr 填充字符串
     *
     * @return 扩展后的字符串，如果字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String center (String str, int size, String padStr) {
        if ((str == null) || (size <= 0)) {
            return str;
        }

        if ((padStr == null) || (padStr.length () == 0)) {
            padStr = " ";
        }

        int strLen = str.length ();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        str = alignRight (str, strLen + (pads / 2), padStr);
        str = alignLeft (str, size, padStr);
        return str;
    }

    /**
     * 反转字符串中的字符顺序。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>null</code>。
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.reverse(null)  = null
     * StringUtils.reverse("")    = ""
     * StringUtils.reverse("bat") = "tab"
     * </pre>
     *
     * @param str 要反转的字符串
     *
     * @return 反转后的字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String reverse (String str) {
        if ((str == null) || (str.length () == 0)) {
            return str;
        }

        return new StringBuffer (str).reverse ().toString ();
    }

    /*
     * ========================================================================== ==
     */
    /* 反转字符串。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 反转指定分隔符分隔的各子串的顺序。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>null</code>。
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.reverseDelimited(null, *)      = null
     * StringUtils.reverseDelimited("", *)        = ""
     * StringUtils.reverseDelimited("a.b.c", 'x') = "a.b.c"
     * StringUtils.reverseDelimited("a.b.c", '.') = "c.b.a"
     * </pre>
     *
     * @param str           要反转的字符串
     * @param separatorChar 分隔符
     *
     * @return 反转后的字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String reverseDelimited (String str, char separatorChar) {
        if (str == null) {
            return null;
        }

        String[] strs = split (str, separatorChar);

        ArrayUtils.reverse (strs);

        return join (strs, separatorChar);
    }

    /**
     * 反转指定分隔符分隔的各子串的顺序。
     * <p/>
     * <p>
     * 如果字符串为<code>null</code>，则返回<code>null</code>。
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.reverseDelimited(null, *, *)          = null
     * StringUtils.reverseDelimited("", *, *)            = ""
     * StringUtils.reverseDelimited("a.b.c", null, null) = "a.b.c"
     * StringUtils.reverseDelimited("a.b.c", "", null)   = "a.b.c"
     * StringUtils.reverseDelimited("a.b.c", ".", ",")   = "c,b,a"
     * StringUtils.reverseDelimited("a.b.c", ".", null)  = "c b a"
     * </pre>
     *
     * @param str            要反转的字符串
     * @param separatorChars 分隔符，如果为<code>null</code>，则默认使用空白字符
     * @param separator      用来连接子串的分隔符，如果为<code>null</code>，默认使用空格
     *
     * @return 反转后的字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String reverseDelimited (String str, String separatorChars, String separator) {
        if (str == null) {
            return null;
        }

        String[] strs = split (str, separatorChars);

        ArrayUtils.reverse (strs);

        if (separator == null) {
            return join (strs, ' ');
        }

        return join (strs, separator);
    }

    /**
     * 将字符串转换成指定长度的缩略，例如： 将"Now is the time for all good men"转换成"Now is the time for..."。
     * <p/>
     * <ul>
     * <li>如果<code>str</code>比<code>maxWidth</code>短，直接返回；</li>
     * <li>否则将它转换成缩略：<code>substring(str, 0, max-3) + "..."</code>；</li>
     * <li>如果<code>maxWidth</code>小于<code>4</code>抛出 <code>IllegalArgumentException</code>；</li>
     * <li>返回的字符串不可能长于指定的<code>maxWidth</code>。</li>
     * </ul>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.abbreviate(null, *)      = null
     * StringUtils.abbreviate("", 4)        = ""
     * StringUtils.abbreviate("abcdefg", 6) = "abc..."
     * StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
     * StringUtils.abbreviate("abcdefg", 8) = "abcdefg"
     * StringUtils.abbreviate("abcdefg", 4) = "a..."
     * StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str      要检查的字符串
     * @param maxWidth 最大长度，不小于<code>4</code>，如果小于<code>4</code>，则看作 <code>4</code>
     *
     * @return 字符串缩略，如果原始字符串为<code>null</code>则返回<code>null</code>
     */
    public static String abbreviate (String str, int maxWidth) {
        return abbreviate (str, 0, maxWidth);
    }

    /*
     * ========================================================================== ==
     */
    /* 取得字符串的缩略。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 将字符串转换成指定长度的缩略，例如： 将"Now is the time for all good men"转换成"...is the time for..."。
     * <p/>
     * <p>
     * 和<code>abbreviate(String, int)</code>类似，但是增加了一个“左边界”偏移量。
     * 注意，“左边界”处的字符未必出现在结果字符串的最左边，但一定出现在结果字符串中。
     * </p>
     * <p/>
     * <p>
     * 返回的字符串不可能长于指定的<code>maxWidth</code>。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.abbreviate(null, *, *)                = null
     * StringUtils.abbreviate("", 0, 4)                  = ""
     * StringUtils.abbreviate("abcdefghijklmno", -1, 10) = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 0, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 1, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 4, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 5, 10)  = "...fghi..."
     * StringUtils.abbreviate("abcdefghijklmno", 6, 10)  = "...ghij..."
     * StringUtils.abbreviate("abcdefghijklmno", 8, 10)  = "...ijklmno"
     * StringUtils.abbreviate("abcdefghijklmno", 10, 10) = "...ijklmno"
     * StringUtils.abbreviate("abcdefghijklmno", 12, 10) = "...ijklmno"
     * StringUtils.abbreviate("abcdefghij", 0, 3)        = IllegalArgumentException
     * StringUtils.abbreviate("abcdefghij", 5, 6)        = IllegalArgumentException
     * </pre>
     * <p/>
     * </p>
     *
     * @param str      要检查的字符串
     * @param offset   左边界偏移量
     * @param maxWidth 最大长度，不小于<code>4</code>，如果小于<code>4</code>，则看作 <code>4</code>
     *
     * @return 字符串缩略，如果原始字符串为<code>null</code>则返回<code>null</code>
     */
    public static String abbreviate (String str, int offset, int maxWidth) {
        if (str == null) {
            return null;
        }

        // 调整最大宽度
        if (maxWidth < 4) {
            maxWidth = 4;
        }

        if (str.length () <= maxWidth) {
            return str;
        }

        if (offset > str.length ()) {
            offset = str.length ();
        }

        if ((str.length () - offset) < (maxWidth - 3)) {
            offset = str.length () - (maxWidth - 3);
        }

        if (offset <= 4) {
            return str.substring (0, maxWidth - 3) + "...";
        }

        // 调整最大宽度
        if (maxWidth < 7) {
            maxWidth = 7;
        }

        if ((offset + (maxWidth - 3)) < str.length ()) {
            return "..." + abbreviate (str.substring (offset), maxWidth - 3);
        }

        return "..." + str.substring (str.length () - (maxWidth - 3));
    }

    /**
     * 比较两个字符串，取得第二个字符串中，和第一个字符串不同的部分。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.difference("i am a machine", "i am a robot")  = "robot"
     * StringUtils.difference(null, null)                        = null
     * StringUtils.difference("", "")                            = ""
     * StringUtils.difference("", null)                          = ""
     * StringUtils.difference("", "abc")                         = "abc"
     * StringUtils.difference("abc", "")                         = ""
     * StringUtils.difference("abc", "abc")                      = ""
     * StringUtils.difference("ab", "abxyz")                     = "xyz"
     * StringUtils.difference("abcde", "abxyz")                  = "xyz"
     * StringUtils.difference("abcde", "xyz")                    = "xyz"
     * </pre>
     *
     * @param str1 字符串1
     * @param str2 字符串2
     *
     * @return 第二个字符串中，和第一个字符串不同的部分。如果两个字符串相同，则返回空字符串<code>""</code>
     */
    public static String difference (String str1, String str2) {
        if (str1 == null) {
            return str2;
        }

        if (str2 == null) {
            return str1;
        }

        int index = indexOfDifference (str1, str2);

        if (index == -1) {
            return EMPTY_STRING;
        }

        return str2.substring (index);
    }

    /*
     * ========================================================================== ==
     */
    /* 比较两个字符串的异同。 */
    /*                                                                              */
    /* 查找字符串之间的差异，比较字符串的相似度。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 比较两个字符串，取得两字符串开始不同的索引值。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.indexOfDifference("i am a machine", "i am a robot")   = 7
     * StringUtils.indexOfDifference(null, null)                         = -1
     * StringUtils.indexOfDifference("", null)                           = -1
     * StringUtils.indexOfDifference("", "")                             = -1
     * StringUtils.indexOfDifference("", "abc")                          = 0
     * StringUtils.indexOfDifference("abc", "")                          = 0
     * StringUtils.indexOfDifference("abc", "abc")                       = -1
     * StringUtils.indexOfDifference("ab", "abxyz")                      = 2
     * StringUtils.indexOfDifference("abcde", "abxyz")                   = 2
     * StringUtils.indexOfDifference("abcde", "xyz")                     = 0
     * </pre>
     *
     * @param str1 字符串1
     * @param str2 字符串2
     *
     * @return 两字符串开始产生差异的索引值，如果两字符串相同，则返回<code>-1</code>
     */
    public static int indexOfDifference (String str1, String str2) {
        if ((str1 == null) || (str2 == null) || (str1.equals (str2))) {
            return -1;
        }

        int i;

        for (i = 0; (i < str1.length ()) && (i < str2.length ()); ++i) {
            if (str1.charAt (i) != str2.charAt (i)) {
                break;
            }
        }

        if ((i < str2.length ()) || (i < str1.length ())) {
            return i;
        }

        return -1;
    }

    /**
     * 取得两个字符串的相似度，<code>0</code>代表字符串相等，数字越大表示字符串越不像。
     * <p/>
     * <p>
     * 这个算法取自<a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com /ld.htm</a>。
     * 它计算的是从字符串1转变到字符串2所需要的删除、插入和替换的步骤数。
     * </p>
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.getLevenshteinDistance(null, *)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance(*, null)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance("","")               = 0
     * StringUtils.getLevenshteinDistance("","a")              = 1
     * StringUtils.getLevenshteinDistance("aaapppp", "")       = 7
     * StringUtils.getLevenshteinDistance("frog", "fog")       = 1
     * StringUtils.getLevenshteinDistance("fly", "ant")        = 3
     * StringUtils.getLevenshteinDistance("elephant", "hippo") = 7
     * StringUtils.getLevenshteinDistance("hippo", "elephant") = 7
     * StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
     * StringUtils.getLevenshteinDistance("hello", "hallo")    = 1
     * </pre>
     *
     * @param s 第一个字符串，如果是<code>null</code>，则看作空字符串
     * @param t 第二个字符串，如果是<code>null</code>，则看作空字符串
     *
     * @return 相似度值
     */
    public static int getLevenshteinDistance (String s, String t) {
        s = defaultIfNull (s);
        t = defaultIfNull (t);

        int[][] d; // matrix
        int n; // length of s
        int m; // length of t
        int i; // iterates through s
        int j; // iterates through t
        char s_i; // ith character of s
        char t_j; // jth character of t
        int cost; // cost

        // Step 1
        n = s.length ();
        m = t.length ();

        if (n == 0) {
            return m;
        }

        if (m == 0) {
            return n;
        }

        d = new int[n + 1][m + 1];

        // Step 2
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        // Step 3
        for (i = 1; i <= n; i++) {
            s_i = s.charAt (i - 1);

            // Step 4
            for (j = 1; j <= m; j++) {
                t_j = t.charAt (j - 1);

                // Step 5
                if (s_i == t_j) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                // Step 6
                d[i][j] = min (d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }

        // Step 7
        return d[n][m];
    }

    /**
     * 取得最小数。
     *
     * @param a 整数1
     * @param b 整数2
     * @param c 整数3
     *
     * @return 三个数中的最小值
     */
    private static int min (int a, int b, int c) {
        if (b < a) {
            a = b;
        }

        if (c < a) {
            a = c;
        }

        return a;
    }

    /**
     * 比较两个字符串（大小写敏感）。
     * <p/>
     * <p>
     *
     * <pre>
     * StringUtils.equals(null, null)   = false
     * StringUtils.equals(null, "abc")  = true
     * StringUtils.equals("abc", null)  = true
     * StringUtils.equals("abc", "abc") = false
     * StringUtils.equals("abc", "ABC") = true
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     *
     * @return 如果两个字符串不相同，返回<code>true</code>
     */
    public static boolean notEquals (String str1, String str2) {
        if (str1 == null) {
            return str2 != null;
        }

        return !str1.equals (str2);
    }

    /**
     * 统计字符串出现个数
     *
     * @param origin 原始字符串
     * @param ch     比较的字符
     *
     * @return 如果统计的字符为空(即 " ", 不是指空格)返回-1；
     */

    public static int getNum (String origin, String ch) {
        int chLength = ch.length ();
        if (origin.length () == 0) {
            return 0;
        }
        if (chLength == 0) {
            return -1; // 如果统计的字符为空(即"",不是指空格)返回-1；
        }
        int index = 0;
        int count = 0;
        while (origin.indexOf (ch, index) != -1) {
            ++count;
            index = origin.indexOf (ch, index) + chLength;
        }
        return count;
    }

    /**
     * 获取最小的字符串,按照字符顺序排序，null是最大值
     *
     * @param strs
     *
     * @return
     */
    public static String min (String... strs) {
        if (strs == null || strs.length == 0) {
            return null;
        }
        String minStr = strs[0];
        for (String str : strs) {
            if (str != null) {
                if (minStr == null) {
                    minStr = str;
                } else {
                    minStr = str.compareTo (minStr) > 0 ? minStr : str;
                }
            }
        }
        return minStr;
    }

    /**
     * 获取最大的字符串,按照字符顺序排序，null是最大值
     *
     * @param strs
     *
     * @return
     */
    public static String max (String... strs) {
        if (strs == null || strs.length == 0) {
            return null;
        }
        String minStr = strs[0];
        for (String str : strs) {
            if (str != null) {
                if (minStr == null) {
                    minStr = str;
                } else {
                    minStr = str.compareTo (minStr) < 0 ? minStr : str;
                }
            } else {
                return null;
            }
        }
        return minStr;
    }

    /**
     * 字符数组转字节数组，默认编码
     *
     * @param chars
     *
     * @return
     */
    public static byte[] toByteArray (char[] chars) {
        byte[] bytes = new byte[chars.length];

        for (int i = 0; i != bytes.length; i++) {
            bytes[i] = (byte) chars[i];
        }

        return bytes;
    }

    /**
     * 字符串转字节数组，默认编码
     *
     * @param string
     *
     * @return
     */
    public static byte[] toByteArray (String string) {
        return toByteArray (string.toCharArray ());
    }

    public static int toByteArray (String s, byte[] buf, int off) {
        int count = s.length ();
        for (int i = 0; i < count; ++i) {
            char c = s.charAt (i);
            buf[off + i] = (byte) c;
        }
        return count;
    }

    /**
     * Convert an array of 8 bit characters into a string.
     *
     * @param bytes 8 bit characters.
     *
     * @return resulting String.
     */
    public static String fromByteArray (byte[] bytes) {
        return new String (asCharArray (bytes));
    }

    /**
     * Do a simple conversion of an array of 8 bit characters into a string.
     *
     * @param bytes 8 bit characters.
     *
     * @return resulting String.
     */
    public static char[] asCharArray (byte[] bytes) {
        char[] chars = new char[bytes.length];

        for (int i = 0; i != chars.length; i++) {
            chars[i] = (char) (bytes[i] & 0xff);
        }

        return chars;
    }

    /**
     * UTF-8 字节数组转字符串
     *
     * @param bytes
     *
     * @return
     */
    public static String fromUTF8ByteArray (byte[] bytes) {
        int i = 0;
        int length = 0;

        while (i < bytes.length) {
            length++;
            if ((bytes[i] & 0xf0) == 0xf0) {
                // surrogate pair
                length++;
                i += 4;
            } else if ((bytes[i] & 0xe0) == 0xe0) {
                i += 3;
            } else if ((bytes[i] & 0xc0) == 0xc0) {
                i += 2;
            } else {
                i += 1;
            }
        }

        char[] cs = new char[length];

        i = 0;
        length = 0;

        while (i < bytes.length) {
            char ch;

            if ((bytes[i] & 0xf0) == 0xf0) {
                int codePoint = ((bytes[i] & 0x03) << 18) | ((bytes[i + 1] & 0x3F) << 12)
                        | ((bytes[i + 2] & 0x3F) << 6) | (bytes[i + 3] & 0x3F);
                int U = codePoint - 0x10000;
                char W1 = (char) (0xD800 | (U >> 10));
                char W2 = (char) (0xDC00 | (U & 0x3FF));
                cs[length++] = W1;
                ch = W2;
                i += 4;
            } else if ((bytes[i] & 0xe0) == 0xe0) {
                ch = (char) (((bytes[i] & 0x0f) << 12) | ((bytes[i + 1] & 0x3f) << 6)
                        | (bytes[i + 2] & 0x3f));
                i += 3;
            } else if ((bytes[i] & 0xd0) == 0xd0) {
                ch = (char) (((bytes[i] & 0x1f) << 6) | (bytes[i + 1] & 0x3f));
                i += 2;
            } else if ((bytes[i] & 0xc0) == 0xc0) {
                ch = (char) (((bytes[i] & 0x1f) << 6) | (bytes[i + 1] & 0x3f));
                i += 2;
            } else {
                ch = (char) (bytes[i] & 0xff);
                i += 1;
            }

            cs[length++] = ch;
        }

        return new String (cs);
    }

    /**
     * 字符串转UTF-8字节数组
     *
     * @param string
     *
     * @return
     */
    public static byte[] toUTF8ByteArray (String string) {
        return toUTF8ByteArray (string.toCharArray ());
    }

    /**
     * 字符数组转UTF-8字节数组
     *
     * @param string
     *
     * @return
     */
    public static byte[] toUTF8ByteArray (char[] string) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream ();

        try {
            toUTF8ByteArray (string, bOut);
        } catch (IOException e) {
            throw new IllegalStateException ("cannot encode string to byte array!");
        }

        return bOut.toByteArray ();
    }

    private static void toUTF8ByteArray (char[] string, OutputStream sOut) throws IOException {
        char[] c = string;
        int i = 0;

        while (i < c.length) {
            char ch = c[i];

            if (ch < 0x0080) {
                sOut.write (ch);
            } else if (ch < 0x0800) {
                sOut.write (0xc0 | (ch >> 6));
                sOut.write (0x80 | (ch & 0x3f));
            }
            // surrogate pair
            else if (ch >= 0xD800 && ch <= 0xDFFF) {
                // in error - can only happen, if the Java String class has a
                // bug.
                if (i + 1 >= c.length) {
                    throw new IllegalStateException ("invalid UTF-16 codepoint");
                }
                char W1 = ch;
                ch = c[++i];
                char W2 = ch;
                // in error - can only happen, if the Java String class has a
                // bug.
                if (W1 > 0xDBFF) {
                    throw new IllegalStateException ("invalid UTF-16 codepoint");
                }
                int codePoint = (((W1 & 0x03FF) << 10) | (W2 & 0x03FF)) + 0x10000;
                sOut.write (0xf0 | (codePoint >> 18));
                sOut.write (0x80 | ((codePoint >> 12) & 0x3F));
                sOut.write (0x80 | ((codePoint >> 6) & 0x3F));
                sOut.write (0x80 | (codePoint & 0x3F));
            } else {
                sOut.write (0xe0 | (ch >> 12));
                sOut.write (0x80 | ((ch >> 6) & 0x3F));
                sOut.write (0x80 | (ch & 0x3F));
            }

            i++;
        }
    }

    /**
     * 解析出下列语法所构成的<code>SENTENCE</code>。
     * <p/>
     * <p>
     *
     * <pre>
     *  SENTENCE = WORD (DELIMITER* WORD)*
     *
     *  WORD = UPPER_CASE_WORD | LOWER_CASE_WORD | TITLE_CASE_WORD | DIGIT_WORD
     *
     *  UPPER_CASE_WORD = UPPER_CASE_LETTER+
     *  LOWER_CASE_WORD = LOWER_CASE_LETTER+
     *  TITLE_CASE_WORD = UPPER_CASE_LETTER LOWER_CASE_LETTER+
     *  DIGIT_WORD      = DIGIT+
     *
     *  UPPER_CASE_LETTER = Character.isUpperCase()
     *  LOWER_CASE_LETTER = Character.isLowerCase()
     *  DIGIT             = Character.isDigit()
     *  NON_LETTER_DIGIT  = !Character.isUpperCase() && !Character.isLowerCase() && !Character.isDigit()
     *
     *  DELIMITER = WHITESPACE | NON_LETTER_DIGIT
     * </pre>
     */
    private abstract static class WordTokenizer {
        protected static final char UNDERSCORE = '_';

        /**
         * Parse sentence。
         */
        public String parse (String str) {
            if (StringUtils.isEmpty (str)) {
                return str;
            }

            int length = str.length ();
            StringBuffer buffer = new StringBuffer (length);

            for (int index = 0; index < length; index++) {
                char ch = str.charAt (index);

                // 忽略空白。
                if (Character.isWhitespace (ch)) {
                    continue;
                }

                // 大写字母开始：UpperCaseWord或是TitleCaseWord。
                if (Character.isUpperCase (ch)) {
                    int wordIndex = index + 1;

                    while (wordIndex < length) {
                        char wordChar = str.charAt (wordIndex);

                        if (Character.isUpperCase (wordChar)) {
                            wordIndex++;
                        } else if (Character.isLowerCase (wordChar)) {
                            wordIndex--;
                            break;
                        } else {
                            break;
                        }
                    }

                    // 1. wordIndex == length，说明最后一个字母为大写，以upperCaseWord处理之。
                    // 2. wordIndex == index，说明index处为一个titleCaseWord。
                    // 3. wordIndex > index，说明index到wordIndex -
                    // 1处全部是大写，以upperCaseWord处理。
                    if ((wordIndex == length) || (wordIndex > index)) {
                        index = parseUpperCaseWord (buffer, str, index, wordIndex);
                    } else {
                        index = parseTitleCaseWord (buffer, str, index);
                    }

                    continue;
                }

                // 小写字母开始：LowerCaseWord。
                if (Character.isLowerCase (ch)) {
                    index = parseLowerCaseWord (buffer, str, index);
                    continue;
                }

                // 数字开始：DigitWord。
                if (Character.isDigit (ch)) {
                    index = parseDigitWord (buffer, str, index);
                    continue;
                }

                // 非字母数字开始：Delimiter。
                inDelimiter (buffer, ch);
            }

            return buffer.toString ();
        }

        private int parseUpperCaseWord (StringBuffer buffer, String str, int index, int length) {
            char ch = str.charAt (index++);

            // 首字母，必然存在且为大写。
            if (buffer.length () == 0) {
                startSentence (buffer, ch);
            } else {
                startWord (buffer, ch);
            }

            // 后续字母，必为小写。
            for (; index < length; index++) {
                ch = str.charAt (index);
                inWord (buffer, ch);
            }

            return index - 1;
        }

        private int parseLowerCaseWord (StringBuffer buffer, String str, int index) {
            char ch = str.charAt (index++);

            // 首字母，必然存在且为小写。
            if (buffer.length () == 0) {
                startSentence (buffer, ch);
            } else {
                startWord (buffer, ch);
            }

            // 后续字母，必为小写。
            int length = str.length ();

            for (; index < length; index++) {
                ch = str.charAt (index);

                if (Character.isLowerCase (ch)) {
                    inWord (buffer, ch);
                } else {
                    break;
                }
            }

            return index - 1;
        }

        private int parseTitleCaseWord (StringBuffer buffer, String str, int index) {
            char ch = str.charAt (index++);

            // 首字母，必然存在且为大写。
            if (buffer.length () == 0) {
                startSentence (buffer, ch);
            } else {
                startWord (buffer, ch);
            }

            // 后续字母，必为小写。
            int length = str.length ();

            for (; index < length; index++) {
                ch = str.charAt (index);

                if (Character.isLowerCase (ch)) {
                    inWord (buffer, ch);
                } else {
                    break;
                }
            }

            return index - 1;
        }

        private int parseDigitWord (StringBuffer buffer, String str, int index) {
            char ch = str.charAt (index++);

            // 首字符，必然存在且为数字。
            if (buffer.length () == 0) {
                startDigitSentence (buffer, ch);
            } else {
                startDigitWord (buffer, ch);
            }

            // 后续字符，必为数字。
            int length = str.length ();

            for (; index < length; index++) {
                ch = str.charAt (index);

                if (Character.isDigit (ch)) {
                    inDigitWord (buffer, ch);
                } else {
                    break;
                }
            }

            return index - 1;
        }

        protected boolean isDelimiter (char ch) {
            return !Character.isUpperCase (ch) && !Character.isLowerCase (ch)
                    && !Character.isDigit (ch);
        }

        protected abstract void startSentence (StringBuffer buffer, char ch);

        protected abstract void startWord (StringBuffer buffer, char ch);

        protected abstract void inWord (StringBuffer buffer, char ch);

        protected abstract void startDigitSentence (StringBuffer buffer, char ch);

        protected abstract void startDigitWord (StringBuffer buffer, char ch);

        protected abstract void inDigitWord (StringBuffer buffer, char ch);

        protected abstract void inDelimiter (StringBuffer buffer, char ch);
    }
}
