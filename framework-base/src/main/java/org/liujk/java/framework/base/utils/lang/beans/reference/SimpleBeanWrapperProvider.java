
package org.liujk.java.framework.base.utils.lang.beans.reference;


/**
 * 说明：
 * <p>
 * {@link BeanWrapperProvider} 的默认实现，使用 {@link StandardBeanWrapper} 来包装 bean 。
 * <p>
 * <core> new StanderdBeanWrapper(bean); </core>
 *
 */
public class SimpleBeanWrapperProvider implements BeanWrapperProvider {

    @Override
    public BeanWrapper newBeanWrapper (Object bean) {
        return new StandardBeanWrapper (bean);
    }

}