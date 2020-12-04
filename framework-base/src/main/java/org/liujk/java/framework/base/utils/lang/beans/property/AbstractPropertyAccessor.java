
package org.liujk.java.framework.base.utils.lang.beans.property;


import org.liujk.java.framework.base.utils.lang.beans.reference.Service;
import org.liujk.java.framework.base.utils.lang.beans.reference.ServiceLoader;

/**
 * 说明：
 * <p>
 * {@link PropertyAccessor} 的骨干实现，继承该类可以简化实现 {@link PropertyAccessor} 。
 * <p>
 * 该类的 {@link #propertyNewInstance()} 使用 {@link PropertyInstanceFactory} 来完成实例的创建，
 * {@link PropertyInstanceFactory} 由 {@link ServiceLoader} 从 <code>service/common/base/</code> 进行加载。
 *
 * @param <T> 访问属性的目标 bean 对象
 *
 * @see PropertyInstanceFactory
 * @see ServiceLoader#load(String)
 * @see Service#get()
 */
public abstract class AbstractPropertyAccessor<T> implements PropertyAccessor {

    private static final PropertyInstanceFactory PROPERTY_INSTANCE_FACTORY;

    static {
        ServiceLoader serviceLoader = ServiceLoader.load ("service/common/base/");
        Service<PropertyInstanceFactory> service = serviceLoader
                .load (PropertyInstanceFactory.class);
        PROPERTY_INSTANCE_FACTORY = service.get ();
    }

    /**
     * 访问的属性在 bean 中的名称
     */
    protected final Object name;

    /**
     * 访问属性的目标 bean 对象
     */
    protected final T target;

    /**
     * 给子类实例化的构造方法。
     *
     * @param name   访问的属性在 bean 中的名称。
     * @param target 访问属性的目标 bean 对象。
     */
    public AbstractPropertyAccessor (Object name, T target) {
        this.name = name;
        this.target = target;
    }

    @Override
    public Object getName () {
        return this.name;
    }

    @Override
    public T getTarget () {
        return this.target;
    }

    @Override
    public Object propertyNewInstance () throws NoSuchMethodException {
        return PROPERTY_INSTANCE_FACTORY.newInstance (getType ());
    }

}