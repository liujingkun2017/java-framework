
package org.liujk.java.framework.base.utils.lang;

import java.util.Random;

/**
 * 说明：
 * <p>
 * 生成随机字符串，主要用于密码和盐值的生成
 *
 */
public class RandomStringUtils {
    /**
     * 小写字母
     */
    public static final String LOWER_CHARS_STR = "abcdefghijklmnopqrstuvwxyz";
    /**
     * 大写字母
     */
    public static final String UPPER_CHARS_STR = LOWER_CHARS_STR.toUpperCase ();
    /**
     * 数字
     */
    public static final String NUMERIC_CHARS_STR = "1234567890";
    /**
     * 特殊字符-
     */
    public static final String SPECIAL_CHARS_STR = "`~!@#$%^&*()_+-=[]|{},.";

    private static Random random = new Random ();

    /**
     * 随机生成字母和数字的字符串
     *
     * @param length
     *
     * @return
     */
    public static String random (int length) {
        return random (length, CharType.LETTERS_CHARS);
    }

    /**
     * 指定定字符串生成随机字符串
     *
     * @param length
     * @param charTypes
     *
     * @return
     */
    public static String random (int length, CharType... charTypes) {
        char[] chars = CharType.getCharArrays (charTypes);

        return random (length, chars);
    }

    /**
     * 指定字符串中获取随机字符
     *
     * @param length        生成随机字符的长度
     * @param randomCharStr 随机字符取值字符
     *
     * @return
     */
    public static String random (int length, String randomCharStr) {
        char[] chars = randomCharStr == null ? null : randomCharStr.toCharArray ();

        return random (length, chars);
    }

    /**
     * 指定字符数组生成随机字符串
     *
     * @param length 生成随机字符的长度
     * @param chars  指定的随机字符数组（不能为空）
     *
     * @return
     */
    public static String random (int length, final char[] chars) {
        if (length == 0) {
            return "";
        } else if (length < 0) {
            throw new IllegalArgumentException (
                    "Requested random string length " + length + " is less than 0.");
        }
        if (chars != null && chars.length == 0) {
            throw new IllegalArgumentException ("The chars array must not be empty");
        }

        char[] buffer = new char[length];
        for (int i = 0; i < length; i++) {
            buffer[i] = chars[random.nextInt (chars.length)];
        }

        return new String (buffer);
    }

    /**
     * <p>
     * Creates a random string based on a variety of options, using supplied source of randomness.
     * </p>
     * <p>
     * <p>
     * If start and end are both {@code 0}, start and end are set to {@code ' '} and {@code 'z'},
     * the ASCII printable characters, will be used, unless letters and numbers are both
     * {@code false}, in which case, start and end are set to {@code 0} and
     * {@code Integer.MAX_VALUE}.
     * <p>
     * <p>
     * If set is not {@code null}, characters between start and end are chosen.
     * </p>
     * <p>
     * <p>
     * This method accepts a user-supplied {@link Random} instance to use as a source of randomness.
     * By seeding a single {@link Random} instance with a fixed seed and using it for each call, the
     * same random sequence of strings can be generated repeatedly and predictably.
     * </p>
     *
     * @param count       the length of random string to create
     * @param start       the position in set of chars to start at
     * @param end         the position in set of chars to end before
     * @param ignoreChars ignore chars .
     *
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not {@code (end - start) + 1} characters in the set array.
     * @throws IllegalArgumentException       if {@code count} &lt; 0 or the provided chars array is empty.
     * @since 2.0
     */
    public static String random (int count, int start, int end, final char[] ignoreChars) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException (
                    "Requested random string length " + count + " is less than 0.");
        }

        if (start == 0 && end == 0) {
            end = 'z' + 1;
            start = ' ';
        } else {
            if (end <= start) {
                throw new IllegalArgumentException (
                        "Parameter end (" + end + ") must be greater than start (" + start + ")");
            }
        }

        final char[] buffer = new char[count];
        final int gap = end - start;

        while (count-- != 0) {
            char ch = (char) (random.nextInt (gap) + start);

            // 忽略的字符串
            boolean ignoreCh = false;
            if (ignoreChars != null && ignoreChars.length > 0) {
                for (char ic : ignoreChars) {
                    if (ic == ch) {
                        ignoreCh = true;
                        break;
                    }
                }
            }
            if (ignoreCh) {
                count++;
                continue;
            }

            buffer[count] = ch;
        }
        return new String (buffer);
    }

    public static enum CharType {
        /**
         * 小写字母
         */
        LOWER_CHARS (LOWER_CHARS_STR),

        /**
         * 大写字母
         */
        UPPER_CHARS (UPPER_CHARS_STR),

        /**
         * 字母
         */
        ALPHABET_CHARS (LOWER_CHARS_STR + UPPER_CHARS_STR),

        /**
         * 数字
         */
        NUMERIC_CHARS (NUMERIC_CHARS_STR),

        /**
         * 字母+数字
         */
        LETTERS_CHARS (LOWER_CHARS_STR + UPPER_CHARS_STR + NUMERIC_CHARS_STR),

        /**
         * 特殊字符
         */
        SPECIAL_CHARS (SPECIAL_CHARS_STR),

        /**
         * 所有字符
         */
        ALL_CHARS (LOWER_CHARS_STR + UPPER_CHARS_STR + NUMERIC_CHARS_STR + SPECIAL_CHARS);

        String randomChars;

        private CharType (String randomChars) {
            this.randomChars = randomChars;
        }

        public static char[] getCharArrays (CharType... charTypes) {
            if (charTypes == null || charTypes.length == 0) {
                return ALL_CHARS.getRandomChars ().toCharArray ();
            }
            if (charTypes.length == 1) {
                return charTypes[0].getRandomChars ().toCharArray ();
            }
            StringBuilder builder = new StringBuilder ();
            for (CharType charType : charTypes) {
                if (charType == null) {
                    continue;
                }
                builder.append (charType.getRandomChars ());
            }
            if (builder.length () == 0) {
                return ALL_CHARS.getRandomChars ().toCharArray ();
            }
            return builder.toString ().toCharArray ();
        }

        public String getRandomChars () {
            return randomChars;
        }
    }
}
