
package org.liujk.java.framework.base.utils.lang.beans.reference;



import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * 说明：
 * <p>
 * 提供了更多功能的 BeanInfo。
 *
 */
public interface BeanInformation<T> extends BeanInfo {

    /**
     * 得到所有的字段信息（包括公共的、保护的、默认的、私有的以及继承的）。
     *
     * @return 所有字段的Set。
     */
    Set<Field> getAllFields();

    /**
     * 得到一个 name 指定的一个 EventSetDescriptor。
     *
     * @param name EventSetDescriptor 描述 的 name。
     *
     * @return 合适的 EventSetDescriptor ，如果不存在则返回 null。
     */
    EventSetDescriptor getEventSetDescriptor(String name);

    /**
     * 得到一个 name 指定的一个 PropertyDescriptor。
     *
     * @param name PropertyDescriptor 描述 的 name。
     *
     * @return 合适的 PropertyDescriptor ，如果不存在则返回 null。
     */
    PropertyDescriptor getPropertyDescriptor(String name);

    /**
     * 得到一个 name 指定的一个 MethodDescriptor 描述。
     *
     * @param name    MethodDescriptor 描述的 name。
     * @param classes MethodDescriptor 的参数的类型数组。
     *
     * @return 合适的 MethodDescriptor ，如果不存在则返回 null。
     */
    MethodDescriptor getMethodDescriptor(String name, Class<?>... classes);
}
