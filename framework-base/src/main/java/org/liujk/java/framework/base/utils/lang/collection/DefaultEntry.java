
package org.liujk.java.framework.base.utils.lang.collection;



import java.util.Map;

/**
 * 说明：
 * <p>
 * 维护键和值的 Entry。可以使用 setValue 方法更改值。使用此类可简化构建自定义映射实现的过程。
 *
 */
public class DefaultEntry<K, V> implements Map.Entry<K, V>, java.io.Serializable {

    /**
     * 版本号
     */
    private static final long serialVersionUID = -60517854119124002L;

    private final K key;

    private V value;

    /**
     * 创建一个项，它表示从指定键到指定值的映射关系。
     *
     * @param key   此项所表示的键。
     * @param value 此项所表示的值。
     */
    public DefaultEntry (K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 创建一个项，它表示的映射关系与指定的项相同。
     *
     * @param entry 要复制的项。
     */
    public DefaultEntry (Map.Entry<? extends K, ? extends V> entry) {
        this.key = entry.getKey ();
        this.value = entry.getValue ();
    }

    @Override
    public K getKey () {
        return this.key;
    }

    @Override
    public V getValue () {
        return this.value;
    }

    @Override
    public V setValue (V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.key == null) ? 0 : this.key.hashCode ());
        result = prime * result + ((this.value == null) ? 0 : this.value.hashCode ());
        return result;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Map.Entry<?, ?>)) {
            return false;
        }
        Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
        if (this.key == null) {
            if (other.getKey () != null) {
                return false;
            }
        } else if (!this.key.equals (other.getKey ())) {
            return false;
        }
        if (this.value == null) {
            if (other.getValue () != null) {
                return false;
            }
        } else if (!this.value.equals (other.getValue ())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString () {
        return this.key + "=" + this.value;
    }
}
