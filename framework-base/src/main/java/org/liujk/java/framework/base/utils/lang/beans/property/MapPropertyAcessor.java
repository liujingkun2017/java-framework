
package org.liujk.java.framework.base.utils.lang.beans.property;



import java.util.Map;

/**
 * 说明：
 * <p>
 * 属性所属 bean实例 为 {@link Map} 的属性访问器。
 *
 */
public class MapPropertyAcessor extends AbstractPropertyAccessor<Map<Object, Object>>
        implements PropertyAccessor {

    private final Class<?> type;

    /**
     * 使用访问的 map 的键和目标 map 对象构造一个 MapPropertyAcessor 。
     *
     * @param name   访问的 map 的键。
     * @param target 目标 map 对象实例。
     */
    public MapPropertyAcessor (Object name, Map<Object, Object> target) {
        this (name, target, null);
    }

    /**
     * 使用访问的 map 的键和目标 map 对象以及 map 值的类型构造一个 MapPropertyAcessor 。
     *
     * @param name   访问的 map 的键。
     * @param target 目标 map 对象实例。
     * @param type   map 值的类型。
     */
    public MapPropertyAcessor (Object name, Map<Object, Object> target, Class<?> type) {
        super (name, target);
        this.type = type == null ? Object.class : type;
    }

    @Override
    public Class<?> getType () {
        return this.type;
    }

    @Override
    public Object get () throws Exception {
        return this.target.get (this.name);
    }

    @Override
    public void set (Object value) throws Exception {
        this.target.put (this.name, value);
    }

}