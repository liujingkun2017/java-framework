
package org.liujk.java.framework.base.utils.lang.beans.reference;



/**
 * 说明：
 * <p>
 * 对象包装器，支持对象访问，并且可在访问过程中完成类型转换。
 * <p>
 * 该包装器适用于满足 JavaBean 的 bean 实例。
 *
 */
public interface ObjectWrapper<E> extends BeanWrapper {

    /**
     * 得到 被包装的对象的实例。
     *
     * @return 被包装的对象的实例。
     */
    @Override
    E getWrappedInstance();

    /**
     * 得到当前对象propertyName指定属性的对象包装器。
     *
     * @param propertyName 需要从 bean 中得到属性的对象包装器的属性名。
     *
     * @return 对应的对象包装器。
     * @throws Exception 发生异常时。
     */
    ObjectWrapper<Object> getPropertyObjectWrapper(String propertyName) throws Exception;

    /**
     * 得到当前对象propertyName指定属性的对象包装器，并且将属性值转换为toType。
     * <p>
     * 类型转换的过程中，可能转换的结果会脱离源对象的引用。
     *
     * @param propertyName 需要从 bean 中得到属性的对象包装器的属性名。
     * @param toType       需要转换属性值的目标类型。
     *
     * @return 对应的对象包装器。
     * @throws Exception 发生异常时。
     */
    <T> ObjectWrapper<T> getPropertyObjectWrapper(String propertyName, Class<T> toType)
            throws Exception;

}
