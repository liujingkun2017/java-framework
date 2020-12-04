
package org.liujk.java.framework.base.utils.security;



import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 说明：
 * <p>
 * MD5加密
 *
 */
public class MD5Utils {
    static ThreadLocal<MessageDigest> digestThreadLocal = new ThreadLocal<> ();
    private static Logger logger = LoggerFactory.getLogger (MD5Utils.class);

    /**
     * 对给定的字符串进行md5加密, 线程安全
     *
     * @param source
     *
     * @return 加密后的16进制的字符串
     */
    public final static String encode (String source) {
        if (logger.isDebugEnabled ()) {
            logger.debug ("加密的字符串：" + source);
        }
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                            'e', 'f'};
        try {

            byte[] strTemp = source.getBytes (Charsets.UTF_8);
            // 使用MD5创建MessageDigest对象
            MessageDigest mdTemp = getMessageDigest ();
            mdTemp.update (strTemp);
            byte[] md = mdTemp.digest ();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte b = md[i];
                str[k++] = hexDigits[b >> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }

            String md5Str = new String (str);
            if (logger.isDebugEnabled ()) {
                logger.debug ("加密后的字符串：{}", md5Str);
            }
            return md5Str;
        } catch (Exception e) {
            logger.error ("md5加密出错：" + source, e);
            return null;
        }
    }

    private static MessageDigest getMessageDigest () throws NoSuchAlgorithmException {
        MessageDigest messageDigest = digestThreadLocal.get ();
        if (messageDigest == null) {
            messageDigest = MessageDigest.getInstance ("MD5");
            digestThreadLocal.set (messageDigest);
        }
        messageDigest.reset ();
        return messageDigest;
    }
}
