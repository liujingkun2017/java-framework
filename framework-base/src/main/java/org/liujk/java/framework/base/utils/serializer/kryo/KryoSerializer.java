
package org.liujk.java.framework.base.utils.serializer.kryo;



import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * 说明：
 * <p>
 *
 */
public class KryoSerializer {
    protected byte[] serialize (Object o) {
        Kryo kryo = Kryos.getKryo ();
        Output output = Kryos.getOutput ();
        try {
            kryo.writeClassAndObject (output, o);
            return output.toBytes ();
        } finally {
            output.clear ();
        }

    }

    protected Object deserialize (byte[] in) {
        if (in == null) {
            return null;
        }
        Kryo kryo = Kryos.getKryo ();
        Input input = new Input ();
        input.setBuffer (in);
        return kryo.readClassAndObject (input);
    }
}
