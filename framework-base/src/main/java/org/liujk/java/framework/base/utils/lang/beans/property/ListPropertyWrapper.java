
package org.liujk.java.framework.base.utils.lang.beans.property;



import java.util.List;

/**
 * 说明：
 * <p>
 * 属性所属 bean 为{@link List}的属性包装器。
 * <p>
 * {@link #createPropertyAccessor(Object)} 使用 {@link ListPropertyAccessor} 为实例对象。
 *
 */
public class ListPropertyWrapper extends AbstractPropertyWrapper {

    /**
     * 构造一个 ListPropertyWrapper 。
     *
     * @param index 元素下标。
     * @param type  元素类型。
     */
    public ListPropertyWrapper (Object index, Class<?> type) {
        super (index, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PropertyAccessor createPropertyAccessor (Object bean) {
        return new ListPropertyAccessor (this.name, (List<Object>) bean, this.type);
    }

}
