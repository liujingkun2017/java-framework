package org.liujk.java.framework.base.utils.lang.beans.reference;


import java.io.Serializable;
import java.lang.ref.Reference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConcurrentReferenceMap<K, V> extends AbstractMap<K, V>
        implements ConcurrentMap<K, V>, AutoClearable, Serializable {


    private static final FinalizableReferenceQueue FINALIZABLE_REFERENCE_QUEUE
            = new FinalizableReferenceQueue("ConcurrentReferenceMapReferenceQueue");

    private ReferenceKeyType keyReferenceKeyType;

    private ReferenceValueType valueReferenceKeyType;

    private transient int initialCapacity = 16;

    private transient float loadFactor = 0.75f;

    private transient int concurrencyLevel = 16;

    private transient ConcurrentMap<Object, Object> map;


    /**
     * 通过指定key引用类型和value引用类型，创建一个带有默认初始容量、加载因子和并发级别的map映射
     *
     * @param keyReferenceKeyType
     * @param valueReferenceKeyType
     */
    public ConcurrentReferenceMap(ReferenceKeyType keyReferenceKeyType,
                                  ReferenceValueType valueReferenceKeyType) {
        this.keyReferenceKeyType = keyReferenceKeyType;
        this.valueReferenceKeyType = valueReferenceKeyType;
        this.map = new ConcurrentHashMap<>(this.initialCapacity, this.loadFactor, this.concurrencyLevel);
    }

    /**
     * 通过指定key引用类型和value引用类型，创建一个带有默认初始容量、加载因子和并发级别的map映射
     *
     * @param keyReferenceKeyType
     * @param valueReferenceKeyType
     * @param initialCapacity
     */
    public ConcurrentReferenceMap(ReferenceKeyType keyReferenceKeyType,
                                  ReferenceValueType valueReferenceKeyType,
                                  int initialCapacity) {
        this.keyReferenceKeyType = keyReferenceKeyType;
        this.valueReferenceKeyType = valueReferenceKeyType;
        this.map = new ConcurrentHashMap<>(initialCapacity, this.loadFactor, this.concurrencyLevel);
    }

    /**
     * 通过指定key引用类型和value引用类型，创建一个带有默认初始容量、加载因子和并发级别的map映射
     *
     * @param keyReferenceKeyType
     * @param valueReferenceKeyType
     * @param initialCapacity
     * @param loadFactor
     * @param concurrencyLevel
     */
    public ConcurrentReferenceMap(ReferenceKeyType keyReferenceKeyType,
                                  ReferenceValueType valueReferenceKeyType,
                                  int initialCapacity,
                                  int loadFactor,
                                  int concurrencyLevel) {
        this.keyReferenceKeyType = keyReferenceKeyType;
        this.valueReferenceKeyType = valueReferenceKeyType;
        this.map = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
    }

    /**
     * 得到object的hashCode
     *
     * @param obj
     * @return
     */
    static int getHashCode(Object obj) {
        return System.identityHashCode(obj);
    }

    static void notNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }

    @Override
    public Set<K> keySet() {

        if (this.keyReferenceKeyType == ReferenceKeyType.STRONG) {
            //如果是强引用，则map中的keySet就是需要的
            return (Set<K>) this.map.keySet();
        }

        return new AbstractSet<K>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {

                    private Iterator<Object> iterator = ConcurrentReferenceMap.this.map.keySet().iterator();

                    @Override
                    public boolean hasNext() {
                        return this.iterator.hasNext();
                    }

                    @Override
                    public K next() {
                        return getKey(this.iterator.next());
                    }

                    @Override
                    public void remove() {
                        this.iterator.remove();
                    }
                };
            }

            @Override
            public int size() {
                return ConcurrentReferenceMap.this.map.size();
            }
        };
    }

    @Override
    public void clear() {
        this.map.clear();
    }


    @Override
    public V remove(Object key) {
        notNull(key);
        Object keyReference = createKeyReferenceWrapper(key);
        Object returnValueReference = this.map.remove(keyReference);
        return getValue(returnValueReference);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        return this.map.containsKey(createKeyReferenceWrapper(key));
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public V get(Object key) {
        notNull(key);
        Object keyReference = createKeyReferenceWrapper(key);
        Object returnValueReference = this.map.get(keyReference);
        return getValue(returnValueReference);
    }

    @Override
    public V put(K key, V value) {
        notNull(key);
        notNull(value);
        Object keyReference = wrapKey(key);
        Object valueReference = wrapValue(keyReference, value);
        Object returnValueReference = this.map.put(keyReference, valueReference);
        if (returnValueReference == null) {
            return null;
        }
        return getValue(returnValueReference);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {

                    private Iterator<Entry<Object, Object>> iterator = ConcurrentReferenceMap.this.map.entrySet().iterator();

                    @Override
                    public boolean hasNext() {
                        return this.iterator.hasNext();
                    }

                    @Override
                    public Entry<K, V> next() {
                        Entry<Object, Object> entry = this.iterator.next();
                        return new Entry<K, V>() {
                            @Override
                            public K getKey() {
                                return ConcurrentReferenceMap.this.getKey(entry.getKey());
                            }

                            @Override
                            public V getValue() {
                                return ConcurrentReferenceMap.this.getValue(entry.getValue());
                            }

                            @Override
                            public V setValue(V value) {
                                throw new UnsupportedOperationException("不支持的操作");
                            }

                        };
                    }

                    @Override
                    public void remove() {
                        this.iterator.remove();
                    }
                };
            }

            @Override
            public int size() {
                return ConcurrentReferenceMap.this.map.size();
            }
        };
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {

    }

    @Override
    public boolean remove(Object key, Object value) {
        notNull(key);
        Object keyReference = createKeyReferenceWrapper(key);
        Object valueReference = createValueReferenceWrapper(value);
        return this.map.remove(keyReference, valueReference);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        notNull(key);
        notNull(oldValue);
        notNull(newValue);
        Object keyReference = wrapKey(key);
        Object oldValueReference = createValueReferenceWrapper(oldValue);
        Object valueReference = wrapValue(keyReference, newValue);
        return this.map.replace(keyReference, oldValueReference, valueReference);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {

    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return null;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        notNull(key);
        notNull(value);
        Object keyReference = wrapKey(key);
        Object valueReference = wrapValue(keyReference, value);
        Object returnValueReference = this.map.putIfAbsent(keyReference, valueReference);
        if (returnValueReference == null) {
            return null;
        }
        return getValue(returnValueReference);
    }

    @Override
    public V replace(K key, V value) {
        notNull(key);
        notNull(value);
        Object keyReference = wrapKey(key);
        Object valueReference = wrapValue(keyReference, value);
        Object returnValueReference = this.map.replace(keyReference, valueReference);
        if (returnValueReference == null) {
            return null;
        }
        return getValue(returnValueReference);
    }


    private V getValue(Object valueReference) {
        return (V) (this.valueReferenceKeyType == ReferenceValueType.STRONG ? valueReference :
                valueReference == null ? null : ((Reference<V>) valueReference).get());
    }

    private K getKey(Object keyReference) {
        return (K) (this.keyReferenceKeyType == ReferenceKeyType.STRONG ? keyReference :
                keyReference == null ? null : ((Reference<V>) keyReference).get());
    }

    private Object createKeyReferenceWrapper(Object key) {
        return this.keyReferenceKeyType == ReferenceKeyType.STRONG ? key : new KeyReferenceWrapper(key);
    }

    private Object createValueReferenceWrapper(Object value) {
        return this.valueReferenceKeyType == ReferenceValueType.STRONG ? value : new ValueReferenceWrapper(value);
    }

    private boolean isEquals(Reference<?> reference, Object object) {
        if (reference == object) {
            return true;
        }
        if (reference == null) {
            return false;
        }
        if (object instanceof Reference<?>) {
            Object referenceValue = ((Reference) object).get();
            return referenceValue != null && isEquals(reference.get(), referenceValue);
        }
        if (object instanceof ReferenceWrapper) {
            return isEquals(((ReferenceWrapper) object).getReference(), reference.get());
        }
        return false;
    }

    private boolean isEquals(Object o1, Object o2) {
        return o1 == o2;
    }

    private Object wrapKey(K key) {
        Object keyReference = null;
        switch (this.keyReferenceKeyType) {
            case STRONG:
                keyReference = key;
                break;
            case SOFT:
                keyReference = new FinalizableKeySoftReference<K>(key);
                break;
            case WEAK:
                keyReference = new FinalizableKeyWeakReference<K>(key);
                break;
            case PHANTOM:
                keyReference = new FinalizableKeyPhantomReference<K>(key);
                break;
            default:
                break;
        }
        return keyReference;
    }

    private Object wrapValue(Object keyReference, V value) {
        Object valueReference = null;
        switch (this.valueReferenceKeyType) {
            case STRONG:
                valueReference = value;
                break;
            case SOFT:
                valueReference = new FinalizableValueSoftReference<V>(keyReference, value);
                break;
            case WEAK:
                valueReference = new FinalizableValueWeakReference<V>(keyReference, value);
                break;
            case PHANTOM:
                valueReference = new FinalizableValuePhantomReference<V>(keyReference, value);
                break;
            default:
                break;
        }
        return valueReference;
    }


    /**
     * 引用类型中的对象的另外一个包装器
     * 该包装器实现为引用类型中的对象保持put与get时用到的hashCode和equals方法的一致性
     */
    protected static class ReferenceWrapper {

        protected Object reference;

        protected ReferenceWrapper(Object reference) {
            this.reference = reference;
        }

        protected Object getReference() {
            return this.reference;
        }

        @Override
        public int hashCode() {
            return getHashCode(this.reference);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            return obj.equals(this);
        }
    }


    /**
     * 专用于key的引用类型中的对象的另外一个包装器
     */
    protected static class KeyReferenceWrapper extends ReferenceWrapper {

        /**
         * 使用一个引用类型中的key对象实例沟通一个新的包装器
         *
         * @param reference
         */
        protected KeyReferenceWrapper(Object reference) {
            super(reference);
        }
    }


    /**
     * 专用于value的引用类型中的对象的另外一个包装器
     */
    protected static class ValueReferenceWrapper extends ReferenceWrapper {

        /**
         * 使用一个引用类型中的value对象实例构造一个新的包装器
         *
         * @param reference
         */
        protected ValueReferenceWrapper(Object reference) {
            super(reference);
        }
    }

    protected class FinalizableKeySoftReference<T> extends FinalizableSoftReference<T>
            implements FinalizableReference<T> {

        private int hashCode;

        public FinalizableKeySoftReference(T referent) {
            super(referent, FINALIZABLE_REFERENCE_QUEUE);
            this.hashCode = getHashCode(referent);
        }

        @Override
        public void finalizeReferent() {
            ConcurrentReferenceMap.this.map.remove(this);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            return isEquals(this, obj);
        }
    }

    protected class FinalizableValueSoftReference<T> extends FinalizableSoftReference<T>
            implements FinalizableReference<T> {

        private Object keyReference;

        public FinalizableValueSoftReference(Object keyReference, T referent) {
            super(referent, FINALIZABLE_REFERENCE_QUEUE);
            this.keyReference = keyReference;
        }

        @Override
        public void finalizeReferent() {
            ConcurrentReferenceMap.this.map.remove(this.keyReference, this);
        }

        @Override
        public int hashCode() {
            return getHashCode(get());
        }

        @Override
        public boolean equals(Object obj) {
            return isEquals(this, obj);
        }
    }

    protected class FinalizableKeyWeakReference<T> extends FinalizableWeakReference<T>
            implements FinalizableReference<T> {

        private int hashCode;

        public FinalizableKeyWeakReference(T referent) {
            super(referent, FINALIZABLE_REFERENCE_QUEUE);
            this.hashCode = getHashCode(referent);
        }

        @Override
        public void finalizeReferent() {
            ConcurrentReferenceMap.this.map.remove(this);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            return isEquals(this, obj);
        }
    }

    protected class FinalizableValueWeakReference<T> extends FinalizableWeakReference<T>
            implements FinalizableReference<T> {

        private Object keyReference;

        public FinalizableValueWeakReference(Object keyReference, T referent) {
            super(referent, FINALIZABLE_REFERENCE_QUEUE);
            this.keyReference = keyReference;
        }

        @Override
        public void finalizeReferent() {
            ConcurrentReferenceMap.this.map.remove(this.keyReference, this);
        }

        @Override
        public int hashCode() {
            return getHashCode(get());
        }

        @Override
        public boolean equals(Object obj) {
            return isEquals(this, obj);
        }
    }

    protected class FinalizableKeyPhantomReference<T> extends FinalizablePhantomReference<T>
            implements FinalizableReference<T> {

        private int hashCode;

        public FinalizableKeyPhantomReference(T referent) {
            super(referent, FINALIZABLE_REFERENCE_QUEUE);
            this.hashCode = getHashCode(referent);
        }

        @Override
        public void finalizeReferent() {
            ConcurrentReferenceMap.this.map.remove(this);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            return isEquals(this, obj);
        }
    }

    protected class FinalizableValuePhantomReference<T> extends FinalizablePhantomReference<T>
            implements FinalizableReference<T> {

        private Object keyReference;

        public FinalizableValuePhantomReference(Object keyReference, T referent) {
            super(referent, FINALIZABLE_REFERENCE_QUEUE);
            this.keyReference = keyReference;
        }

        @Override
        public void finalizeReferent() {
            ConcurrentReferenceMap.this.map.remove(this.keyReference, this);
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return isEquals(this, obj);
        }
    }


}
