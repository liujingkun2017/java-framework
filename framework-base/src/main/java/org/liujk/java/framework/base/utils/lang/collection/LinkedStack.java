
package org.liujk.java.framework.base.utils.lang.collection;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 说明：
 * <p>
 * Stack 接口的链接列表实现。
 * <p>
 * <strong>该实现不是同步的</strong>，它通常比 标准库 中的 {@link java.util.Stack} 快，且没有破坏栈结构的方法出现。
 * <p>
 * 此类的 iterator 方法返回的迭代器不支持 remove 操作。并且迭代器从栈顶开始迭代元素。同时该迭代器也是快速失败的：在迭代器创建之后，如果从结构上对列表进行修改，迭代器都将抛出
 * ConcurrentModificationException。因此，面对并发的修改，迭代器很快就会完全失败 ，而不冒将来不确定的时间任意发生不确定行为的风险。
 * <p>
 * 注意，迭代器的快速失败行为不能得到保证，一般来说，存在不同步的并发修改时，不可能作出任何硬性保证。快速失败迭代器尽最大努力抛出 ConcurrentModificationException
 * 。因此，编写依赖于此异常的程序的方式是错误的，正确做法是：迭代器的快速失败行为应该仅用于检测程序错误。
 *
 * @see
 */
public class LinkedStack<E> extends AbstractStack<E> implements Stack<E>, Serializable, Cloneable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = 7979888195061228678L;

    /**
     * 栈的高度(也是栈中的项数)
     */
    private transient int height = 0;

    /**
     * 头元素，它的上一个元素为栈头
     */
    private transient Item<E> headerItem;

    /**
     * 修改该栈的次数，每对该栈作一次修改就会使该数值+1
     */
    private transient int modCount = 0;

    /**
     * 构造一个新的栈。
     */
    public LinkedStack () {
        init ();
    }

    @Override
    public E peek () {
        if (this.height == 0) {
            throw new EmptyStackException ();
        }
        return this.headerItem.previous.item;
    }

    @Override
    public E pop () {
        E item = peek ();
        this.headerItem.previous = this.headerItem.previous.previous;
        this.headerItem.previous.next = this.headerItem;
        this.height--;
        this.modCount++;
        return item;
    }

    @Override
    public E push (E item) {
        // 新增加项的上一个项连接是头的上一个连接，下一个连接是头
        Item<E> newItem = new Item<E> (item, this.headerItem.previous, this.headerItem);
        newItem.previous.next = newItem;
        newItem.next.previous = newItem;
        this.height++;
        this.modCount++;
        return item;
    }

    @Override
    public int search (E item) {
        int index = 0;
        // 如果对象为 null ,则从栈顶向下查找第一个为 null 项的位置
        if (item == null) {
            for (Item<E> i = this.headerItem.previous; i != this.headerItem; i = i.previous) {
                index++;
                if (i.item == null) {
                    return index;
                }
            }
        } else {
            // 不为 null 时使用 equals 方法
            for (Item<E> i = this.headerItem.previous; i != this.headerItem; i = i.previous) {
                index++;
                if (item.equals (i.item)) {
                    return index;
                }
            }
        }
        return -1;
    }

    @Override
    public void clear () {
        Item<E> e = this.headerItem.next;
        while (e != this.headerItem) {
            Item<E> next = e.next;
            e.next = e.previous = null;
            e.item = null;
            e = next;
        }
        this.height = 0;
        this.modCount++;
    }

    /**
     * 初始化该栈。
     */
    private void init () {
        this.headerItem = new Item<E> (null, null, null);
        this.headerItem.previous = this.headerItem.next = this.headerItem;
        this.height = 0;
        this.modCount = 0;
    }

    @Override
    public Iterator<E> iterator () {
        return new LinkedStackIterator ();
    }

    /**
     * 返回此 Stack 实例的浅表复制。（不复制这些元素本身。）
     *
     * @return 此 Stack 实例的一个克隆。
     */
    @SuppressWarnings("unchecked")
    @Override
    public LinkedStack<E> clone () {
        LinkedStack<E> clone = null;
        try {
            clone = (LinkedStack<E>) super.clone ();
        } catch (CloneNotSupportedException e) {
            throw new InternalError ();
        }
        clone.init ();
        for (Item<E> e = this.headerItem.next; e != this.headerItem; e = e.next) {
            clone.push (e.item);
        }
        return clone;
    }

    @Override
    public int height () {
        return this.height;
    }

    @Override
    public Object[] toArray () {
        Object[] arrays = new Object[this.height];
        Item<E> iteratorItem = this.headerItem;
        for (int i = 0; i != this.height; i++) {
            iteratorItem = iteratorItem.previous;
            arrays[i] = iteratorItem.item;
        }
        return arrays;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] toArray (E[] array) {
        if (array == null) {
            throw new NullPointerException ();
        }
        if (array.length < height ()) {
            array = (E[]) Array.newInstance (array.getClass ().getComponentType (), this.height);
        }
        Item<E> iteratorItem = headerItem;
        for (int i = 0; i != this.height; i++) {
            iteratorItem = iteratorItem.previous;
            array[i] = iteratorItem.item;
        }
        if (array.length > this.height) {
            array[this.height] = null;
        }
        return array;
    }

    private void writeObject (ObjectOutputStream objectOutputStream) throws java.io.IOException {
        int expectedModCount = this.modCount;
        // 基本的
        objectOutputStream.defaultWriteObject ();
        objectOutputStream.writeInt (this.height);
        // 写入全部项
        for (Item<E> e = this.headerItem.next; e != this.headerItem; e = e.next) {
            objectOutputStream.writeObject (e.item);
        }
        if (this.modCount != expectedModCount) {
            throw new ConcurrentModificationException ();
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject (ObjectInputStream objectInputStream)
            throws java.io.IOException, ClassNotFoundException {
        // 基本的
        objectInputStream.defaultReadObject ();
        // 初始化头
        this.headerItem = new Item<E> (null, null, null);
        this.headerItem.previous = this.headerItem.next = this.headerItem;
        int count = objectInputStream.readInt ();
        // 读取并把各个项加入
        for (int i = 0; i < count; i++) {
            push ((E) objectInputStream.readObject ());
        }
    }

    /**
     * 代表栈中的项。
     */
    static class Item<E> {
        /**
         * 项
         */
        E item;
        /**
         * 前一个链结点
         */
        Item<E> previous;
        /**
         * 后一个链结点
         */
        Item<E> next;

        /**
         * 构造一个新的 Item。
         *
         * @param item     项
         * @param previous 前一个链结点
         * @param next     后一个链结点
         */
        public Item (E item, Item<E> previous, Item<E> next) {
            this.item = item;
            this.previous = previous;
            this.next = next;
        }
    }

    /**
     * 迭代器的实现，该迭代器从栈顶开始迭代元素，该迭代器的 remove 方法永远抛出 UnsupportedOperationException。
     */
    private class LinkedStackIterator implements Iterator<E> {

        private int count = 0;

        /**
         * 迭代器同步于栈修改次数的初值
         */
        private int expectedModCount = LinkedStack.this.modCount;

        private Item<E> iteratorItem = LinkedStack.this.headerItem;

        @Override
        public boolean hasNext () {
            return this.count != height ();
        }

        @Override
        public E next () {
            if (LinkedStack.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException ();
            }
            if (this.count >= height ()) {
                throw new NoSuchElementException ("迭代器中没有元素。");
            }
            this.iteratorItem = this.iteratorItem.previous;
            E e = this.iteratorItem.item;
            this.count++;
            return e;
        }

        /**
         * 该操作会破坏栈的特性，故不支持。
         *
         * @throws UnsupportedOperationException 永远抛出 UnsupportedOperationException
         */
        @Override
        public void remove () {
            throw new UnsupportedOperationException ("不支持的操作。");
        }

    }
}