
package org.liujk.java.framework.base.utils.lang.beans.property;


import org.liujk.java.framework.base.utils.lang.beans.reference.FieldTypeScanner;

import java.beans.PropertyDescriptor;

/**
 * 说明：
 * <p>
 * 属性包装器提供者，用于提供属性包装器实例。
 *
 */
public interface PropertyWrapperProvider {
    /**
     * 创建一个属性包装器的实例。
     *
     * @param beanClass          属性所属的 bean 的 {@link Class} 对象。
     * @param propertyName       属性的名称。
     * @param propertyName       属性的类型，如果暂时未知则为null。
     * @param propertyDescriptor 属性的描述信息，可能为 null 。
     * @param fieldTypeScanner   当前的字段类型扫描器。
     *
     * @return 合适的属性包装器实例。
     */
    PropertyWrapper newPropertyWrapper(Class<?> beanClass, String propertyName,
                                       Class<?> propertyType, PropertyDescriptor propertyDescriptor,
                                       FieldTypeScanner fieldTypeScanner);
}
