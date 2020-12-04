
package org.liujk.java.framework.base.utils.lang.beans.property;



import org.liujk.java.framework.base.utils.lang.ReflectionUtils;
import org.liujk.java.framework.base.utils.lang.beans.reference.FieldTypeScanner;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 说明：
 * <p>
 * {@link PropertyWrapperProvider} 的标准实现。
 * <p>
 * 当前实现使用的 {@link PropertyWrapper} 的情况如下：
 * <ul>
 * <li>标准 JavaBean ： {@link BeanPropertyWrapper}</li>
 * <li>Map ： {@link MapPropertyWrapper}</li>
 * <li>Set ： {@link SetPropertyWrapper}</li>
 * <li>Queue ： {@link QueuePropertyWrapper}</li>
 * <li>List ： {@link ListPropertyWrapper}</li>
 * <li>数组 ： {@link ArrayPropertyWrapper}</li>
 * </ul>
 *
 */
public class SimplePropertyWrapperProvider implements PropertyWrapperProvider {

    private static Class<?> findPropertyType (Class<?> propertyClass, String fieldName,
                                              PropertyDescriptor propertyDescriptor, FieldTypeScanner fieldTypeScanner,
                                              int index) {
        if (propertyClass != null) {
            return propertyClass;
        }
        Method method = null;
        Type type = null;
        if (propertyDescriptor != null && (method = propertyDescriptor.getWriteMethod ()) != null) {
            type = method.getGenericParameterTypes ()[0];
        } else if (propertyDescriptor != null
                && (method = propertyDescriptor.getReadMethod ()) != null) {
            Class<?> returnClass = method.getReturnType ();
            if (!Void.class.isAssignableFrom (returnClass)) {
                type = returnClass;
            }
        }
        return fieldTypeScanner == null ? null : fieldTypeScanner.getNextType (type, index);
    }

    private static PropertyDescriptor getPropertyDescriptor (Class<?> beanClass, String propertyName,
                                                             PropertyDescriptor propertyDescriptor) {
        if (propertyDescriptor != null) {
            return propertyDescriptor;
        }
        return ReflectionUtils.getPropertyDescriptor (beanClass, propertyName);
    }

    @Override
    public PropertyWrapper newPropertyWrapper (Class<?> beanClass, String propertyName,
                                               Class<?> propertyType, PropertyDescriptor propertyDescriptor,
                                               FieldTypeScanner fieldTypeScanner) {
        PropertyWrapper propertyWrapper;
        if (beanClass.isArray ()) {
            Class<?> componentType = beanClass.getComponentType ();
            propertyWrapper = new ArrayPropertyWrapper (propertyName,
                                                        Object.class == componentType
                                                                ? findPropertyType (beanClass, propertyName,
                                                                                    propertyDescriptor,
                                                                                    fieldTypeScanner, 0)
                                                                : componentType);
        } else if (List.class.isAssignableFrom (beanClass)) {
            propertyWrapper = new ListPropertyWrapper (propertyName,
                                                       findPropertyType (propertyType, propertyName,
                                                                         getPropertyDescriptor (beanClass, propertyName,
                                                                                                propertyDescriptor),
                                                                         fieldTypeScanner, 0));
        } else if (Set.class.isAssignableFrom (beanClass)) {
            propertyWrapper = new SetPropertyWrapper (propertyName,
                                                      findPropertyType (propertyType, propertyName,
                                                                        getPropertyDescriptor (beanClass, propertyName,
                                                                                               propertyDescriptor),
                                                                        fieldTypeScanner, 0));
        } else if (Queue.class.isAssignableFrom (beanClass)) {
            propertyWrapper = new QueuePropertyWrapper (propertyName,
                                                        findPropertyType (propertyType, propertyName,
                                                                          getPropertyDescriptor (beanClass,
                                                                                                 propertyName,
                                                                                                 propertyDescriptor),
                                                                          fieldTypeScanner, 0));
        } else if (Map.class.isAssignableFrom (beanClass)) {
            propertyWrapper = new MapPropertyWrapper (propertyName,
                                                      findPropertyType (propertyType, propertyName,
                                                                        getPropertyDescriptor (beanClass, propertyName,
                                                                                               propertyDescriptor),
                                                                        fieldTypeScanner, 1));
        } else {
            propertyDescriptor = getPropertyDescriptor (beanClass, propertyName, propertyDescriptor);
            if (propertyDescriptor == null) {
                return null;
            }
            propertyWrapper = new BeanPropertyWrapper (propertyName,
                                                       propertyDescriptor.getPropertyType ());
            if (fieldTypeScanner != null) {
                fieldTypeScanner.getNextType ();
            }
        }
        return propertyWrapper;
    }

}
