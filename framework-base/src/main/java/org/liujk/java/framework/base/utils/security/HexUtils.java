
package org.liujk.java.framework.base.utils.security;



import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.base.utils.security.encoders.DecoderException;
import org.liujk.java.framework.base.utils.security.encoders.Encoder;
import org.liujk.java.framework.base.utils.security.encoders.EncoderException;
import org.liujk.java.framework.base.utils.security.encoders.HexEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 说明：
 * <p>
 *
 */
public class HexUtils {
    private static final Encoder ENCODER = new HexEncoder ();

    public static String toHexString (byte[] data) {
        return toHexString (data, 0, data.length);
    }

    public static String toHexString (byte[] data, int off, int length) {
        byte[] encoded = encode (data, off, length);
        return StringUtils.fromByteArray (encoded);
    }

    /**
     * encode the input data producing a Hex encoded byte array.
     *
     * @return a byte array containing the Hex encoded data.
     */
    public static byte[] encode (byte[] data) {
        return encode (data, 0, data.length);
    }

    /**
     * encode the input data producing a Hex encoded byte array.
     *
     * @return a byte array containing the Hex encoded data.
     */
    public static byte[] encode (byte[] data, int off, int length) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream ();

        try {
            ENCODER.encode (data, off, length, bOut);
        } catch (Exception e) {
            throw new EncoderException ("exception encoding Hex string: " + e.getMessage (), e);
        }

        return bOut.toByteArray ();
    }

    /**
     * Hex encode the byte data writing it to the given output stream.
     *
     * @return the number of bytes produced.
     */
    public static int encode (byte[] data, OutputStream out) throws IOException {
        return ENCODER.encode (data, 0, data.length, out);
    }

    /**
     * Hex encode the byte data writing it to the given output stream.
     *
     * @return the number of bytes produced.
     */
    public static int encode (byte[] data, int off, int length, OutputStream out)
            throws IOException {
        return ENCODER.encode (data, off, length, out);
    }

    /**
     * decode the Hex encoded input data. It is assumed the input data is valid.
     *
     * @return a byte array representing the decoded data.
     */
    public static byte[] decode (byte[] data) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream ();

        try {
            ENCODER.decode (data, 0, data.length, bOut);
        } catch (Exception e) {
            throw new DecoderException ("exception decoding Hex data: " + e.getMessage (), e);
        }

        return bOut.toByteArray ();
    }

    /**
     * decode the Hex encoded String data - whitespace will be ignored.
     *
     * @return a byte array representing the decoded data.
     */
    public static byte[] decode (String data) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream ();

        try {
            ENCODER.decode (data, bOut);
        } catch (Exception e) {
            throw new DecoderException ("exception decoding Hex string: " + e.getMessage (), e);
        }

        return bOut.toByteArray ();
    }

    /**
     * decode the Hex encoded String data writing it to the given output stream, whitespace
     * characters will be ignored.
     *
     * @return the number of bytes produced.
     */
    public static int decode (String data, OutputStream out) throws IOException {
        return ENCODER.decode (data, out);
    }

    /**
     * Parses a <code>short</code> from a hex encoded number. This method will skip all characters
     * that are not 0-9, A-F and a-f.
     * <p>
     * Returns 0 if the {@link CharSequence} does not contain any interesting characters.
     *
     * @param s the {@link CharSequence} to extract a <code>short</code> from, may not be
     *          <code>null</code>
     *
     * @return a <code>short</code>
     * @throws NullPointerException if the {@link CharSequence} is <code>null</code>
     */
    public static short parseShort (String s) {
        short out = 0;
        byte shifts = 0;
        char c;
        for (int i = 0; i < s.length () && shifts < 4; i++) {
            c = s.charAt (i);
            if ((c > 47) && (c < 58)) {
                ++shifts;
                out <<= 4;
                out |= c - 48;
            } else if ((c > 64) && (c < 71)) {
                ++shifts;
                out <<= 4;
                out |= c - 55;
            } else if ((c > 96) && (c < 103)) {
                ++shifts;
                out <<= 4;
                out |= c - 87;
            }
        }
        return out;
    }
}
