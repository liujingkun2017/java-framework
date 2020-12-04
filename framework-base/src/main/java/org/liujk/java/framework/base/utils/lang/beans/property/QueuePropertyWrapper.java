
package org.liujk.java.framework.base.utils.lang.beans.property;



import java.util.Queue;

/**
 * 说明：
 * <p>
 * 属性所属 bean 为{@link Queue}的属性包装器。
 * <p>
 * {@link #createPropertyAccessor(Object)} 抛出 {@link UnsupportedOperationException}。
 *
 */
public class QueuePropertyWrapper extends AbstractPropertyWrapper {

    /**
     * 构造一个 QueuePropertyWrapper 。
     *
     * @param name 元素名称。
     * @param type 元素类型。
     */
    public QueuePropertyWrapper (Object name, Class<?> type) {
        super (name, type);
    }

    @Override
    public PropertyAccessor createPropertyAccessor (Object bean) {
        throw new UnsupportedOperationException ();
    }

}