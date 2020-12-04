
package org.liujk.java.framework.base.utils.lang;


import com.google.common.collect.Lists;
import org.liujk.java.framework.base.api.response.page.Page;
import org.liujk.java.framework.base.api.response.page.Pageable;
import org.liujk.java.framework.base.utils.lang.collection.AbstractStack;
import org.liujk.java.framework.base.utils.lang.collection.ArrayIterator;
import org.liujk.java.framework.base.utils.lang.collection.MultiValueMap;
import org.liujk.java.framework.base.utils.lang.collection.Stack;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

/**
 * 说明：
 * <p>
 * 集合工具。做为 {@link Collections} 的补充与工具包中的 "collection" 类的支持。
 * <p>
 * <p>
 * 如果没有特殊说明，在调研该类的“包装器”方法时，如果传递参数为 null 则会抛出 {@link IllegalArgumentException} 。
 */
public class CollectionUtils {
    /**
     * 空的 Stack（不可变的）。此 Stack 是可序列化的。
     *
     * @see #emptyStack()
     */
    @SuppressWarnings({"rawtypes"})
    public static final Stack EMPTY_STACK = new EmptyStack();
    /**
     * 空的 Queue（不可变的）。此 Queue 是可序列化的。
     *
     * @see #emptyQueue()
     */
    @SuppressWarnings({"rawtypes"})
    public static final Queue EMPTY_QUEUE = new EmptyQueue();
    /**
     * 空的 MultiValueMap（不可变的）。此 MultiValueMap 是可序列化的。
     */
    @SuppressWarnings({"rawtypes"})
    public static final MultiValueMap EMPTY_MULTI_VALUE_MAP = new EmptyMultiValueMap();
    /**
     * 空的 Iterator（不可变的）。此 Iterator 是可序列化的。
     *
     * @see #emptyIterator()
     */
    @SuppressWarnings({"rawtypes"})
    public static final Iterator EMPTY_ITERATOR = new EmptyIterator();
    /**
     * 空的 Enumeration（不可变的）。此 Enumeration 是可序列化的。
     *
     * @see #emptyEnumeration()
     */
    @SuppressWarnings({"rawtypes"})
    public static final Enumeration EMPTY_ENUMERATIONR = new EmptyEnumeration();
    static final int HASH_MAP_DEFAULT_INITIAL_CAPACITY = 16;
    static final float HASH_MAP_DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 得到Collection的第一个元素。
     *
     * @param c 需要得到第一个元素的Collection。
     * @return 对应的第一个元素，如果 c 为null 或者 empty ，则返回 null 。
     * @since 2.1
     */
    public static <E> E getFirst(Collection<E> c) {
        if (c == null) {
            return null;
        }
        if (c instanceof List) {
            List<E> list = (List<E>) c;
            return list.isEmpty() ? null : list.get(0);
        } else if (c instanceof Set) {
            return c.isEmpty() ? null : c.iterator().next();
        } else if (c instanceof Queue) {
            return c.isEmpty() ? null : ((Queue<E>) c).peek();
        } else {
            return c.isEmpty() ? null : c.iterator().next();
        }
    }

    /**
     * 使用 element 拆分一个 List 。
     * <p>
     * 假设 list 中的元素为 e ，则比较使用 <code>element == null ? e == null : element.equals(e)</code>作为判断依据。
     * <p>
     * element 作为被拆分对象会被舍去。
     *
     * @param list    需要拆分的 List 。
     * @param element 拆分元素。
     * @return 根据 element 对 list 进行拆分的结果，如果 list 为 null 则返回空的 List 。
     */
    public static <E> List<List<E>> split(List<E> list, E element) {
        if (list == null) {
            return new ArrayList<List<E>>(0);
        }
        List<List<E>> result = new ArrayList<List<E>>();
        int start = 0, end = 0;
        for (E e : list) {
            if (element == null ? e == null : element.equals(e)) {
                result.add(new ArrayList<E>(list.subList(start, end)));
                start = end + 1;
            }
            end++;
        }
        if (start == list.size()) {
            result.add(new ArrayList<E>(0));
        } else if (start < list.size()) {
            result.add(new ArrayList<E>(list.subList(start, list.size())));
        }
        return result;
    }

    /**
     * 将 list 使用 index 指定的下标位置进行拆分。
     * <p>
     * index 下标不包含在被拆分的前段而在后段。
     *
     * @param list  需要拆分的 List 。
     * @param index 拆分 list 的下标，不能超过 list 的长度。
     * @return 根据 index 对 list 拆分的结果，如果 list 为 null 则返回空的 List 。
     * @throws IllegalArgumentException 如果 index 数量超过 list 长度，或者 index 的不是每个值都比前一个大，或者 index 值大于 list 长度。
     */
    public static <E> List<List<E>> split(List<E> list, int... index) {
        if (list == null) {
            return new ArrayList<List<E>>(0);
        }
        if (index == null || index.length == 0) {
            ArrayList<List<E>> arrayList = new ArrayList<List<E>>();
            arrayList.add(new ArrayList<E>(list));
            return arrayList;
        }
        if (index.length > list.size()) {
            throw new IllegalArgumentException("{index}数量不能大于{list}长度。");
        }
        List<List<E>> result = new ArrayList<List<E>>();
        int temp = 0;
        for (int i : index) {
            if (i < temp) {
                throw new IllegalArgumentException("{index}的每个值必须比前一个大。");
            }
            if (i > list.size()) {
                throw new IllegalArgumentException("{index}值不能大于{list}长度。");
            }
            result.add(new ArrayList<E>(list.subList(temp, i)));
            temp = i;
        }
        if (temp == list.size()) {
            result.add(new ArrayList<E>(0));
        } else if (temp < list.size()) {
            result.add(new ArrayList<E>(list.subList(temp, list.size())));
        }
        return result;
    }

    /**
     * 过滤一个 collection ，排除掉 elements 相等的元素。
     * <p>
     * 假设 collection 中的元素为 e ，则比较使用
     * <code>element == null ? e == null : element.equals(e)</code>作为判断依据。
     *
     * @param collection 需要过滤的 collection 。
     * @param elements   需要排除的元素列表。
     * @throws UnsupportedOperationException 如果 collection 的迭代器不支持修改操作。
     */
    public static <C extends Collection<?>> void filterExclude(C collection, Object... elements) {
        filter(collection, true, elements);
    }

    /**
     * 过滤一个 collection ，只保留 elements 相等的元素。
     * <p>
     * 假设 collection 中的元素为 e ，则比较使用
     * <code>element == null ? e == null : element.equals(e)</code>作为判断依据。
     *
     * @param collection 需要过滤的 collection 。
     * @param elements   需要保留的元素列表。
     * @throws UnsupportedOperationException 如果 collection 的迭代器不支持修改操作。
     */
    public static <C extends Collection<?>> void filterInclude(C collection, Object... elements) {
        filter(collection, false, elements);
    }

    private static <C extends Collection<?>> void filter(C collection, boolean isExclude,
                                                         Object... elements) {
        if (collection == null) {
            return;
        }
        if (ArrayUtils.isEmpty(elements)) {
            if (!isExclude) {
                collection.clear();
            }
            return;
        }
        l1:
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext(); ) {
            Object element = iterator.next();
            boolean isInclude = false;
            for (Object object : elements) {
                boolean isEquals = object.equals(element);
                if (isExclude && isEquals) {
                    iterator.remove();
                    continue l1;
                }
                if (isEquals) {
                    isInclude = true;
                }
            }
            if (!isExclude && !isInclude) {
                iterator.remove();
            }
        }
    }

    /**
     * 包装一个数组为 {@link List}，数组的修改会影响到返回的 {@link List}。
     * <p>
     * 返回的 {@link List} 不允许任何 add 与 clear 等改变 array 性质的操作，但是允许 set 操作。
     *
     * @param array 要包装的数组。
     * @return 包装着数组 array 的 {@link List}，如果 array 为 null 则返回 null。
     */
    public static <T> List<T> wrap(final T[] array) {
        if (array == null) {
            return null;
        }
        return new AbstractList<T>() {

            @Override
            public T get(int index) {
                return array[index];
            }

            @Override
            public int size() {
                return array.length;
            }

            @Override
            public T set(int index, T element) {
                T old = array[index];
                array[index] = element;
                return old;
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int indexOf(Object o) {
                if (o == null) {
                    for (int i = 0; i < array.length; i++) {
                        if (null == array[i]) {
                            return i;
                        }
                    }
                } else {
                    for (int i = 0; i < array.length; i++) {
                        if (o.equals(array[i])) {
                            return i;
                        }
                    }
                }
                return -1;
            }

            @Override
            public int lastIndexOf(Object o) {
                if (o == null) {
                    for (int i = array.length - 1; i > -1; i--) {
                        if (null == array[i]) {
                            return i;
                        }
                    }
                } else {
                    for (int i = array.length - 1; i > -1; i--) {
                        if (o.equals(array[i])) {
                            return i;
                        }
                    }
                }
                return -1;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * 返回 map 中包含的值的 List 视图。map 之后的改变不会反映到返回的 List 中。
     *
     * @param map map。
     * @return map 中包含的值的 List 视图。
     */
    public static <T> List<T> values(Map<?, T> map) {
        if (map == null) {
            return null;
        }
        List<T> list = new ArrayList<T>(map.size());
        for (T t : map.values()) {
            list.add(t);
        }
        return list;
    }

    /**
     * 返回 map 中包含的建的 List 视图。map 之后的改变不会反映到返回的 List 中。
     *
     * @param map map。
     * @return map 中包含的建的 List 视图。
     */
    public static <T> List<T> keys(Map<T, ?> map) {
        if (map == null) {
            return null;
        }
        List<T> list = new ArrayList<T>(map.size());
        for (T t : map.keySet()) {
            list.add(t);
        }
        return list;
    }

    /**
     * 得到 element 在 elements 中出现的次数。
     *
     * @param element  元素。
     * @param elements element 元素的 {@link Iterable} 。
     * @return element 在 elements 中出现的次数，如果 elements 为 null ，则返回 0 。
     */
    public static <E> int countOccurrence(E element, Iterable<E> elements) {
        if (elements == null) {
            return 0;
        }
        int i = 0;
        if (element == null) {
            for (E e : elements) {
                if (null == e) {
                    i++;
                }
            }
        } else {
            for (E e : elements) {
                if (element.equals(e)) {
                    i++;
                }
            }
        }
        return i;
    }

    /**
     * 将一个集合转换为数组形式，该方法返回的数组为该 {@link Collection} 的迭代器相同规则的数组。
     *
     * @param <E>        其中的元素。
     * @param collection 要转换的集合。
     * @param typeClass  要转换到的数组的元素类型。
     * @return collection 参数集合所对应的数组形式。
     * @see Collection#iterator()
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] toArray(Collection<E> collection, Class<E> typeClass) {
        E[] t = (E[]) Array.newInstance(typeClass, collection.size());
        int i = 0;
        for (Iterator<E> iterator = collection.iterator(); iterator.hasNext(); i++) {
            t[i] = iterator.next();
        }
        return t;
    }

    /**
     * 将一个栈转换为数组形式，该方法返回的数组开始位置为栈底数据，结束位置为栈顶数据。
     *
     * @param <E>       其中的元素。
     * @param stack     要转换的栈。
     * @param typeClass 要转换到的数组的元素类型。
     * @return stack 参数集合所对应的数组形式。
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] toBaseArray(Stack<E> stack, Class<E> typeClass) {
        E[] t = (E[]) Array.newInstance(typeClass, stack.height());
        int i = stack.height() - 1;
        for (Iterator<E> iterator = stack.iterator(); iterator.hasNext(); ) {
            t[i--] = iterator.next();
        }
        return t;
    }

    /**
     * 将一个栈转换为数组形式，该方法返回的数组为该 {@link Stack} 的迭代器相同规则的数组。
     *
     * @param <E>       其中的元素。
     * @param stack     要转换的栈。
     * @param typeClass 要转换到的数组的元素类型。
     * @return stack 参数集合所对应的数组形式。
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] toArray(Stack<E> stack, Class<E> typeClass) {
        E[] t = (E[]) Array.newInstance(typeClass, stack.height());
        int i = 0;
        for (Iterator<E> iterator = stack.iterator(); iterator.hasNext(); ) {
            t[i++] = iterator.next();
        }
        return t;
    }

    /**
     * 将一个迭代器转换为数组形式。
     *
     * @param <E>       其中的元素。
     * @param iterator  要转换的迭代器。
     * @param typeClass 要转换到的数组的元素类型。
     * @return iterator 参数集合所对应的数组形式。
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] toArray(Iterator<E> iterator, Class<E> typeClass) {
        if (iterator instanceof ArrayIterator<?>) {
            return ((ArrayIterator<E>) iterator).getArray();
        }
        E[] temp = (E[]) Array.newInstance(typeClass, 10);
        int i = 0;
        while (iterator.hasNext()) {
            if (i == temp.length) {
                E[] newTemp = (E[]) Array.newInstance(typeClass, temp.length * 2);
                System.arraycopy(temp, 0, newTemp, 0, temp.length);
                temp = newTemp;
                newTemp = null; // 帮助垃圾回收
            }
            temp[i] = iterator.next();
            i++;
        }
        E[] t = (E[]) Array.newInstance(typeClass, i);
        System.arraycopy(temp, 0, t, 0, i);
        return t;
    }

    /**
     * 返回一个指定 enumeration 上的 {@link Iterator}。该迭代器没有指向的集合，所以对该迭代器进行修改操作会抛出
     * UnsupportedOperationException。
     *
     * @param enumeration 将为其返回一个 enumeration 的 {@link Iterator}。
     * @return 指定 enumeration 的 {@link Iterator}。
     */
    public static <E> Iterator<E> iterator(final Enumeration<E> enumeration) {
        return new Iterator<E>() {

            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override
            public E next() {
                return enumeration.nextElement();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("该迭代器没有指向的集合，所以移除元素为不支持的操作。");
            }

        };
    }

    /**
     * 返回一个指定 iterator 上的 {@link Enumeration}。
     *
     * @param iterator 将为其返回一个 {@link Enumeration} 的 iterator。
     * @return 指定 iterator 的 {@link Enumeration}。
     */
    public static <E> Enumeration<E> enumeration(final Iterator<E> iterator) {

        return new Enumeration<E>() {

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public E nextElement() {
                return iterator.next();
            }
        };
    }

    /**
     * 将指定元素添加到指定 collection 中。如果为数组或者{@link Collection}或者{@link Iterator} 的子类则分别添加数组的每个元素，否则直接添加。
     *
     * @param collection value 所要插入的 collection。
     * @param value      插入 collection 的元素。
     * @throws ClassCastException 如果 value 的类型和 collection 持有类型不兼容。
     */
    @SuppressWarnings("unchecked")
    public static <T> void add(Collection<T> collection, Object value) {
        if (value == null) {
            collection.add(null);
        } else {
            // 数组
            if (value.getClass().isArray()) {
                if (value instanceof Object[]) {
                    for (Object o : (Object[]) value) {
                        collection.add((T) o);
                    }
                } else {
                    int length = Array.getLength(value);
                    for (int i = 0; i < length; i++) {
                        collection.add((T) Array.get(value, i));
                    }
                }
            } else { // 非数组
                if (value instanceof Collection<?> || value instanceof Stack<?>) {
                    for (Object object : (Iterable<T>) value) {
                        collection.add((T) object);
                    }
                } else if (value instanceof Iterator<?>) {
                    // 迭代器
                    for (Iterator<T> iterator = (Iterator<T>) value; iterator.hasNext(); ) {
                        collection.add(iterator.next());
                    }
                } else {
                    collection.add((T) value);
                }
            }
        }
    }

    /**
     * 将所有指定元素添加到指定 stack 中。如果为数组或者{@link Collection}的子类则分别添加数组的每个元素，否则直接添加。
     *
     * @param stack value 所要插入的 Stack。
     * @param value 推入 stack 的元素。
     * @throws ClassCastException 如果 value 的类型和 stack 持有类型不兼容。
     */
    @SuppressWarnings("unchecked")
    public static <T> void push(Stack<T> stack, Object value) {
        if (value == null) {
            stack.push(null);
        } else {
            // 数组
            if (value.getClass().isArray()) {
                if (value instanceof Object[]) {
                    for (Object o : (Object[]) value) {
                        stack.push((T) o);
                    }
                } else {
                    int length = Array.getLength(value);
                    for (int i = 0; i < length; i++) {
                        stack.push((T) Array.get(value, i));
                    }
                }
            } else { // 非数组
                if (value instanceof Collection || value instanceof Stack) {
                    for (Object object : (Iterable<? super T>) value) {
                        stack.push((T) object);
                    }
                } else {
                    stack.push((T) value);
                }
            }
        }
    }

    /**
     * 返回由指定栈支持的同步（线程安全的）栈。为了保证按顺序访问，必须通过返回的栈完成对底层栈的所有访问。 在返回的栈上进行迭代时，强制用户手工在返回的栈上进行同步：
     * <p>
     *
     * <pre>
     * Stack stack = CollectionUtils.synchronizedStack(new LinkedStack());
     *  ...
     * synchronized(stack) {
     *  Iterator i = stack.iterator();
     * while (i.hasNext())
     *  foo(i.next());
     * }
     * </pre>
     * <p>
     * 不遵从此建议将导致无法确定的行为。 如果指定栈是可序列化的，则返回的栈也将是可序列化的。
     *
     * @param stack 被“包装”在同步栈中的栈。
     * @return 指定栈的同步视图。
     */
    public static <T> Stack<T> synchronizedStack(Stack<T> stack) {
        if (stack == null) {
            throw new IllegalArgumentException("The input argument is null!");
        }
        return new SynchronizedStack<T>(stack);
    }

    /**
     * 返回空的 Stack（不可变的）。此 Stack 是可序列化的。
     *
     * @return 空的 Stack（不可变的）。
     * @see #EMPTY_STACK
     */
    @SuppressWarnings("unchecked")
    public static final <T> Stack<T> emptyStack() {
        return (Stack<T>) EMPTY_STACK;
    }

    /**
     * 返回空的 Queue（不可变的）。此 Queue 是可序列化的。
     *
     * @return 空的 Queue（不可变的）。
     * @see #EMPTY_QUEUE
     */
    @SuppressWarnings("unchecked")
    public static final <T> Queue<T> emptyQueue() {
        return (Queue<T>) EMPTY_QUEUE;
    }

    /**
     * 返回由指定映射支持的同步（线程安全的）{@link MultiValueMap} 。 为了保证按顺序访问，必须通过返回的映射完成对底层映射的<strong>所有</strong>访问。
     * <p>
     * 在返回的映射或其任意 collection 视图上进行迭代时，强制用户手工在返回的映射上进行同步：
     * <p>
     *
     * <pre>
     * MultiValueMap m = CollectionUtils.synchronizedMultiValueMap(new LinkedMultiValueMap());
     * ...
     * Set s = m.keySet();
     * ...
     * synchronized(m) {
     * 		Iterator i = s.iterator();
     * 		while (i.hasNext()) {
     * 			 foo(i.next());
     *        }
     * }
     * </pre>
     * <p>
     * 不遵从此建议将导致无法确定的行为。
     * <p>
     * 如果指定映射是可序列化的，则返回的映射也将是可序列化的。
     *
     * @param multiValueMap 被“包装”在同步映射中的映射。
     * @return 指定映射的同步视图。
     */
    public static <K, V> MultiValueMap<K, V> synchronizedMultiValueMap(
            MultiValueMap<K, V> multiValueMap) {
        if (multiValueMap == null) {
            throw new IllegalArgumentException("The input argument is null!");
        }
        return new SynchronizedMultiValueMap<K, V>(multiValueMap);
    }

    /**
     * 返回空的 MultiValueMap（不可变的）。此 MultiValueMap 是可序列化的。
     *
     * @return 空的 MultiValueMap（不可变的）。
     * @see #EMPTY_MULTI_VALUE_MAP
     */
    @SuppressWarnings("unchecked")
    public static <K, V> MultiValueMap<K, V> emptyMultiValueMap() {
        return (MultiValueMap<K, V>) EMPTY_MULTI_VALUE_MAP;
    }

    /**
     * 返回空的 Iterator（不可变的）。此 Iterator 是可序列化的。
     *
     * @return 空的 Iterator（不可变的）。
     * @see #EMPTY_ITERATOR
     */
    @SuppressWarnings("unchecked")
    public static final <T> Iterator<T> emptyIterator() {
        return (Iterator<T>) EMPTY_ITERATOR;
    }

    // static class UnmodifiableMultiValueMap<K, V> implements MultiValueMap<K,
    // V> {
    //
    // final MultiValueMap<K, V> multiValueMap;
    //
    // UnmodifiableMultiValueMap(MultiValueMap<K, V> multiValueMap) {
    // this.multiValueMap = multiValueMap;
    // }
    //
    // }

    /**
     * 返回空的 Enumeration（不可变的）。此 Enumeration 是可序列化的。
     *
     * @return 空的 Enumeration（不可变的）。
     * @see #EMPTY_ENUMERATIONR
     */
    @SuppressWarnings("unchecked")
    public static final <T> Enumeration<T> emptyEnumeration() {
        return (Enumeration<T>) EMPTY_ENUMERATIONR;
    }

    /**
     * 返回指定映射的不可修改视图。此方法允许使用模块，为用户提供对内部映射的“只读”访问。在返回的映射上执行的查询操作将“读完”指定的映射。 试图修改返回的映射，不管是直接修改还是通过其
     * collection 视图进行修改，都将导致抛出 UnsupportedOperationException。
     * <p>
     * 如果指定映射是可序列化的，则返回的映射也将是可序列化的。
     *
     * @param multiValueMap 将为其返回一个不可修改视图的映射。
     * @return 指定映射的不可修改视图。
     */
    public static <K, V> MultiValueMap<K, V> unmodifiableMultiValueMap(
            MultiValueMap<? extends K, ? extends V> multiValueMap) {
        if (multiValueMap == null) {
            throw new IllegalArgumentException("The input argument is null!");
        }
        return new UnmodifiableMultiValueMap<K, V>(multiValueMap);
    }

    /**
     * 返回 Stack 的 Collection 形式。
     * <p>
     * 该 Collection 底层仍然是个 Stack ，所以任何破坏 Stack 的方法如 remove,removeAll,retainAll 都会抛出
     * UnsupportedOperationException 。
     * <p>
     * 调用该 Collection add 方法相当于调用 {@link Stack#push(Object)} 方法。iterator 使用底层 Stack 的迭代器。
     *
     * @param stack 要转化为 Collection 的 Stack。
     * @return Stack 的 Collection 形式。
     */
    public static <T> Collection<T> collection(final Stack<T> stack) {
        return new AbstractCollection<T>() {

            @Override
            public boolean add(T e) {
                return stack.push(e) != null;
            }

            @Override
            public void clear() {
                stack.clear();
            }

            @Override
            public Iterator<T> iterator() {
                return stack.iterator();
            }

            @Override
            public boolean remove(Object o) {
                unRemove();
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                unRemove();
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                unRemove();
                return false;
            }

            @Override
            public int size() {
                return stack.height();
            }

            @Override
            public String toString() {
                return stack.toString();
            }

            private void unRemove() {
                throw new UnsupportedOperationException("底层栈不支持可能移除非栈顶元素的操作。");
            }

            @Override
            public boolean equals(Object obj) {
                return stack.equals(obj);
            }

            @Override
            public int hashCode() {
                return stack.hashCode();
            }

            @Override
            public boolean isEmpty() {
                return stack.isEmpty();
            }
        };
    }

    /**
     * 判断Collection是否为空。
     *
     * @param collection 要判断的Collection。
     * @return 如果Collection为 null 或者 {@link Collection#isEmpty()} 返回true时，则返回 true。
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * 判断Stack是否为空。
     *
     * @param stack 要判断的Stack。
     * @return 如果Stack为 null 或者 {@link Stack#isEmpty()} 返回true时，则返回 true。
     */
    public static boolean isEmpty(Stack<?> stack) {
        return (stack == null || stack.isEmpty());
    }

    /**
     * 判断Map是否为空。
     *
     * @param map 要判断的Map。
     * @return 如果Map为 null 或者 {@link Map#isEmpty()} 返回true时，则返回 true。
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 判断Collection是否不为空。
     *
     * @param collection 要判断的Collection。
     * @return 如果Collection不为 null 且 {@link Collection#isEmpty()} 返回false时，则返回 true。
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return (collection != null && !collection.isEmpty());
    }

    /**
     * 判断Stack是否不为空。
     *
     * @param stack 要判断的Stack。
     * @return 如果Stack不为 null 且 {@link Stack#isEmpty()} 返回false时，则返回 true。
     */
    public static boolean isNotEmpty(Stack<?> stack) {
        return (stack != null && !stack.isEmpty());
    }

    /**
     * 判断Map是否不为空。
     *
     * @param map 要判断的Map。
     * @return 如果Map不为 null 且 {@link Map#isEmpty()} 返回false时，则返回 true。
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return (map != null && !map.isEmpty());
    }

    /**
     * 返回 compareList 在 list 中<strong>首次完整出现</strong>的索引。
     *
     * @param list        一个列表。
     * @param compareList 一个列表。
     * @return 如果 list 、 compareList 至少一个为空或者 compareList 在 list 中并没完整出现时返回 -1 ，否则返回 compareList 在
     * list 中的<strong>首次完整出现</strong>的索引。
     */
    public static int indexOf(List<?> list, List<?> compareList) {
        return indexOf(list, compareList, 0);
    }

    /**
     * 返回 compareList 在 list 中从 index 指定的索引位<strong>首次完整出现</strong>的索引。
     *
     * @param list        一个列表。
     * @param compareList 一个列表。
     * @param index       开始搜索的索引。
     * @return 如果 list 、 compareList 至少一个为空或者 compareList 从 index 开始在 list 中并没完整出现时返回 -1 ，否则返回
     * compareList 在 list 中的<strong>首次完整出现</strong>的索引。
     * @throws IllegalArgumentException 如果 index 的值不在 0 至 list.size() - 1 之间。
     */
    public static int indexOf(List<?> list, List<?> compareList, int index) {
        if (!isComparable(list, compareList, index)) {
            return -1;
        }
        int compareListSize = compareList.size();
        boolean listIsRandomAccess = list instanceof RandomAccess;
        boolean compareListIsRandomAccess = compareList instanceof RandomAccess;
        if (listIsRandomAccess) {
            for (; index + compareListSize <= list.size(); index++) {
                if (compareListIsRandomAccess) {
                    for (int i = 0, j = index; ; i++) {
                        if (i == compareListSize) {
                            return index;
                        }
                        Object o1 = list.get(j + i);
                        Object o2 = compareList.get(i);
                        if (!ObjectUtils.isEquals(o1, o2)) {
                            break;
                        }
                    }
                } else {
                    int i = 0, j = index;
                    for (Iterator<?> compareListIterator = compareList.iterator(); ; i++) {
                        if (compareListIterator.hasNext()) {
                            Object o1 = list.get(j + i);
                            Object o2 = compareListIterator.next();
                            if (!ObjectUtils.isEquals(o1, o2)) {
                                break;
                            }
                        } else {
                            return index;
                        }
                    }
                }
            }
        } else {
            for (; index + compareListSize <= list.size(); index++) {
                Iterator<?> listIterator = list.listIterator(index);
                if (compareListIsRandomAccess) {
                    for (int i = 0; ; i++) {
                        if (i == compareListSize) {
                            return index;
                        }
                        Object o1 = listIterator.next();
                        Object o2 = compareList.get(i);
                        if (!ObjectUtils.isEquals(o1, o2)) {
                            break;
                        }
                    }
                } else {
                    for (Iterator<?> compareListIterator = compareList.iterator(); ; ) {
                        if (compareListIterator.hasNext()) {
                            Object o1 = listIterator.next();
                            Object o2 = compareListIterator.next();
                            if (!ObjectUtils.isEquals(o1, o2)) {
                                break;
                            }
                        } else {
                            return index;
                        }
                    }
                }
            }
        }
        return -1;
    }

    private static boolean isComparable(List<?> list, List<?> compareList, int index) {
        if (isEmpty(list) || isEmpty(compareList)) {
            return false;
        }
        Assert.isTrue(!(index < 0 || index >= list.size()), "{index} 的值必须从0 至 list.size() - 1 之间。");
        if (list.size() < compareList.size()) {
            return false;
        }
        return true;
    }

    /**
     * 返回 compareList 在 list 中<strong>最后一次完整出现</strong>的索引。
     *
     * @param list        一个列表。
     * @param compareList 一个列表。
     * @return 如果 list 、 compareList 至少一个为空或者 compareList 在 list 中并没完整出现时返回 -1 ，否则返回 compareList 在
     * list 中的<strong>最后一次完整出现</strong>的索引。
     */
    public static int lastIndexOf(List<?> list, List<?> compareList) {
        return lastIndexOf(list, compareList, 0);
    }

    /**
     * 返回 compareList 在 list 中从 index 指定的索引位<strong>最后一次完整出现</strong>的索引。
     *
     * @param list        一个列表。
     * @param compareList 一个列表。
     * @param index       开始搜索的索引。
     * @return 如果 list 、 compareList 至少一个为空或者 compareList 从 index 开始在 list 中并没完整出现时返回 -1 ，否则返回
     * compareList 在 list 中的<strong>最后一次完整出现</strong>的索引。
     * @throws IllegalArgumentException 如果 index 的值不在 0 至 list.size() - 1 之间。
     */
    public static int lastIndexOf(List<?> list, List<?> compareList, int index) {
        if (!isComparable(list, compareList, index)) {
            return -1;
        }
        int compareListSize = compareList.size();
        boolean listIsRandomAccess = list instanceof RandomAccess;
        boolean compareListIsRandomAccess = compareList instanceof RandomAccess;
        if (listIsRandomAccess) {
            for (int k = list.size() - compareListSize + 1; k > 0; k--) {
                if (k < index) {
                    return -1;
                }
                if (compareListIsRandomAccess) {
                    for (int i = 0, j = k; ; i++) {
                        if (i == compareListSize) {
                            return k;
                        }
                        Object o1 = list.get(j + i);
                        Object o2 = compareList.get(i);
                        if (!ObjectUtils.isEquals(o1, o2)) {
                            break;
                        }
                    }
                } else {
                    int i = 0, j = k;
                    for (Iterator<?> compareListIterator = compareList.iterator(); ; i++) {
                        if (compareListIterator.hasNext()) {
                            Object o1 = list.get(j + i);
                            Object o2 = compareListIterator.next();
                            if (!ObjectUtils.isEquals(o1, o2)) {
                                break;
                            }
                        } else {
                            return k;
                        }
                    }
                }
            }
        } else {
            for (int k = list.size() - compareListSize + 1; k > 0; k--) {
                if (k < index) {
                    return -1;
                }
                Iterator<?> listIterator = list.listIterator(k);
                if (compareListIsRandomAccess) {
                    for (int i = 0; ; i++) {
                        if (i == compareListSize) {
                            return k;
                        }
                        Object o1 = listIterator.next();
                        Object o2 = compareList.get(i);
                        if (!ObjectUtils.isEquals(o1, o2)) {
                            break;
                        }
                    }
                } else {
                    for (Iterator<?> compareListIterator = compareList.iterator(); ; ) {
                        if (compareListIterator.hasNext()) {
                            Object o1 = listIterator.next();
                            Object o2 = compareListIterator.next();
                            if (!ObjectUtils.isEquals(o1, o2)) {
                                break;
                            }
                        } else {
                            return k;
                        }
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 将 collection 转换为 字符串数组。
     *
     * @param collection 需要转换的 collection 。
     * @return collection 对应的字符串数组。
     * @see StringUtils#toString(Object)
     */
    public static String[] toStringArray(Collection<?> collection) {
        if (collection == null) {
            return null;
        }
        String[] stringArray = new String[collection.size()];
        int i = 0;
        for (Object o : collection) {
            stringArray[i] = StringUtils.toString(o);
            i++;
        }
        return stringArray;
    }

    /**
     * 对 collection 进行过滤，新建一个 {@link ArrayList} 对结果进行存放并返回，过滤保留的元素为 decider 判定为 true 的元素，如果 decider
     * 为 null ，则返回所有元素组成的一个新的 Collection 。
     *
     * @param collection 需要过滤的 Collection 。
     * @param decider    过滤条件判定器。
     * @return 过滤后的结果。
     */
    public static <E> Collection<E> filter(Collection<? extends E> collection, Decider<E> decider) {
        if (collection == null) {
            return null;
        }
        if (decider == null) {
            return new ArrayList<E>(collection);
        }
        Collection<E> result = new ArrayList<E>();
        filter(collection, result, decider);
        return result;
    }

    /**
     * 对 set 进行过滤，新建一个 {@link HashSet} 对结果进行存放并返回，过滤保留的元素为 decider 判定为 true 的元素，如果 decider 为 null
     * ，则返回所有元素组成的一个新的 Set 。
     *
     * @param set     需要过滤的 Set 。
     * @param decider 过滤条件判定器。
     * @return 过滤后的结果。
     */
    public static <E> Set<E> filter(Set<? extends E> set, Decider<E> decider) {
        if (set == null) {
            return null;
        }
        if (decider == null) {
            return new HashSet<E>(set);
        }
        Set<E> result = new HashSet<E>();
        filter(set, result, decider);
        return result;
    }

    /**
     * 对 list 进行过滤，新建一个 {@link ArrayList} 对结果进行存放并返回，过滤保留的元素为 decider 判定为 true 的元素，如果 decider 为 null
     * ，则返回所有元素组成的一个新的 List 。
     *
     * @param list    需要过滤的 List 。
     * @param decider 过滤条件判定器。
     * @return 过滤后的结果。
     */
    public static <E> List<E> filter(List<? extends E> list, Decider<E> decider) {
        return (List<E>) filter((Collection<? extends E>) list, decider);
    }

    /**
     * 对 queue 进行过滤，新建一个 {@link LinkedList} 对结果进行存放并返回，过滤保留的元素为 decider 判定为 true 的元素，如果 decider 为
     * null ，则返回所有元素组成的一个新的 Queue 。
     *
     * @param queue   需要过滤的 Queue 。
     * @param decider 过滤条件判定器。
     * @return 过滤后的结果。
     */
    public static <E> Queue<E> filter(Queue<? extends E> queue, Decider<E> decider) {
        if (queue == null) {
            return null;
        }
        if (decider == null) {
            return new LinkedList<E>(queue);
        }
        Queue<E> result = new LinkedList<E>();
        filter(queue, result, decider);
        return result;
    }

    /**
     * 对 collection 进行过滤，用 result 对结果进行存放，过滤保留的元素为 decider 判定为 true 的元素。
     *
     * @param collection 需要过滤的 Collection 。
     * @param result     存放过滤后的结果的 Collection 。
     * @param decider    过滤条件判定器。
     * @throws IllegalArgumentException 如果 result 为 null 。
     */
    public static <E> void filter(Collection<? extends E> collection, Collection<E> result,
                                  Decider<E> decider) {
        if (collection == null || decider == null) {
            return;
        }
        for (E e : collection) {
            if (decider.decide(e)) {
                result.add(e);
            }
        }
    }

    /**
     * 对 map 进行过滤，新建一个 {@link HashMap} 对结果进行存放并返回，过滤保留的元素为 decider 判定为 true 的元素，如果 decider 为 null
     * ，则返回所有元素组成的一个新的 Map 。
     *
     * @param map     需要过滤的 Map 。
     * @param decider 过滤条件判定器。
     * @return 过滤后的结果。
     */
    public static <K, V> Map<K, V> filter(Map<? extends K, ? extends V> map,
                                          Decider<Map.Entry<K, V>> decider) {
        if (map == null) {
            return null;
        }
        if (decider == null) {
            return new HashMap<K, V>(map);
        }
        Map<K, V> result = new HashMap<K, V>();
        filter(map, result, decider);
        return result;
    }

    /**
     * 对 map 进行过滤，用 result 对结果进行存放，过滤保留的元素为 decider 判定为 true 的元素。
     *
     * @param map     需要过滤的 Map 。
     * @param result  存放过滤后的结果的 Map 。
     * @param decider 过滤条件判定器。
     * @throws IllegalArgumentException 如果 result 为 null 。
     */
    @SuppressWarnings("unchecked")
    public static <K, V> void filter(Map<? extends K, ? extends V> map, Map<K, V> result,
                                     Decider<Map.Entry<K, V>> decider) {
        if (map == null || decider == null) {
            return;
        }
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            if (decider.decide((Map.Entry<K, V>) entry)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 如果 paramMap 中的键已经不存在于 map 中，则将它 put 到 map 中。
     * <p>
     * 如果 map 或者 paramMap 为 null ，则不会有任何变化。
     *
     * @param map      一个 Map 。
     * @param paramMap 一个 Map 。
     */
    public static <K, V> void putAllIfAbsent(Map<K, V> map,
                                             Map<? extends K, ? extends V> paramMap) {
        if (map == null || paramMap == null) {
            return;
        }
        for (Map.Entry<? extends K, ? extends V> entry : paramMap.entrySet()) {
            if (!map.containsKey(entry.getKey())) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 计算 size 指定的大小的使用默认加载因子（0.75f）的 {@link HashMap} 的最小初始容量。
     *
     * @param size 指定的大小。
     * @return size 指定的大小的使用默认加载因子（0.75f）的 {@link HashMap} 的最小初始容量，如果该值小于 16 ，则为 16。
     */
    public static int calculateHashMapMinInitialCapacity(int size) {
        return calculateHashMapMinInitialCapacity(size, HASH_MAP_DEFAULT_LOAD_FACTOR,
                HASH_MAP_DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * 计算 size 指定的大小的使用 loadFactor 指定的加载因子的 {@link HashMap} 的最小初始容量。
     *
     * @param size           指定的大小。
     * @param loadFactor     指定的加载因子。
     * @param initlalCapcity 容器默认初始化大小。
     * @return size 指定的大小的使用 loadFactor 指定的加载因子的 {@link HashMap} 的最小初始容量，如果该值小于 initlalCapcity ，则为
     * initlalCapcity。
     */
    public static int calculateHashMapMinInitialCapacity(int size, float loadFactor,
                                                         int initlalCapcity) {
        return Math.max((int) (size / loadFactor) + 1, initlalCapcity);
    }

    /**
     * 深度克隆一个Collection。
     * <p>
     * 注意：该方法是用反射创建 Collection 的实现类，在创建 Collection 的实现类时所使用的为默认（无参）构造方法，如果传递的 Collection
     * 的实现类没有默认构造方法，那么将返回 {@link ArrayList} 的实例。
     *
     * @param collection 需要深度克隆的Collection。
     * @return Collection的深度克隆副本，如果 collection 为 null 则返回 null 。
     */
    @SuppressWarnings("unchecked")
    public static <E extends DeepCloneable> Collection<E> deepClone(Collection<E> collection) {
        if (collection == null) {
            return null;
        }
        Collection<E> c;
        try {
            try {
                c = ReflectionUtils.createObject(collection.getClass(),
                        new Class<?>[]{Integer.TYPE}, collection.size());
            } catch (NoSuchMethodException e) {
                c = ReflectionUtils.createObject(collection.getClass());
            }
        } catch (NoSuchMethodException e) {
            c = new ArrayList<E>(collection.size());
        }
        for (E e : collection) {
            c.add((E) e.deepClone());
        }
        return c;
    }

    /**
     * 对集合进行分页
     * 如果集合非有序集合(如HashSet)，集合更改后可能导致顺序变化，分页的结果可能不准备
     *
     * @param collection 分页的集合
     * @param <E>
     * @return 分页 {@link Page} 对象
     * @throws ConcurrentModificationException
     */
    public static <E> Page<E> page(Collection<E> collection) {
        return page(collection, 1);
    }

    /**
     * 对集合进行分页
     * 如果集合非有序集合(如HashSet)，集合更改后可能导致顺序变化，分页的结果可能不准备
     *
     * @param collection 分页的集合
     * @param pageNumber 起始页
     * @param <E>
     * @return 分页 {@link Page} 对象
     * @throws ConcurrentModificationException
     */
    public static <E> Page<E> page(Collection<E> collection, int pageNumber) {
        return page(collection, pageNumber, Pageable.DEFAULT_PAGE_SIZE);
    }

    /**
     * 对集合进行分页
     * 如果集合非有序集合(如HashSet)，集合更改后可能导致顺序变化，分页的结果可能不准备
     *
     * @param collection 分页的集合
     * @param pageNumber 起始页
     * @param pageSize   分页大小
     * @param <E>
     * @return 分页 {@link Page} 对象
     * @throws ConcurrentModificationException
     */
    public static <E> Page<E> page(Collection<E> collection, int pageNumber, int pageSize) {

        Page page = new Page<E>(pageNumber, pageSize);
        if (isNotEmpty(collection)) {
            page.setTotalCount(collection.size());

            if (collection.size() < page.getPageSize()) {
                page.setCurrentPageResult(Lists.newArrayList(collection));
            } else {
                int startRow = page.getPageNumber() > 0 ? (page.getPageNumber() - 1) * page.getPageSize() : 0;
                if (startRow >= collection.size()) {
                    //超出边界
                } else {
                    int endRow = Math.min(collection.size(),
                            startRow + page.getPageSize() * (page.getPageNumber() > 0 ? 1 : 0));
                    if (collection instanceof ArrayList) {
                        ArrayList list = (ArrayList) collection;
                        for (int i = startRow; i < endRow; i++) {
                            page.getCurrentPageResult().add(list.get(i));
                        }
                    } else {
                        Iterator<E> iterator = collection.iterator();
                        int i = 0;
                        while (i++ < startRow) {
                            iterator.next();
                        }
                        do {
                            page.getCurrentPageResult().add(iterator.next());
                        }
                        while (i++ < endRow);
                    }
                }
            }
        }
        return page;
    }

    /**
     * 同步包装栈。
     */
    static class SynchronizedStack<E> implements Stack<E>, Serializable {
        /**
         * 版本号
         */
        private static final long serialVersionUID = 1739673574513790948L;

        final Stack<E> stack;

        final Object mutex;

        SynchronizedStack(Stack<E> stack) {
            super();
            if (stack == null) {
                throw new IllegalArgumentException("The input argument is null!");
            }
            this.stack = stack;
            this.mutex = this;
        }

        SynchronizedStack(Stack<E> stack, Object mutex) {
            super();
            if (stack == null) {
                throw new IllegalArgumentException("The input argument is null!");
            }
            this.stack = stack;
            this.mutex = mutex;
        }

        @Override
        public void clear() {
            synchronized (this.mutex) {
                this.stack.clear();
            }
        }

        @Override
        public boolean isEmpty() {
            synchronized (this.mutex) {
                return this.stack.isEmpty();
            }
        }

        @Override
        public boolean equals(Object obj) {
            synchronized (this.mutex) {
                return this.stack.equals(obj);
            }
        }

        @Override
        public int hashCode() {
            synchronized (this.mutex) {
                return this.stack.hashCode();
            }
        }

        @Override
        public int height() {
            synchronized (this.mutex) {
                return this.stack.height();
            }
        }

        @Override
        public Iterator<E> iterator() {
            return this.stack.iterator();
        }

        @Override
        public E peek() {
            synchronized (this.mutex) {
                return this.stack.peek();
            }
        }

        @Override
        public E pop() {
            synchronized (this.mutex) {
                return this.stack.pop();
            }
        }

        @Override
        public E push(E item) {
            synchronized (this.mutex) {
                return this.stack.push(item);
            }
        }

        @Override
        public int search(E item) {
            synchronized (this.mutex) {
                return this.stack.search(item);
            }
        }

        @Override
        public boolean contains(E item) {
            synchronized (this.mutex) {
                return this.stack.contains(item);
            }
        }

        @Override
        public Object[] toArray() {
            synchronized (this.mutex) {
                return this.stack.toArray();
            }
        }

        @Override
        public E[] toArray(E[] array) {
            synchronized (this.mutex) {
                return this.stack.toArray(array);
            }
        }

        @Override
        public String toString() {
            synchronized (this.mutex) {
                return this.stack.toString();
            }
        }

        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (this.mutex) {
                s.defaultWriteObject();
            }
        }
    }

    /**
     * 空的可序列化的栈。
     */
    private static class EmptyStack extends AbstractStack<Object>
            implements Stack<Object>, Serializable {

        /**
         * 版本号
         */
        private static final long serialVersionUID = -5718284556257947212L;

        @Override
        public Iterator<Object> iterator() {
            return emptyIterator();
        }

        @Override
        public int search(Object item) {
            return -1;
        }

        @Override
        public int height() {
            return 0;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object push(Object item) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object peek() {
            throw new EmptyStackException();
        }

        @Override
        public Object pop() {
            throw new EmptyStackException();
        }

    }

    /**
     * 空的可序列化的队列。
     */
    private static class EmptyQueue extends AbstractQueue<Object>
            implements Serializable, Queue<Object> {

        /**
         * 版本号
         */
        private static final long serialVersionUID = 7721902712051011220L;

        @Override
        public boolean offer(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object peek() {
            return null;
        }

        @Override
        public Object poll() {
            return null;
        }

        @Override
        public Iterator<Object> iterator() {
            return emptyIterator();
        }

        @Override
        public int size() {
            return 0;
        }

    }

    /**
     * 同步包装 MultiValueMap 。
     */
    static class SynchronizedMultiValueMap<K, V> implements MultiValueMap<K, V> {

        final MultiValueMap<K, V> multiValueMap;

        final Object mutex;

        private transient Set<K> keySet = null;

        private transient Set<Map.Entry<K, List<V>>> entrySet;

        private transient Collection<List<V>> values;

        private transient Set<Map.Entry<K, V>> singleValueEntrySet;

        private transient Collection<V> singleValues;

        SynchronizedMultiValueMap(MultiValueMap<K, V> multiValueMap) {
            if (multiValueMap == null) {
                throw new IllegalArgumentException("The input argument is null!");
            }
            this.multiValueMap = multiValueMap;
            this.mutex = this;
        }

        SynchronizedMultiValueMap(MultiValueMap<K, V> multiValueMap, Object mutex) {
            if (multiValueMap == null) {
                throw new IllegalArgumentException("The input argument is null!");
            }
            this.multiValueMap = multiValueMap;
            this.mutex = mutex;
        }

        @Override
        public void add(K key, V value) {
            synchronized (this.mutex) {
                this.multiValueMap.add(key, value);
            }
        }

        @Override
        public void clear() {
            synchronized (this.mutex) {
                this.multiValueMap.clear();
            }
        }

        @Override
        public boolean containsKey(Object key) {
            synchronized (this.mutex) {
                return this.multiValueMap.containsKey(key);
            }
        }

        @Override
        public boolean containsValue(Object value) {
            synchronized (this.mutex) {
                return this.multiValueMap.containsValue(value);
            }
        }

        @Override
        public Set<Map.Entry<K, List<V>>> entrySet() {
            synchronized (this.mutex) {
                if (this.entrySet == null) {
                    this.entrySet = new SynchronizedSet<Map.Entry<K, List<V>>>(
                            this.multiValueMap.entrySet(), this.mutex);
                }
                return this.entrySet;
            }
        }

        @Override
        public boolean equals(Object o) {
            synchronized (this.mutex) {
                return this.multiValueMap.equals(o);
            }
        }

        @Override
        public List<V> get(Object key) {
            synchronized (this.mutex) {
                return this.multiValueMap.get(key);
            }
        }

        @Override
        public V getFirst(K key) {
            synchronized (this.mutex) {
                return this.multiValueMap.getFirst(key);
            }
        }

        @Override
        public int hashCode() {
            synchronized (this.mutex) {
                return this.multiValueMap.hashCode();
            }
        }

        @Override
        public boolean isEmpty() {
            synchronized (this.mutex) {
                return this.multiValueMap.isEmpty();
            }
        }

        @Override
        public Set<K> keySet() {
            synchronized (this.mutex) {
                if (this.keySet == null) {
                    this.keySet = new SynchronizedSet<K>(this.multiValueMap.keySet(), this.mutex);
                }
                return this.keySet;
            }
        }

        @Override
        public List<V> put(K key, List<V> value) {
            synchronized (this.mutex) {
                return this.multiValueMap.put(key, value);
            }
        }

        @Override
        public void putAll(Map<? extends K, ? extends List<V>> t) {
            synchronized (this.mutex) {
                this.multiValueMap.putAll(t);
            }
        }

        @Override
        public List<V> remove(Object key) {
            synchronized (this.mutex) {
                return this.multiValueMap.remove(key);
            }
        }

        @Override
        public void set(K key, V value) {
            synchronized (this.mutex) {
                this.multiValueMap.set(key, value);
            }
        }

        @Override
        public void setAll(Map<K, V> values) {
            synchronized (this.mutex) {
                this.multiValueMap.setAll(values);
            }
        }

        @Override
        public Set<Map.Entry<K, V>> singleValueEntrySet() {
            synchronized (this.mutex) {
                if (this.singleValueEntrySet == null) {
                    this.singleValueEntrySet = new SynchronizedSet<Map.Entry<K, V>>(
                            this.multiValueMap.singleValueEntrySet(), this.mutex);
                }
                return this.singleValueEntrySet;
            }
        }

        @Override
        public Collection<V> singleValues() {
            synchronized (this.mutex) {
                if (this.singleValues == null) {
                    this.singleValues = new SynchronizedCollection<V>(
                            this.multiValueMap.singleValues(), this.mutex);
                }
                return this.singleValues;
            }
        }

        @Override
        public int size() {
            synchronized (this.mutex) {
                return this.multiValueMap.size();
            }
        }

        @Override
        public Map<K, V> toSingleValueMap() {
            synchronized (this.mutex) {
                return this.multiValueMap.toSingleValueMap();
            }
        }

        @Override
        public Collection<List<V>> values() {
            synchronized (this.mutex) {
                if (this.values == null) {
                    this.values = new SynchronizedCollection<List<V>>(this.multiValueMap.values(),
                            this.mutex);
                }
                return this.values;
            }
        }

        @Override
        public String toString() {
            synchronized (this.mutex) {
                return this.multiValueMap.toString();
            }
        }

        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (this.mutex) {
                s.defaultWriteObject();
            }
        }

    }

    /**
     * 同步包装的 Collection。
     */
    static class SynchronizedCollection<E> implements Collection<E>, Serializable {

        /**
         * 版本号
         */
        private static final long serialVersionUID = -7540724106974451779L;

        final Collection<E> collection;

        final Object mutex;

        SynchronizedCollection(Collection<E> collection) {
            if (collection == null) {
                throw new IllegalArgumentException("The input argument is null!");
            }
            this.collection = collection;
            mutex = this;
        }

        SynchronizedCollection(Collection<E> collection, Object mutex) {
            if (collection == null) {
                throw new IllegalArgumentException("The input argument is null!");
            }
            this.collection = collection;
            this.mutex = mutex;
        }

        @Override
        public int size() {
            synchronized (this.mutex) {
                return this.collection.size();
            }
        }

        @Override
        public boolean isEmpty() {
            synchronized (this.mutex) {
                return this.collection.isEmpty();
            }
        }

        @Override
        public boolean contains(Object o) {
            synchronized (this.mutex) {
                return this.collection.contains(o);
            }
        }

        @Override
        public Object[] toArray() {
            synchronized (this.mutex) {
                return this.collection.toArray();
            }
        }

        @Override
        public <T> T[] toArray(T[] a) {
            synchronized (this.mutex) {
                return this.collection.toArray(a);
            }
        }

        @Override
        public Iterator<E> iterator() {
            return this.collection.iterator();
        }

        @Override
        public boolean add(E o) {
            synchronized (this.mutex) {
                return this.collection.add(o);
            }
        }

        @Override
        public boolean remove(Object o) {
            synchronized (this.mutex) {
                return this.collection.remove(o);
            }
        }

        @Override
        public boolean containsAll(Collection<?> coll) {
            synchronized (this.mutex) {
                return this.collection.containsAll(coll);
            }
        }

        @Override
        public boolean addAll(Collection<? extends E> coll) {
            synchronized (this.mutex) {
                return this.collection.addAll(coll);
            }
        }

        @Override
        public boolean removeAll(Collection<?> coll) {
            synchronized (this.mutex) {
                return this.collection.removeAll(coll);
            }
        }

        @Override
        public boolean retainAll(Collection<?> coll) {
            synchronized (this.mutex) {
                return this.collection.retainAll(coll);
            }
        }

        @Override
        public void clear() {
            synchronized (this.mutex) {
                this.collection.clear();
            }
        }

        @Override
        public String toString() {
            synchronized (this.mutex) {
                return this.collection.toString();
            }
        }

        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (this.mutex) {
                s.defaultWriteObject();
            }
        }
    }

    /**
     * 同步包装的 Set。
     */
    static class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {

        /**
         * 版本号
         */
        private static final long serialVersionUID = 6982504952424781802L;

        SynchronizedSet(Set<E> set) {
            super(set);
        }

        SynchronizedSet(Set<E> set, Object mutex) {
            super(set, mutex);
        }

        @Override
        public boolean equals(Object o) {
            synchronized (this.mutex) {
                return this.collection.equals(o);
            }
        }

        @Override
        public int hashCode() {
            synchronized (this.mutex) {
                return this.collection.hashCode();
            }
        }
    }

    /**
     * 空的可序列化的 MultiValueMap 。
     */
    private static class EmptyMultiValueMap extends AbstractMap<Object, List<Object>>
            implements MultiValueMap<Object, Object>, Serializable {

        /**
         * 版本号
         */
        private static final long serialVersionUID = -7774454121893327510L;

        @Override
        public void add(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getFirst(Object key) {
            return null;
        }

        @Override
        public void set(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAll(Map<Object, Object> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Entry<Object, Object>> singleValueEntrySet() {
            return Collections.emptySet();
        }

        @Override
        public Collection<Object> singleValues() {
            return Collections.emptyList();
        }

        @Override
        public Map<Object, Object> toSingleValueMap() {
            return Collections.emptyMap();
        }

        @Override
        public Set<Entry<Object, List<Object>>> entrySet() {
            return Collections.emptySet();
        }

    }

    /**
     * 空的可序列化的迭代器。
     */
    private static class EmptyIterator implements Iterator<Object>, Serializable {

        /**
         * 版本号
         */
        private static final long serialVersionUID = 6450079282524485545L;

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 空的可序列化的枚举器。
     */
    private static class EmptyEnumeration implements Enumeration<Object>, Serializable {

        /**
         * 版本号
         */
        private static final long serialVersionUID = 6450079282524485435L;

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public Object nextElement() {
            throw new NoSuchElementException();
        }
    }

    /**
     * 不可修改的 MultiValueMap 实现。
     */
    static class UnmodifiableMultiValueMap<K, V> implements MultiValueMap<K, V>, Serializable {

        /**
         * 版本号
         */
        private static final long serialVersionUID = 7629840602579792180L;

        final MultiValueMap<K, V> multiValueMap;

        Set<Map.Entry<K, List<V>>> entrySet;

        Collection<List<V>> values;

        Set<K> keySet;

        Map<K, V> singleValueMap;

        @SuppressWarnings("unchecked")
        UnmodifiableMultiValueMap(MultiValueMap<? extends K, ? extends V> multiValueMap) {
            this.multiValueMap = (MultiValueMap<K, V>) multiValueMap;
        }

        @Override
        public void add(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.multiValueMap.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.multiValueMap.containsValue(value);
        }

        @Override
        public Set<Map.Entry<K, List<V>>> entrySet() {
            if (this.entrySet == null) {
                this.entrySet = Collections.unmodifiableSet(this.multiValueMap.entrySet());
            }
            return this.entrySet;
        }

        @Override
        public boolean equals(Object o) {
            return this.multiValueMap.equals(o);
        }

        @Override
        public List<V> get(Object key) {
            return this.multiValueMap.get(key);
        }

        @Override
        public V getFirst(K key) {
            return this.multiValueMap.getFirst(key);
        }

        @Override
        public int hashCode() {
            return this.multiValueMap.hashCode();
        }

        @Override
        public boolean isEmpty() {
            return this.multiValueMap.isEmpty();
        }

        @Override
        public Set<K> keySet() {
            if (this.keySet == null) {
                this.keySet = Collections.unmodifiableSet(this.multiValueMap.keySet());
            }
            return this.keySet;
        }

        @Override
        public List<V> put(K key, List<V> value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends K, ? extends List<V>> t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<V> remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAll(Map<K, V> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Map.Entry<K, V>> singleValueEntrySet() {
            return toSingleValueMap().entrySet();
        }

        @Override
        public Collection<V> singleValues() {
            return toSingleValueMap().values();
        }

        @Override
        public int size() {
            return this.multiValueMap.size();
        }

        @Override
        public Map<K, V> toSingleValueMap() {
            if (this.singleValueMap == null) {
                this.singleValueMap = Collections
                        .unmodifiableMap(this.multiValueMap.toSingleValueMap());
            }
            return this.singleValueMap;
        }

        @Override
        public Collection<List<V>> values() {
            if (this.values == null) {
                this.values = Collections.unmodifiableCollection(this.multiValueMap.values());
            }
            return this.values;
        }

    }

}
