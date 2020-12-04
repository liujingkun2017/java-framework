
package org.liujk.java.framework.base.utils.lang.beans.collection;


import java.util.*;

/**
 * 说明：
 * <p>
 * 对 {@link MultiValueMap} 使用哈希表和链接列表的实现。内部的多值存储使用 {@link LinkedList} 实现，其他特性同它的父类
 * {@link LinkedHashMap}。
 * <p>
 * <p>
 * <strong>该实现不是同步的。</strong>
 * <p>
 * 注意，迭代器的快速失败行为不能得到保证，一般来说，存在不同步的并发修改时，不可能作出任何硬性保证。快速失败迭代器尽最大努力抛出 ConcurrentModificationException
 * 。因此，编写依赖于此异常的程序的方式是错误的，正确做法是：迭代器的快速失败行为应该仅用于检测程序错误。
 *
 * @see LinkedHashMap
 */
public class LinkedMultiValueMap<K, V> extends LinkedHashMap<K, List<V>>
        implements MultiValueMap<K, V> {

    /**
     * 版本号
     */
    private static final long serialVersionUID = -6503370799407269817L;

    transient volatile Set<Map.Entry<K, V>> singleEntrySet;

    transient volatile Collection<V> singleValues;

    /**
     * 构造一个带默认初始容量 (16) 和加载因子 (0.75) 的空插入顺序 LinkedMultiValueMap 实例。
     */
    public LinkedMultiValueMap () {
        super ();
    }

    /**
     * 构造一个带指定初始容量和默认加载因子 (0.75) 的空插入顺序 LinkedMultiValueMap 实例。
     *
     * @param initialCapacity 初始容量。
     *
     * @throws IllegalArgumentException 如果初始容量为负。
     */
    public LinkedMultiValueMap (int initialCapacity) {
        super (initialCapacity);
    }

    /**
     * 构造一个带指定初始容量和加载因子的空插入顺序 LinkedMultiValueMap 实例。
     *
     * @param initialCapacity 初始容量。
     * @param loadFactor      加载因子。
     *
     * @throws IllegalArgumentException 如果初始容量为负或者加载因子为非正。
     */
    public LinkedMultiValueMap (int initialCapacity, float loadFactor) {
        super (initialCapacity, loadFactor);
    }

    /**
     * 构造一个带指定初始容量、加载因子和排序模式的空 LinkedMultiValueMap 实例。
     *
     * @param initialCapacity 初始容量。
     * @param loadFactor      加载因子。
     * @param accessOrder     排序模式 - 对于访问顺序，为 true；对于插入顺序，则为 false。
     *
     * @throws IllegalArgumentException 如果初始容量为负或者加载因子为非正。
     */
    public LinkedMultiValueMap (int initialCapacity, float loadFactor, boolean accessOrder) {
        super (initialCapacity, loadFactor, accessOrder);
    }

    /**
     * 构造一个映射关系与指定映射相同的插入顺序 LinkedMultiValueMap 实例。所创建的 LinkedMultiValueMap 实例具有默认的加载因子 (0.75)
     * 和足以容纳指定映射中映射关系的初始容量。
     *
     * @param m 要将其映射关系存放在此映射中的映射。
     *
     * @throws NullPointerException 如果指定的映射为 null。
     */
    public LinkedMultiValueMap (Map<? extends K, ? extends List<V>> m) {
        super (m);
    }

    @Override
    public void add (K key, V value) {
        List<V> values = get (key);
        if (values == null) {
            values = new LinkedList<V> ();
            put (key, values);
        }
        values.add (value);
    }

    @Override
    public V getFirst (K key) {
        List<V> values = get (key);
        return (values != null ? values.get (0) : null);
    }

    @Override
    public void set (K key, V value) {
        List<V> values = new LinkedList<V> ();
        values.add (value);
        put (key, values);
    }

    @Override
    public void setAll (Map<K, V> values) {
        for (Map.Entry<K, V> entry : values.entrySet ()) {
            set (entry.getKey (), entry.getValue ());
        }
    }

    @Override
    public Map<K, V> toSingleValueMap () {
        LinkedHashMap<K, V> singleValueMap = new LinkedHashMap<K, V> (size ());
        for (Map.Entry<K, List<V>> entry : entrySet ()) {
            singleValueMap.put (entry.getKey (), entry.getValue ().get (0));
        }
        return singleValueMap;
    }

    @Override
    public Set<Map.Entry<K, V>> singleValueEntrySet () {
        if (this.singleEntrySet == null) {
            this.singleEntrySet = new AbstractSet<Map.Entry<K, V>> () {
                private final Set<Map.Entry<K, List<V>>> entrySet = entrySet ();

                @Override
                public boolean add (Map.Entry<K, V> o) {
                    throw new UnsupportedOperationException ();
                }

                @Override
                public boolean addAll (Collection<? extends Map.Entry<K, V>> c) {
                    throw new UnsupportedOperationException ();
                }

                @Override
                public void clear () {
                    this.entrySet.clear ();
                }

                @Override
                public boolean contains (Object o) {
                    return this.entrySet.contains (o);
                }

                @Override
                public boolean isEmpty () {
                    return this.entrySet.isEmpty ();
                }

                @Override
                public Iterator<Map.Entry<K, V>> iterator () {
                    return new Iterator<Map.Entry<K, V>> () {
                        private Iterator<Map.Entry<K, List<V>>> iterator = entrySet.iterator ();

                        @Override
                        public boolean hasNext () {
                            return this.iterator.hasNext ();
                        }

                        @Override
                        public Map.Entry<K, V> next () {
                            final Map.Entry<K, List<V>> entry = this.iterator.next ();
                            return new Map.Entry<K, V> () {

                                @Override
                                public K getKey () {
                                    return entry.getKey ();
                                }

                                @Override
                                public V getValue () {
                                    return entry.getValue ().get (0);
                                }

                                @Override
                                public V setValue (V value) {
                                    V oldValue = LinkedMultiValueMap.this.getFirst (entry.getKey ());
                                    LinkedMultiValueMap.this.set (entry.getKey (), value);
                                    return oldValue;
                                }

                            };
                        }

                        @Override
                        public void remove () {
                            this.iterator.remove ();
                        }
                    };
                }

                @Override
                public int size () {
                    return this.entrySet.size ();
                }

            };
        }
        return this.singleEntrySet;
    }

    @Override
    public Collection<V> singleValues () {
        if (this.singleValues == null) {
            this.singleValues = new AbstractCollection<V> () {
                @Override
                public Iterator<V> iterator () {
                    return new Iterator<V> () {
                        private Iterator<Map.Entry<K, V>> iterator = LinkedMultiValueMap.this
                                .singleValueEntrySet ().iterator ();

                        @Override
                        public boolean hasNext () {
                            return this.iterator.hasNext ();
                        }

                        @Override
                        public V next () {
                            return this.iterator.next ().getValue ();
                        }

                        @Override
                        public void remove () {
                            this.iterator.remove ();
                        }
                    };
                }

                @Override
                public int size () {
                    return LinkedMultiValueMap.this.size ();
                }

                @Override
                public boolean contains (Object v) {
                    return LinkedMultiValueMap.this.containsValue (v);
                }
            };
        }
        return this.singleValues;
    }

}
