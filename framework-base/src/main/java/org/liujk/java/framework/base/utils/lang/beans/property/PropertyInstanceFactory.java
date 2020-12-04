
package org.liujk.java.framework.base.utils.lang.beans.property;


/**
 * 说明：
 * <p>
 * Property实例的工厂。
 *
 */
public interface PropertyInstanceFactory {
    /**
     * 创建一个属性的对象的实例。
     *
     * @param type 属性的 {@link Class} 对象。
     *
     * @return 合适的实例。
     * @throws NoSuchMethodException 如果没有指定的构造方法。
     */
    Object newInstance(Class<?> type) throws NoSuchMethodException;
}
