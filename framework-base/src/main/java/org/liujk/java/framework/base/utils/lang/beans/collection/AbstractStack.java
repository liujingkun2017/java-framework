
package org.liujk.java.framework.base.utils.lang.beans.collection;


import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * 说明：
 * <p>
 * 此类提供 Stack 接口的骨干实现，从而最大限度地减少了实现 Stack 接口所需的工作。
 *
 */
public abstract class AbstractStack<E> implements Stack<E> {

    /**
     * 返回此栈的哈希码值。
     *
     * @return 此栈的哈希码值。
     */
    @Override
    public int hashCode () {
        int hashCode = 1;
        Iterator<E> i = iterator ();
        while (i.hasNext ()) {
            E obj = i.next ();
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode ());
        }
        return hashCode;
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Stack<?>)) {
            return false;
        }
        Iterator<E> stackIterator = iterator ();
        Iterator<?> objectIterator = ((Stack<?>) obj).iterator ();
        while (stackIterator.hasNext () && objectIterator.hasNext ()) {
            E stackObj = stackIterator.next ();
            Object objObj = objectIterator.next ();
            if (!(stackObj == null ? objObj == null : stackObj.equals (objObj))) {
                return false;
            }
        }
        return !(objectIterator.hasNext () || objectIterator.hasNext ());
    }

    @Override
    public String toString () {
        Iterator<E> i = iterator ();
        if (!i.hasNext ()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder ();
        sb.append ('[');
        for (; ; ) {
            E e = i.next ();
            sb.append (e == this ? "(当前栈)" : e);
            if (!i.hasNext ()) {
                return sb.append (']').toString ();
            }
            sb.append (", ");
        }
    }

    @Override
    public boolean isEmpty () {
        return height () == 0;
    }

    @Override
    public boolean contains (E item) {
        return search (item) != -1;
    }

    @Override
    public Object[] toArray () {
        Object[] arrays = new Object[height ()];
        int i = 0;
        for (Iterator<E> iterator = iterator (); iterator.hasNext (); ) {
            arrays[i++] = iterator.next ();
        }
        return arrays;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] toArray (E[] array) {
        if (array == null) {
            throw new NullPointerException ();
        }
        int height = height ();
        if (array.length < height) {
            array = (E[]) Array.newInstance (array.getClass ().getComponentType (), height);
        }
        int i = 0;
        for (Iterator<E> iterator = iterator (); iterator.hasNext (); ) {
            array[i++] = iterator.next ();
        }
        if (array.length > height) {
            array[height] = null;
        }
        return array;
    }

}
