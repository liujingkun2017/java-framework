
package org.liujk.java.framework.base.utils.security;



import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.base.utils.security.encoders.Base64Encoder;
import org.liujk.java.framework.base.utils.security.encoders.DecoderException;
import org.liujk.java.framework.base.utils.security.encoders.Encoder;
import org.liujk.java.framework.base.utils.security.encoders.EncoderException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 说明：
 * <p>
 *
 */
public class Base64Utils {
    private static final Encoder ENCODER = new Base64Encoder ();

    public static String toBase64String (byte[] data) {
        return toBase64String (data, 0, data.length);
    }

    public static String toBase64String (byte[] data, int off, int length) {
        byte[] encoded = encode (data, off, length);
        return StringUtils.fromByteArray (encoded);
    }

    /**
     * encode the input data producing a base 64 encoded byte array.
     *
     * @return a byte array containing the base 64 encoded data.
     */
    public static byte[] encode (byte[] data) {
        return encode (data, 0, data.length);
    }

    /**
     * encode the input data producing a base 64 encoded byte array.
     *
     * @return a byte array containing the base 64 encoded data.
     */
    public static byte[] encode (byte[] data, int off, int length) {
        int len = (length + 2) / 3 * 4;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream (len);

        try {
            ENCODER.encode (data, off, length, bOut);
        } catch (Exception e) {
            throw new EncoderException ("exception encoding base64 string: " + e.getMessage (), e);
        }

        return bOut.toByteArray ();
    }

    /**
     * Encode the byte data to base 64 writing it to the given output stream.
     *
     * @return the number of bytes produced.
     */
    public static int encode (byte[] data, OutputStream out) throws IOException {
        return ENCODER.encode (data, 0, data.length, out);
    }

    /**
     * Encode the byte data to base 64 writing it to the given output stream.
     *
     * @return the number of bytes produced.
     */
    public static int encode (byte[] data, int off, int length, OutputStream out)
            throws IOException {
        return ENCODER.encode (data, off, length, out);
    }

    /**
     * decode the base 64 encoded input data. It is assumed the input data is valid.
     *
     * @return a byte array representing the decoded data.
     */
    public static byte[] decode (byte[] data) {
        int len = data.length / 4 * 3;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream (len);

        try {
            ENCODER.decode (data, 0, data.length, bOut);
        } catch (Exception e) {
            throw new DecoderException ("unable to decode base64 data: " + e.getMessage (), e);
        }

        return bOut.toByteArray ();
    }

    /**
     * decode the base 64 encoded String data - whitespace will be ignored.
     *
     * @return a byte array representing the decoded data.
     */
    public static byte[] decode (String data) {
        int len = data.length () / 4 * 3;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream (len);

        try {
            ENCODER.decode (data, bOut);
        } catch (Exception e) {
            throw new DecoderException ("unable to decode base64 string: " + e.getMessage (), e);
        }

        return bOut.toByteArray ();
    }

    /**
     * decode the base 64 encoded String data writing it to the given output stream, whitespace
     * characters will be ignored.
     *
     * @return the number of bytes produced.
     */
    public static int decode (String data, OutputStream out) throws IOException {
        return ENCODER.decode (data, out);
    }

    /**
     * Decode to an output stream;
     *
     * @param base64Data The source data.
     * @param start      Start position.
     * @param length     the length.
     * @param out        The output stream to write to.
     */
    public static int decode (byte[] base64Data, int start, int length, OutputStream out) {
        try {
            return ENCODER.decode (base64Data, start, length, out);
        } catch (Exception e) {
            throw new DecoderException ("unable to decode base64 data: " + e.getMessage (), e);
        }

    }
}
