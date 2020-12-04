
package org.liujk.java.framework.base.utils.lang.beans.property;



import java.util.Map;

/**
 * 说明：
 * <p>
 * 属性所属 bean {@link Map}的属性包装器。
 * <p>
 * {@link #createPropertyAccessor(Object)} 使用 {@link MapPropertyAcessor} 为实例对象。
 *
 */
public class MapPropertyWrapper extends AbstractPropertyWrapper {

    /**
     * 构造一个 MapPropertyWrapper 。
     *
     * @param key  键。
     * @param type 值的类型。
     */
    public MapPropertyWrapper (Object key, Class<?> type) {
        super (key, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PropertyAccessor createPropertyAccessor (Object bean) {
        return new MapPropertyAcessor (this.name, (Map<Object, Object>) bean, this.type);
    }

}