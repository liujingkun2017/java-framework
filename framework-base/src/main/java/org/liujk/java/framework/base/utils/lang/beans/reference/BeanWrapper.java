
package org.liujk.java.framework.base.utils.lang.beans.reference;



import org.liujk.java.framework.base.utils.lang.beans.converter.TypeConverterManager;
import org.liujk.java.framework.base.utils.lang.beans.property.PropertyAccessor;
import org.liujk.java.framework.base.utils.lang.beans.property.PropertyWrapper;

import java.util.List;

/**
 * 说明：
 * <p>
 * 通过该包装器包装 bean 的实例可简化对 JavaBean 的访问。
 *
 * @see BeanWrapperProvider
 */
public interface BeanWrapper {
    /**
     * 得到 被包装的 bean 的实例。
     *
     * @return 被包装的 bean 的实例。
     */
    Object getWrappedInstance();

    /**
     * 通过属性名得到对应属性包装器。得到的属性包装器包装了该bean包装器所包装的bean的属性名对应的属性。
     *
     * @param propertyName 需要从 bean 中得到属性包装器的属性名。
     *
     * @return 属性名对应的属性包装器，如果为 null 则表示被包装的 bean 没有属性名对应的属性。
     */
    PropertyWrapper getPropertyWrapper(String propertyName);

    /**
     * 通过属性路径表达式得到对应的属性包装器列表。得到的属性包装器列表的下标对应了属性路径表达式的每一段的下标， 每个属性包装器包装了对应bean的属性名对应的属性。
     *
     * @param propertyPath 需要从 bean 中得到属性包装器列表的属性路径表达式。
     *
     * @return 属性路径表达式对应的属性包装器列表，如果空列表则表示被包装的 bean
     * 中没有属性路径表达式对应的属性（可能有其中一段路径，但是并不包含所有路径，这样的情况也会返回空列表）。
     */
    List<PropertyWrapper> getPropertyWrappers(String propertyPath);

    /**
     * 通过属性路径表达式调用被包装的bean的set方法完成value的写入。
     *
     * @param propertyPath 完成value写入的属性路径表达式。
     * @param value        需要写入的值。
     *
     * @return 如果写入成功返回 true ，否则返回 false 。
     * @throws Exception 如果过程中发生异常。
     */
    boolean setPropertyValue(String propertyPath, Object value) throws Exception;

    /**
     * 通过属性路径表达式与 callback 所指定的属性访问规则调用被包装的bean的set方法完成value的写入。
     *
     * @param propertyPath 完成value写入的属性路径表达式。
     * @param value        需要写入的值。
     * @param callback     属性访问的回调策略。
     *
     * @return 如果写入成功返回 true ，否则返回 false 。
     * @throws Exception 如果过程中发生异常。
     * @see PropertyAccessCallback
     */
    boolean setPropertyValue(String propertyPath, Object value, PropertyAccessCallback callback)
            throws Exception;

    /**
     * 属性访问回调接口，用于指定属性访问的具体操作。
     *
     * @see BeanWrapper#setPropertyValue(String, Object, PropertyAccessCallback)
     */
    public static interface PropertyAccessCallback {

        /**
         * 完成属性路径表达式指定的非最后一段的属性的写入。
         *
         * @param propertyWrapper      当前属性的属性包装器。
         * @param propertyAccessor     当前属性的属性访问器。
         * @param typeConverterManager 类型转换器管理器。
         *
         * @return 当前操作被写入的对象实例。
         * @throws Exception 如果过程中发生异常。
         */
        Object propertyWrite(PropertyWrapper propertyWrapper, PropertyAccessor propertyAccessor,
                             TypeConverterManager typeConverterManager) throws Exception;

        /**
         * 完成属性路径表达式指定的最后一段的属性的写入。
         *
         * @param value                当前需要写入的值。
         * @param propertyWrapper      当前属性的属性包装器。
         * @param propertyAccessor     当前属性的属性访问器。
         * @param typeConverterManager 类型转换器管理器。
         *
         * @return 当前操作被写入的对象实例。
         * @throws Exception 如果过程中发生异常。
         */
        Object lastPropertyWrite(Object value, PropertyWrapper propertyWrapper,
                                 PropertyAccessor propertyAccessor, TypeConverterManager typeConverterManager)
                throws Exception;

    }
}
