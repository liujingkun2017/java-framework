
package org.liujk.java.framework.base.utils.security.encoders;



import java.io.IOException;
import java.io.OutputStream;

/**
 * 说明：
 * <p>
 *
 */
public interface Encoder {
    int encode(byte[] data, int off, int length, OutputStream out) throws IOException;

    int decode(byte[] data, int off, int length, OutputStream out) throws IOException;

    int decode(String data, OutputStream out) throws IOException;
}