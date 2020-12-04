
package org.liujk.java.framework.base.utils.lang.beans.collection;



import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 说明：
 * <p>
 * 数组对象包装器，该包装器满足数组对象的 {@link #hashCode()} 、 {@link #equals(Object)} 方法的一致性。
 *
 */
public class ArrayWrapper {
    private final Object array;

    /**
     * 构建一个 ArrayWrapper 。
     *
     * @param array 需要包装的数组。
     */
    public ArrayWrapper (Object array) {
        Assert.notNull (array,"array must not null");
        this.array = array;
    }

    private static void deepToString (Object a, StringBuilder buf, Set<Object> dejaVu) {
        if (a == null) {
            buf.append ("null");
            return;
        }
        dejaVu.add (a);
        buf.append ('[');
        int length = Array.getLength (a);
        for (int i = 0; i < length; i++) {
            if (i != 0) {
                buf.append (", ");
            }

            Object element = Array.get (a, i);
            if (element == null) {
                buf.append ("null");
            } else {
                Class<?> eClass = element.getClass ();

                if (eClass.isArray ()) {
                    if (eClass == byte[].class) {
                        buf.append (Arrays.toString ((byte[]) element));
                    } else if (eClass == short[].class) {
                        buf.append (Arrays.toString ((short[]) element));
                    } else if (eClass == int[].class) {
                        buf.append (Arrays.toString ((int[]) element));
                    } else if (eClass == long[].class) {
                        buf.append (Arrays.toString ((long[]) element));
                    } else if (eClass == char[].class) {
                        buf.append (Arrays.toString ((char[]) element));
                    } else if (eClass == float[].class) {
                        buf.append (Arrays.toString ((float[]) element));
                    } else if (eClass == double[].class) {
                        buf.append (Arrays.toString ((double[]) element));
                    } else if (eClass == boolean[].class) {
                        buf.append (Arrays.toString ((boolean[]) element));
                    } else {
                        if (dejaVu.contains (element)) {
                            buf.append ("[...]");
                        } else {
                            deepToString ((Object[]) element, buf, dejaVu);
                        }
                    }
                } else {
                    buf.append (element.toString ());
                }
            }
        }
        buf.append (']');
        dejaVu.remove (a);
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof ArrayWrapper) {
            return deepEquals (this.array, ((ArrayWrapper) obj).array);
        }
        if (!obj.getClass ().isArray ()) {
            return false;
        }
        return deepEquals (this.array, obj);
    }

    private boolean deepEquals (Object a1, Object a2) {
        if (a1 == a2) {
            return true;
        }
        if (a1 == null || a2 == null) {
            return false;
        }
        int length = Array.getLength (a1);
        if (Array.getLength (a2) != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            Object e1 = Array.get (a1, i);
            Object e2 = Array.get (a2, i);

            if (e1 == e2) {
                continue;
            }
            if (e1 == null) {
                return false;
            }

            boolean eq;
            if (e1 instanceof Object[] && e2 instanceof Object[]) {
                eq = deepEquals ((Object[]) e1, (Object[]) e2);
            } else if (e1 instanceof byte[] && e2 instanceof byte[]) {
                eq = Arrays.equals ((byte[]) e1, (byte[]) e2);
            } else if (e1 instanceof short[] && e2 instanceof short[]) {
                eq = Arrays.equals ((short[]) e1, (short[]) e2);
            } else if (e1 instanceof int[] && e2 instanceof int[]) {
                eq = Arrays.equals ((int[]) e1, (int[]) e2);
            } else if (e1 instanceof long[] && e2 instanceof long[]) {
                eq = Arrays.equals ((long[]) e1, (long[]) e2);
            } else if (e1 instanceof char[] && e2 instanceof char[]) {
                eq = Arrays.equals ((char[]) e1, (char[]) e2);
            } else if (e1 instanceof float[] && e2 instanceof float[]) {
                eq = Arrays.equals ((float[]) e1, (float[]) e2);
            } else if (e1 instanceof double[] && e2 instanceof double[]) {
                eq = Arrays.equals ((double[]) e1, (double[]) e2);
            } else if (e1 instanceof boolean[] && e2 instanceof boolean[]) {
                eq = Arrays.equals ((boolean[]) e1, (boolean[]) e2);
            } else {
                eq = e1.equals (e2);
            }
            if (!eq) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode () {
        return deepHashCode (this.array);
    }

    private int deepHashCode (Object a) {
        if (a == null) {
            return 0;
        }
        if (!a.getClass ().isArray ()) {
            return a.hashCode ();
        }
        int result = 1;
        int length = Array.getLength (a);
        for (int i = 0; i < length; i++) {
            Object element = Array.get (a, i);
            int elementHash = 0;
            if (element instanceof Object[]) {
                elementHash = deepHashCode ((Object[]) element);
            } else if (element instanceof byte[]) {
                elementHash = Arrays.hashCode ((byte[]) element);
            } else if (element instanceof short[]) {
                elementHash = Arrays.hashCode ((short[]) element);
            } else if (element instanceof int[]) {
                elementHash = Arrays.hashCode ((int[]) element);
            } else if (element instanceof long[]) {
                elementHash = Arrays.hashCode ((long[]) element);
            } else if (element instanceof char[]) {
                elementHash = Arrays.hashCode ((char[]) element);
            } else if (element instanceof float[]) {
                elementHash = Arrays.hashCode ((float[]) element);
            } else if (element instanceof double[]) {
                elementHash = Arrays.hashCode ((double[]) element);
            } else if (element instanceof boolean[]) {
                elementHash = Arrays.hashCode ((boolean[]) element);
            } else if (element != null) {
                elementHash = element.hashCode ();
            }
            result = 31 * result + elementHash;
        }

        return result;
    }

    @Override
    public String toString () {
        return deepToString (this.array);
    }

    private String deepToString (Object a) {
        if (a == null) {
            return "null";
        }
        if (!a.getClass ().isArray ()) {
            return a.toString ();
        }
        int length = Array.getLength (a);
        int bufLen = 20 * length;
        if (length != 0 && bufLen <= 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder buf = new StringBuilder (bufLen);
        deepToString (a, buf, new HashSet<Object> ());
        return buf.toString ();
    }

    /**
     * 得到被包装的数组对象。
     *
     * @return 被包装的数组对象。
     */
    public Object getArray () {
        return this.array;
    }
}
