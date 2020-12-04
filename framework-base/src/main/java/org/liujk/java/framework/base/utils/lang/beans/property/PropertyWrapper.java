
package org.liujk.java.framework.base.utils.lang.beans.property;


/**
 * 说明：
 * <p>
 * 属性包装器，对 JavaBean 的 Property 进行了包装。
 *
 */
public interface PropertyWrapper {
    /**
     * 得到 被包装的属性的类型信息。
     *
     * @return 被包装的属性的类型信息。
     */
    Class<?> getType();

    /**
     * 得到 被包装的属性在 bean 的 {@link Class} 中的属性名称。
     *
     * @return 被包装的属性在 bean 的 {@link Class} 中的属性名称。
     */
    Object getName();

    /**
     * 使用一个被包装 bean 的实例创建一个属性访问器。
     *
     * @param bean 被包装的 bean 的实例。
     *
     * @return 对应的属性访问器。
     */
    PropertyAccessor createPropertyAccessor(Object bean);
}
