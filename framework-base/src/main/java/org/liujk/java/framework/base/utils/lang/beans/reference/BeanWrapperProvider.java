
package org.liujk.java.framework.base.utils.lang.beans.reference;



/**
 * 说明：
 * <p>
 * bean包装器提供者，用于创建bean包装器实例。
 *
 */
public interface BeanWrapperProvider {
    /**
     * 创建一个包装器。
     *
     * @param bean 需要被包装的 bean 实例。
     *
     * @return 包装 bean 的包装器。
     */
    BeanWrapper newBeanWrapper(Object bean);
}
