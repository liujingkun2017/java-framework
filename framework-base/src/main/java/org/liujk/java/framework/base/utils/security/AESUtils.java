
package org.liujk.java.framework.base.utils.security;


import org.springframework.util.Assert;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 说明：
 * <p>
 *
 */
public class AESUtils {
    /**
     * 注意key和加密用到的字符串是不一样的 加密还要指定填充的加密模式和填充模式 AES密钥可以是128或者256，加密模式包括ECB, CBC等
     * ECB模式是分组的模式，CBC是分块加密后，每块与前一块的加密结果异或后再加密 第一块加密的明文是与IV变量进行异或
     */
    public static final String KEY_ALGORITHM = "AES";
    public static final String ECB_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    public static final String CBC_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * ECB解密
     *
     * @param plain       明文
     * @param secretBytes 密钥，必须是128位的
     *
     * @return
     */
    public static byte[] encode (byte[] plain, byte[] secretBytes) {
        Assert.isTrue (secretBytes != null && secretBytes.length == 16,
                       "Must secretBytes != null and secretBytes.length == 16");
        return aesEcbEncode (plain, restoreSecretKey (secretBytes));
    }

    /**
     * CBC加密 128位
     *
     * @param plain       明文
     * @param secretBytes 密钥，必须是128位的
     * @param IV          IV(Initialization Value)是一个初始值，对于CBC模式来说，它必须是随机选取并且需要保密的 而且它的长度和密码分组相同(比如：对于AES
     *                    128为128位，即长度为16的byte类型数组)
     *
     * @return
     */
    public static byte[] encode (byte[] plain, byte[] secretBytes, byte[] IV) {
        Assert.isTrue (secretBytes != null && secretBytes.length == 16,
                       "Must secretBytes != null and secretBytes.length == 16");
        return aesCbcEncode (plain, restoreSecretKey (secretBytes), IV);
    }

    /**
     * ECB解密
     *
     * @param secret      ECB加密后的密文
     * @param secretBytes 密钥，必须是128位的
     *
     * @return
     */
    public static byte[] decode (byte[] secret, byte[] secretBytes) {
        Assert.isTrue (secretBytes != null && secretBytes.length == 16,
                       "Must secretBytes != null and secretBytes.length == 16");
        return aesEcbDecode (secret, restoreSecretKey (secretBytes));
    }

    /**
     * CBC解密
     *
     * @param secret      CBC加密后的密文
     * @param secretBytes 密钥，必须是128位的
     * @param IV          IV(Initialization Value)是一个初始值，对于CBC模式来说，它必须是随机选取并且需要保密的 而且它的长度和密码分组相同(比如：对于AES
     *                    128为128位，即长度为16的byte类型数组)
     *
     * @return
     */
    public static byte[] decode (byte[] secret, byte[] secretBytes, byte[] IV) {
        Assert.isTrue (secretBytes != null && secretBytes.length == 16,
                       "Must secretBytes != null && secretBytes.length == 16");
        return aesCbcDecode (secret, restoreSecretKey (secretBytes), IV);
    }

    /**
     * 将非128位的密码转换为128位
     *
     * @param key
     *
     * @return
     */
    public static byte[] toSecretBytes (String key) {
        return HexUtils.decode (MD5Utils.encode (key));
    }

    /**
     * 使用ECB模式进行加密。 加密过程三步走： 1. 传入算法，实例化一个加解密器 2. 传入加密模式和密钥，初始化一个加密器 3. 调用doFinal方法加密
     *
     * @param plainText
     *
     * @return
     */
    private static byte[] aesEcbEncode (byte[] plainText, SecretKey key) {

        try {

            Cipher cipher = Cipher.getInstance (ECB_CIPHER_ALGORITHM);
            cipher.init (Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal (plainText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace ();
        }
        return null;
    }

    /**
     * 使用ECB解密，三步走，不说了
     *
     * @param decodedText
     * @param key
     *
     * @return
     */
    private static byte[] aesEcbDecode (byte[] decodedText, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance (ECB_CIPHER_ALGORITHM);
            cipher.init (Cipher.DECRYPT_MODE, key);
            return cipher.doFinal (decodedText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * CBC加密，三步走，只是在初始化时加了一个初始变量
     *
     * @param plainText
     * @param key
     * @param IVParameter
     *
     * @return
     */
    private static byte[] aesCbcEncode (byte[] plainText, SecretKey key, byte[] IVParameter) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec (IVParameter);

            Cipher cipher = Cipher.getInstance (CBC_CIPHER_ALGORITHM);
            cipher.init (Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            return cipher.doFinal (plainText);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * CBC 解密
     *
     * @param decodedText
     * @param key
     * @param IVParameter
     *
     * @return
     */
    private static byte[] aesCbcDecode (byte[] decodedText, SecretKey key, byte[] IVParameter) {
        IvParameterSpec ivParameterSpec = new IvParameterSpec (IVParameter);

        try {
            Cipher cipher = Cipher.getInstance (CBC_CIPHER_ALGORITHM);
            cipher.init (Cipher.DECRYPT_MODE, key, ivParameterSpec);
            return cipher.doFinal (decodedText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * 1.创建一个KeyGenerator 2.调用KeyGenerator.generateKey方法 由于某些原因，这里只能是128，如果设置为256会报异常，原因在下面文字说明
     *
     * @return
     */
    public static byte[] generateAESSecretKey () {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance (KEY_ALGORITHM);
            // keyGenerator.init(256);
            return keyGenerator.generateKey ().getEncoded ();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * 还原密钥
     *
     * @param secretBytes
     *
     * @return
     */
    public static SecretKey restoreSecretKey (byte[] secretBytes) {
        SecretKey secretKey = new SecretKeySpec (secretBytes, KEY_ALGORITHM);
        return secretKey;
    }
}
