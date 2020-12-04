
package org.liujk.java.framework.base.utils.lang.beans.reference;



import org.liujk.java.framework.base.utils.lang.ClassUtils;
import org.liujk.java.framework.base.utils.lang.ReflectionUtils;
import org.liujk.java.framework.base.utils.lang.collection.ArrayWrapper;

import java.awt.*;
import java.beans.*;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 说明：
 * <p>
 *
 */
public class CacheBeanInfo<T> implements BeanInformation<T> {
    private final BeanInfo beanInfo;

    private final ClassReference clazzReference;

    private final Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<String, PropertyDescriptor> ();
    private final Map<String, Map<ArrayWrapper, MethodDescriptor>> methodDescriptorMap
            = new HashMap<String, Map<ArrayWrapper, MethodDescriptor>> ();
    private final Map<String, EventSetDescriptor> eventSetDescriptorMap = new HashMap<String, EventSetDescriptor> ();
    private final Set<Field> fields;
    private final Reference<Class<?>>[] interfaceReferences;
    private volatile Reference<Class<?>[]> interfaces;

    @SuppressWarnings("unchecked")
    CacheBeanInfo (Class<T> clazz, BeanInfo beanInfo) {
        this.beanInfo = beanInfo;
        this.clazzReference = new ClassReference (clazz);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors ();
        if (propertyDescriptors != null) {
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                this.propertyDescriptorMap.put (propertyDescriptor.getName (), propertyDescriptor);
            }
        }
        MethodDescriptor[] methodDescriptors = beanInfo.getMethodDescriptors ();
        if (methodDescriptors != null) {
            for (MethodDescriptor methodDescriptor : methodDescriptors) {
                Map<ArrayWrapper, MethodDescriptor> mdm = this.methodDescriptorMap
                        .get (methodDescriptor.getName ());
                if (mdm == null) {
                    mdm = new HashMap<ArrayWrapper, MethodDescriptor> ();
                    this.methodDescriptorMap.put (methodDescriptor.getName (), mdm);
                }
                mdm.put (new ArrayWrapper (
                                 ClassUtils.toStrings (methodDescriptor.getMethod ().getParameterTypes ())),
                         methodDescriptor);
            }
        }
        EventSetDescriptor[] eventSetDescriptors = beanInfo.getEventSetDescriptors ();
        if (eventSetDescriptors != null) {
            for (EventSetDescriptor eventSetDescriptor : eventSetDescriptors) {
                this.eventSetDescriptorMap.put (eventSetDescriptor.getName (), eventSetDescriptor);
            }
        }
        Set<Field> fields = new HashSet<Field> ();
        allField (clazz, fields);
        this.fields = Collections.unmodifiableSet (fields);
        Set<Reference<Class<?>>> interfaces = new HashSet<Reference<Class<?>>> ();
        allInterface (clazz, interfaces);
        this.interfaceReferences = (Reference<Class<?>>[]) interfaces
                .toArray (new Reference<?>[interfaces.size ()]);
    }

    private static void allField (Class<?> clazz, Set<Field> fields) {
        if (clazz == null) {
            return;
        }
        // 添加自己的 Field
        for (Field field : clazz.getDeclaredFields ()) {
            fields.add (field);
        }
        // 解析父类的 Field
        allField (clazz.getSuperclass (), fields);
    }

    private static void allInterface (Class<?> clazz, Set<Reference<Class<?>>> interfaceClasses) {
        if (clazz == null) {
            return;
        }
        // 添加自己的 interface
        for (Class<?> interfaceClass : clazz.getInterfaces ()) {
            interfaceClasses.add (new WeakReference<Class<?>> (interfaceClass));
        }
        // 解析父类的 interface
        allInterface (clazz.getSuperclass (), interfaceClasses);
    }

    synchronized void clear () {
        ReflectionUtils.CACHE_BEAN_INFO.remove (this.clazzReference.get ());
    }

    @Override
    public Set<Field> getAllFields () {
        return this.fields;
    }

    public Class<?>[] getAllInterfaces () {
        Class<?>[] classes;
        if (this.interfaces == null || (classes = this.interfaces.get ()) == null) {
            classes = new Class<?>[this.interfaceReferences.length];
            for (int i = 0; i < classes.length; i++) {
                classes[i] = this.interfaceReferences[i].get ();
            }
            this.interfaces = new SoftReference<Class<?>[]> (classes);
        }
        return classes;
    }

    @Override
    public EventSetDescriptor getEventSetDescriptor (String name) {
        return this.eventSetDescriptorMap.get (name);
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor (String name) {
        return this.propertyDescriptorMap.get (name);
    }

    @Override
    public MethodDescriptor getMethodDescriptor (String name, Class<?>... classes) {
        Map<ArrayWrapper, MethodDescriptor> mdm = this.methodDescriptorMap.get (name);
        if (mdm == null) {
            return null;
        }
        String[] classStrArray = ClassUtils.toStrings (classes);
        if (classStrArray == null) {
            return mdm.get (null);
        }
        return mdm.get (new ArrayWrapper (classStrArray));
    }

    @Override
    public BeanInfo[] getAdditionalBeanInfo () {
        return this.beanInfo.getAdditionalBeanInfo ();
    }

    @Override
    public BeanDescriptor getBeanDescriptor () {
        return this.beanInfo.getBeanDescriptor ();
    }

    @Override
    public int getDefaultEventIndex () {
        return this.beanInfo.getDefaultEventIndex ();
    }

    @Override
    public int getDefaultPropertyIndex () {
        return this.beanInfo.getDefaultPropertyIndex ();
    }

    @Override
    public EventSetDescriptor[] getEventSetDescriptors () {
        return this.beanInfo.getEventSetDescriptors ();
    }

    @Override
    public Image getIcon (int iconKind) {
        return this.beanInfo.getIcon (iconKind);
    }

    @Override
    public MethodDescriptor[] getMethodDescriptors () {
        return this.beanInfo.getMethodDescriptors ();
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors () {
        return this.beanInfo.getPropertyDescriptors ();
    }

    /**
     * 将 {@link Class} 弱引用的包装器，防止 {@link Class} 保持强引用而不被回收，并且在回收 {@link Class} 会清空缓存中的记录。
     */
    class ClassReference extends FinalizableWeakReference<Class<T>> {

        ClassReference (Class<T> referent) {
            super (referent);
        }

        @Override
        public void finalizeReferent () {
            synchronized (ReflectionUtils.CACHE_BEAN_INFO) {
                ReflectionUtils.CACHE_BEAN_INFO.values ().remove (CacheBeanInfo.this);
            }
        }
    }

}
