
package org.liujk.java.framework.base.utils.lang.beans.property;

/**
 * 说明：
 * <p>
 * 属性所属 bean 为数组的属性包装器。 *
 * <p>
 * {@link #createPropertyAccessor(Object)} 使用 {@link ArrayPropertyAccessor} 为实例对象。
 *
 */
public class ArrayPropertyWrapper extends AbstractPropertyWrapper {

    /**
     * 构造一个 ArrayPropertyWrapper 。
     *
     * @param index 元素下标。
     * @param type  元素类型。
     */
    public ArrayPropertyWrapper (Object index, Class<?> type) {
        super (index, type);
    }

    @Override
    public PropertyAccessor createPropertyAccessor (Object bean) {
        return new ArrayPropertyAccessor (this.name, bean);
    }

}