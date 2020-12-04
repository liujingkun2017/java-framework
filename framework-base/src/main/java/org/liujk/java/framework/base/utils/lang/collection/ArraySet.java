
package org.liujk.java.framework.base.utils.lang.collection;



import org.liujk.java.framework.base.utils.lang.ObjectUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 说明：
 * <p>
 * Set 接口的大小可变数组的实现。实现了所有可选集操作，并允许包括 null 在内的所有元素。除了实现 Set
 * 接口外，此类还提供一些方法来操作内部用来存储集的数组的大小。<strong>此实现不是同步的</strong>。
 * <p>
 * 在添加大量元素前，应用程序可以使用 ensureCapacity 操作来增加 ArraySet 实例的容量。这可以减少递增式再分配的数量。
 * <p>
 * 此类的 iterator 方法返回的迭代器是快速失败 的：在创建迭代器之后，如果对集合进行修改，除非通过迭代器自身的 remove 方法，否则在任何时间以任何方式对其进行修改，Iterator
 * 都将抛出 ConcurrentModificationException。因此，面对并发的修改 ，迭代器很快就会完全失败，而不冒将来在某个不确定时间发生任意不确定行为的风险。
 * <p>
 * 注意，迭代器的快速失败行为无法得到保证，因为一般来说，不可能对是否出现不同步并发修改做出任何硬性保证。快速失败迭代器在尽最大努力抛出
 * ConcurrentModificationException 。因此，为提高这类迭代器的正确性而编写一个依赖于此异常的程序是错误做法：迭代器的快速失败行为应该仅用于检测程序错误。
 *
 */
public class ArraySet<E> extends AbstractSet<E>
        implements RandomAccess, Serializable, Set<E>, Cloneable {

    /**
     * 版本号
     */
    private static final long serialVersionUID = 67506573217451881L;

    private ArrayList<E> arrayList;

    /**
     * 构造一个初始容量为 10 的空集。
     */
    public ArraySet () {
        this.arrayList = new ArrayList<E> ();
    }

    /**
     * 构造一个包含指定 collection 的元素的集，这些元素是按照该 collection 的迭代器返回它们的顺序排列的。
     *
     * @param collection 其元素将放置在此集中的 collection。
     *
     * @throws NullPointerException 如果指定的 collection 为 null。
     */
    public ArraySet (Collection<? extends E> collection) {
        if (collection == null) {
            throw new NullPointerException ();
        }
        this.arrayList = new ArrayList<E> (collection.size ());
        for (E e : collection) {
            add (e);
        }
    }

    /**
     * 构造一个具有指定初始容量的空集。
     *
     * @param initialCapacity 集的初始容量。
     *
     * @throws IllegalArgumentException 如果指定的初始容量为负。
     */
    public ArraySet (int initialCapacity) {
        this.arrayList = new ArrayList<E> (initialCapacity);
    }

    @Override
    public boolean add (E o) {
        if (this.arrayList.contains (o)) {
            return false;
        }
        return this.arrayList.add (o);
    }

    @Override
    public void clear () {
        this.arrayList.clear ();
    }

    /**
     * 返回此 ArraySet 实例的浅表复制。（不复制这些元素本身） 。
     *
     * @return 此 ArraySet 实例的一个克隆。
     */
    @Override
    @SuppressWarnings("unchecked")
    public ArraySet<E> clone () {
        ArraySet<E> clone;
        try {
            clone = (ArraySet<E>) super.clone ();
            clone.arrayList = (ArrayList<E>) this.arrayList.clone ();
        } catch (Exception e) {
            throw new InternalError ();
        }
        return clone;
    }

    @Override
    public boolean contains (Object elem) {
        return this.arrayList.contains (elem);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals (Object o) {
        if (o == null || !Set.class.isInstance (o)) {
            return false;
        }
        Set<Object> set = (Set<Object>) o;
        if (size () != set.size ()) {
            return false;
        }
        Iterator<Object> sIterator = set.iterator ();
        Iterator<E> iterator = iterator ();
        while (iterator.hasNext ()) {
            if (!ObjectUtils.isEquals (iterator.next (), sIterator.next ())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode () {
        return this.arrayList.hashCode ();
    }

    @Override
    public boolean isEmpty () {
        return this.arrayList.isEmpty ();
    }

    @Override
    public Iterator<E> iterator () {
        return this.arrayList.iterator ();
    }

    @Override
    public boolean remove (Object o) {
        return this.arrayList.remove (o);
    }

    @Override
    public int size () {
        return this.arrayList.size ();
    }

    @Override
    public Object[] toArray () {
        return this.arrayList.toArray ();
    }

    @Override
    public <T> T[] toArray (T[] a) {
        return this.arrayList.toArray (a);
    }

    /**
     * 如有必要，增加此 ArraySet 实例的容量，以确保它至少能够容纳最小容量参数所指定的元素数。
     *
     * @param minCapacity 所需的最小容量。
     */
    public void ensureCapacity (int minCapacity) {
        this.arrayList.ensureCapacity (minCapacity);
    }

    /**
     * 返回此集中指定位置上的元素。
     *
     * @param index 所返回元素的索引。
     *
     * @return 此集中指定位置上的元素。
     * @throws IndexOutOfBoundsException 如果索引超出范围 (index < 0 || index >= size())。
     */
    public E get (int index) {
        return this.arrayList.get (index);
    }

    /**
     * 搜索给定参数从前向后出现的位置，使用 equals 方法进行相等性测试。
     *
     * @param elem 一个对象。
     *
     * @return 搜索给定参数从前向后出现的位置，如果没有找到该对象，则返回 -1。
     */
    public int indexOf (Object elem) {
        return this.arrayList.indexOf (elem);
    }

    /**
     * 返回指定的对象在集中从后向前出现的位置索引。
     *
     * @param elem 所需的元素。
     *
     * @return 指定的对象在集中从后向前出现的位置索引；如果没有找到该对象，则返回 -1。
     */
    public int lastIndexOf (Object elem) {
        return this.arrayList.lastIndexOf (elem);
    }

    /**
     * 移除此集中指定位置上的元素。向左移动所有后续元素（将其索引减 1）。
     *
     * @param index 要移除的元素的索引。
     *
     * @return 从集中移除的元素。
     * @throws IndexOutOfBoundsException 如果索引超出范围 (index < 0 || index >= size())。
     */
    public E remove (int index) {
        return this.arrayList.remove (index);
    }

    /**
     * 将此 ArraySet 实例的容量调整为集的当前大小。应用程序可以使用此操作来最小化 ArraySet 实例的存储量。
     */
    public void trimToSize () {
        this.arrayList.trimToSize ();
    }
}
