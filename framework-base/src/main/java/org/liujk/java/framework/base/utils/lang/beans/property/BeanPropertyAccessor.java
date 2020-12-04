
package org.liujk.java.framework.base.utils.lang.beans.property;


import org.liujk.java.framework.base.exceptions.RunException;
import org.liujk.java.framework.base.utils.lang.ReflectionUtils;
import org.liujk.java.framework.base.utils.lang.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * 说明：
 * <p>
 * 属性所属 bean实例 为标准 JavaBean 的属性访问器。
 *
 */
public class BeanPropertyAccessor extends AbstractPropertyAccessor<Object>
        implements PropertyAccessor {

    private final PropertyDescriptor propertyDescriptor;

    private final Class<?> type;

    /**
     * 使用访问的属性在 bean 中的名称、访问属性的目标 bean 对象构造一个 BeanPropertyAccessor 。
     * <p>
     * 该构造方法使用 {@link ReflectionUtils#getPropertyClass(Object, String)} 得到 访问的属性的属性描述。
     *
     * @param name   访问的属性在 bean 中的名称。
     * @param target 访问属性的目标 bean 对象。
     */
    public BeanPropertyAccessor (Object name, Object target) {
        this (name, target,
              ReflectionUtils.getPropertyDescriptor (target, StringUtils.toString (name)));
    }

    /**
     * 使用访问的属性在 bean 中的名称、访问属性的目标 bean 对象、访问的属性的类型构造一个 BeanPropertyAccessor 。
     * <p>
     * 该构造方法使用 {@link ReflectionUtils#getPropertyClass(Object, String)} 得到 访问的属性的属性描述。
     *
     * @param name   访问的属性在 bean 中的名称。
     * @param target 访问属性的目标 bean 对象。
     * @param type   访问的属性的类型。
     */
    public BeanPropertyAccessor (Object name, Object target, Class<?> type) {
        this (name, target,
              ReflectionUtils.getPropertyDescriptor (target, StringUtils.toString (name)), type);
    }

    /**
     * 使用访问的属性在 bean 中的名称、访问属性的目标 bean 对象、访问的属性的属性描述构造一个 BeanPropertyAccessor 。
     *
     * @param name               访问的属性在 bean 中的名称。
     * @param target             访问属性的目标 bean 对象。
     * @param propertyDescriptor 访问的属性的属性描述。
     */
    public BeanPropertyAccessor (Object name, Object target, PropertyDescriptor propertyDescriptor) {
        this (name, target, propertyDescriptor, null);
    }

    /**
     * 使用访问的属性在 bean 中的名称、访问属性的目标 bean 对象、访问的属性的属性描述、访问的属性的类型构造一个 BeanPropertyAccessor 。
     *
     * @param name               访问的属性在 bean 中的名称。
     * @param target             访问属性的目标 bean 对象。
     * @param propertyDescriptor 访问的属性的属性描述。
     * @param type               访问的属性的类型。
     */
    public BeanPropertyAccessor (Object name, Object target, PropertyDescriptor propertyDescriptor,
                                 Class<?> type) {
        super (name, target);
        this.propertyDescriptor = propertyDescriptor;
        this.type = type;
    }

    private static Method checkAndGetRead (Class<?> clazz, Object name,
                                           PropertyDescriptor propertyDescriptor) throws Exception {
        Method readMethod;
        if (propertyDescriptor == null
                || (readMethod = propertyDescriptor.getReadMethod ()) == null) {
            throw new Exception ("类 [" + clazz.getName () + "] 中没有字段名 [" + name + "] 对应的 getter 方法。");
        }
        return readMethod;
    }

    private static Method checkAndGetWrite (Class<?> clazz, Object name,
                                            PropertyDescriptor propertyDescriptor) throws Exception {
        Method writeMethod;
        if (propertyDescriptor == null
                || (writeMethod = propertyDescriptor.getWriteMethod ()) == null) {
            throw new Exception ("类 [" + clazz.getName () + "] 中没有字段名 [" + name + "] 对应的 setter 方法。");
        }
        return writeMethod;
    }

    @Override
    public Class<?> getType () {
        if (this.type != null) {
            return this.type;
        }
        if (this.propertyDescriptor == null) {
            throw new RunException ("类 [" + this.target.getClass ().getName () + "] 没有对应的属性 ["
                                            + StringUtils.toString (this.name) + "]");
        }
        return this.propertyDescriptor.getPropertyType ();
    }

    @Override
    public Object get () throws Exception {
        return ReflectionUtils.executeMethod (this.target,
                                              checkAndGetRead (this.target.getClass (), this.name,
                                                               this.propertyDescriptor));
    }

    @Override
    public void set (Object value) throws Exception {
        ReflectionUtils.executeMethod (this.target,
                                       checkAndGetWrite (this.target.getClass (), this.name, this.propertyDescriptor),
                                       value);
    }

}
