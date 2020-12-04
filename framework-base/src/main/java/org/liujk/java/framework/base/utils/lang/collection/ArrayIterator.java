
package org.liujk.java.framework.base.utils.lang.collection;



import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 说明：
 * <p>
 * 为数组提供的迭代器。
 * <p>
 * 该迭代器可支持迭代开始位置和结束位置，但不支持 {@link #remove()}。
 * <p>
 * {@link #getArray()} 返回的是一个底层数组的copy,任何对该数组的操作都不会影响到迭代器。
 * <p>
 * 该迭代器实现了 {@link Iterable} 接口，并且 {@link Iterable#iterator()} 方法返回自身而可用于 增强for循环 上。
 *
 */
public class ArrayIterator<E> implements Iterator<E>, Cloneable, Iterable<E> {

    /**
     * 迭代的数组
     */
    private E[] array;

    /**
     * 迭代的步长
     */
    private int step;

    /**
     * 迭代结束位置
     */
    private int endIndex;

    /**
     * 用一个数组构造一个新的 ArrayIterator。
     *
     * @param array 需要迭代的数组。
     *
     * @throws NullPointerException 如果 array 为 null。
     */
    public ArrayIterator (E[] array) {
        if (array == null) {
            throw new NullPointerException ();
        }
        this.array = array;
        this.step = 0;
        this.endIndex = array.length - 1;
    }

    /**
     * 用一个数组构造一个新的 ArrayIterator ，并指定迭代开始位置。
     *
     * @param array      需要迭代的数组。
     * @param startIndex 迭代开始位置，不能小于0。
     *
     * @throws NullPointerException     如果 array 为 null。
     * @throws IllegalArgumentException 如果 迭代开始位置小于 0 、迭代开始位置超过数组最大下标 时。
     */
    public ArrayIterator (E[] array, int startIndex) {
        if (array == null) {
            throw new NullPointerException ();
        }
        Assert.isTrue (startIndex >= 0, "迭代开始位置不能小于 0");
        this.array = array;
        this.step = startIndex;
        this.endIndex = array.length - 1;
        Assert.isTrue (startIndex <= this.endIndex, "迭代开始位置不能大于结束位置");
    }

    /**
     * 用一个数组构造一个新的 ArrayIterator ，并指定迭代开始位置和结束位置。
     *
     * @param array      需要迭代的数组。
     * @param startIndex 迭代开始位置，不能小于0。
     * @param endIndex   迭代结束位置，不能超过数组最大下标(array.length - 1)。
     *
     * @throws NullPointerException     如果 array 为 null。
     * @throws IllegalArgumentException 如果 迭代开始位置小于 0 、迭代开始位置大于结束位置 或者 迭代结束位置超过数组最大下标 时。
     */
    public ArrayIterator (E[] array, int startIndex, int endIndex) {
        if (array == null) {
            throw new NullPointerException ();
        }
        Assert.isTrue (startIndex >= 0, "迭代开始位置不能小于 0");
        Assert.isTrue (startIndex <= endIndex, "迭代开始位置不能大于结束位置");
        Assert.isTrue (endIndex < array.length, "迭代结束位置不能超过数组最大下标");
        this.array = array;
        this.step = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public boolean hasNext () {
        return (this.step <= this.endIndex);
    }

    @Override
    public E next () {
        if (this.step > this.endIndex) {
            throw new NoSuchElementException ("迭代器中没有更多的元素");
        }
        return this.array[this.step++];
    }

    /**
     * 抛出 UnsupportedOperationException。
     *
     * @throws UnsupportedOperationException 始终抛出该异常。
     */
    @Override
    public void remove () {
        throw new UnsupportedOperationException ();
    }

    /**
     * 得到底层的数组的一个copy。
     *
     * @return 底层的数组的一个copy。
     */
    public E[] getArray () {
        return this.array.clone ();
    }

    /**
     * 返回此 ArrayIterator 实例的浅表复制，并且复制出的对象状态与当前迭代器状态相同。
     *
     * @return 此 ArrayIterator 实例的一个克隆。
     */
    @SuppressWarnings("unchecked")
    @Override
    public ArrayIterator<E> clone () {
        ArrayIterator<E> clone;
        try {
            clone = (ArrayIterator<E>) super.clone ();
        } catch (CloneNotSupportedException e) {
            throw new InternalError ();
        }
        E[] cloneArray = (E[]) new Object[this.array.length];
        System.arraycopy (this.array, 0, cloneArray, 0, cloneArray.length);
        clone.array = cloneArray;
        return clone;
    }

    /**
     * 返回自身。
     *
     * @return 返回自身。
     */
    @Override
    public Iterator<E> iterator () {
        return this;
    }
}
