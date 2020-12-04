
package org.liujk.java.framework.base.utils.lang.beans.property;



/**
 * 说明：
 * <p>
 * 属性所属 bean 为标准 JavaBean 的属性包装器。
 * <p>
 * {@link #createPropertyAccessor(Object)} 使用 {@link BeanPropertyAccessor} 为实例对象。
 *
 */
public class BeanPropertyWrapper extends AbstractPropertyWrapper {

    /**
     * 构造一个BeanPropertyWrapper 。
     *
     * @param name 被包装的属性在 bean 的 {@link Class} 中的属性名称。
     * @param type 被包装的属性的类型信息。
     */
    public BeanPropertyWrapper (Object name, Class<?> type) {
        super (name, type);
    }

    @Override
    public PropertyAccessor createPropertyAccessor (Object bean) {
        return new BeanPropertyAccessor (this.name, bean, this.type);
    }

}
