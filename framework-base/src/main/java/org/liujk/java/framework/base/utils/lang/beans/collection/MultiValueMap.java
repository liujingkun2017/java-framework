
package org.liujk.java.framework.base.utils.lang.beans.collection;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 说明：
 * <p>
 * 多值的 {@link Map} ，该 {@link Map} 的 value 为一个 {@link List} 用于存储多个值。
 *
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    /**
     * 返回给定键对应的第一个值。
     *
     * @param key 键。
     *
     * @return key 对应的第一个值，如果没有，则返回 null 。
     */
    V getFirst(K key);

    /**
     * 添加一个值到多值的 value 中。
     *
     * @param key   多值的键。
     * @param value 要添加的值。
     */
    void add(K key, V value);

    /**
     * 修改 key 对应的多值的 value 为只有指定值的 {@link List}。
     *
     * @param key   多值的键。
     * @param value 指定的值。
     */
    void set(K key, V value);

    /**
     * 修改 values 里的所有 key 对应的 value 为 values 的 关联关系。
     *
     * @param values 一个 {@code Map} 。
     *
     * @see #set(Object, Object)
     */
    void setAll(Map<K, V> values);

    /**
     * 返回所有 多值 的第一个值所组成的 {@link Map} 。
     *
     * @return 所有 多值 的第一个值所组成的 {@link Map} 。
     */
    Map<K, V> toSingleValueMap();

    /**
     * 返回此映射所包含的映射关系的 collection 视图。在返回的集合中，每个元素都是一个 {@link Entry}，该 Entry 中的
     * {@link Entry#getValue()} 为 多值 的 第一个值，{@link Entry#setValue(Object)} 是包装调用的
     * {@link #set(Object, Object)} 方法。该集合受映射的支持，所以映射的变化也反映在该集合中，反之亦然。该集合支持元素的移除，通过
     * Iterator.remove、Collection.remove、removeAll、retainAll 和 clear 操作，从该映射中移除相应的映射关系。它不支持 add 或
     * addAll 操作。
     *
     * @return 此映射所包含的映射关系的 collection 视图。
     */
    Set<Entry<K, V>> singleValueEntrySet();

    /**
     * 返回此映射所包含的 多值 的 第一个值 的 collection 视图。该集合受映射的支持，所以映射的变化也反映在该集合中，反之亦然。该集合支持元素的移除，通过
     * Iterator.remove、Collection.remove、removeAll、retainAll 和 clear 操作，从该映射中移除相应的映射关系。它不支持 add 或
     * addAll 操作。
     *
     * @return 此映射所包含的 多值 的 第一个值 的 collection 视图。
     */
    Collection<V> singleValues();
}
