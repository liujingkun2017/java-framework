
package org.liujk.java.framework.base.utils.lang;



import java.io.UnsupportedEncodingException;

/**
 * 说明：
 * <p>
 * 字符编码以及转换编码工具。
 *
 */
public class EncodingUtils {
    /**
     * 系统环境默认编码，如果没有则为 UTF-8
     */
    public static final String DEFAULT_CHARSET = StringUtils
            .checkEmptyString (System.getProperty ("file.encoding"), "UTF-8");
    /**
     * 十六进制编码字符数组
     */
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                                              '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 将字符串使用 charSet 指定的字符集解码后使用 encoding 指定的字符集编码。
     *
     * @param str      需要转换的字符串。
     * @param encoding 使用指定的字符集将此 String 编码，如果为null则为平台默认编码。
     * @param decoding 使用指定的字符集将此 String 解码，如果为null则为平台默认编码。
     *
     * @return 转换后的字符串。
     * @throws IllegalArgumentException 如果 encoding 或者 decoding 使用了不支持指定的编码。
     */
    public static String codeToString (String str, String encoding, String decoding) {
        if (str == null) {
            return str;
        }
        String s = str;
        byte[] byteStr;
        if (decoding == null) {
            byteStr = s.getBytes ();
        } else {
            try {
                byteStr = s.getBytes (decoding);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException ("解码时使用了不支持的编码字符集[" + decoding + "]。");
            }
        }
        if (encoding == null) {
            s = new String (byteStr);
        } else {
            try {
                s = new String (byteStr, encoding);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException ("编码时使用了不支持的编码字符集[" + encoding + "]。");
            }
        }
        return s;
    }

    /**
     * 转换 data 到16进制。
     *
     * @param data 要转换到16进制表示形式的字节数组。
     *
     * @return data 以16进制表示的 字符数组。
     */
    public static char[] encodeHex (byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = HEX_DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = HEX_DIGITS[0x0F & data[i]];
        }
        return out;
    }

    /**
     * 使用系统默认字符集转换 string 到16进制。
     *
     * @param string 要转换到16进制表示形式的字符串。
     *
     * @return string 以16进制表示的 字符数组。
     */
    public static char[] encodeHex (String string) {
        return encodeHex (string.getBytes ());
    }

    /**
     * 使用 charsetName 指定的字符集转换 string 到16进制。
     *
     * @param string      要转换到16进制表示形式的字符串。
     * @param charsetName 要用何种编码对字符串进行到字节数组（{@link String#getBytes(String)}）的转换。
     *
     * @return string 以16进制表示的 字符数组。
     * @throws IllegalArgumentException charsetName 使用了系统不支持的字符集时。
     */
    public static char[] encodeHex (String string, String charsetName) {
        try {
            return encodeHex (string.getBytes (charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException ("解码时使用了不支持的编码字符集[" + charsetName + "]。");
        }
    }
}
