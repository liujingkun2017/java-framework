
package org.liujk.java.framework.base.utils.lang;



import org.liujk.java.framework.base.exceptions.RunException;
import org.liujk.java.framework.base.utils.lang.beans.converter.TypeConverterUtils;
import org.liujk.java.framework.base.utils.lang.beans.property.PropertyAccessor;
import org.liujk.java.framework.base.utils.lang.beans.property.PropertyWrapper;
import org.liujk.java.framework.base.utils.lang.beans.reference.*;
import org.springframework.util.Assert;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * 说明：
 * <p>
 * 反射工具。
 * <p>
 * 该工具会尽可能的缓存解析过的类信息以减小反射带来的性能消耗。
 * <p>
 * 该工具中支持bean参数（bean属性）路径表达式的方法所使用的 {@link BeanWrapper} 由 {@link BeanWrapperProvider} 提供。
 * {@link BeanWrapperProvider} 由 {@link org.liujk.java.framework.base.utils.lang.beans.reference.ServiceLoader}
 * 从 <code>service/common/base/</code> 进行加载。
 * <p>
 * 如果没有特别说明，向该类的方法传递 null 都会抛出 {@link NullPointerException}
 *
 */
public class ReflectionUtils {
    /**
     * setter方法前缀
     */
    public static final String SETTER = "set";

    /**
     * getter方法前缀
     */
    public static final String GETTER = "get";
    /**
     * 缓存的 map
     */
    public static final Map<Class<?>, CacheBeanInfo<?>> CACHE_BEAN_INFO
            = new ConcurrentReferenceMap<Class<?>, CacheBeanInfo<?>> (
            ReferenceKeyType.WEAK, ReferenceValueType.SOFT, 1024);
    private static final BeanWrapperProvider BEAN_WRAPPER_PROVIDER;

    static {
        org.liujk.java.framework.base.utils.lang.beans.reference.ServiceLoader serviceLoader
                = org.liujk.java.framework.base.utils.lang.beans.reference.ServiceLoader
                .load ("service/common/base/");
        Service<BeanWrapperProvider> service = serviceLoader.load (BeanWrapperProvider.class);
        BEAN_WRAPPER_PROVIDER = service.get ();
    }

    /**
     * 通过方法名执行一个对象的特定方法，该方法执行的方法是类的指定公共成员方法。该方法会根据参数自动判断参数类型。
     *
     * @param object     要执行方法的对象。
     * @param methodName 要执行方法的方法名。
     * @param parameters 要执行方法的参数。
     *
     * @return 执行返回结果。
     * @throws NoSuchMethodException            如果对象中没有该方法。
     * @throws InvocationTargetRunTimeException 如果基础方法抛出异常。
     * @throws RunException                     如果执行方法时发生除方法自身错误外的其他错误。
     */
    public static Object executeMethod (Object object, String methodName, Object... parameters)
            throws NoSuchMethodException {
        Class<?>[] parameterTypes = ObjectParameterUtils
                .processParameterToParameterType (parameters);
        return executeMethod (object, methodName, parameterTypes, parameters);
    }

    /**
     * 通过方法名执行一个对象的特定方法，该方法执行的方法是类的指定公共成员方法。
     *
     * @param object         要执行方法的对象。
     * @param methodName     要执行方法的方法名。
     * @param parameterTypes 参数的类型。
     * @param parameters     要执行方法的参数。
     *
     * @return 执行返回结果。
     * @throws NoSuchMethodException            如果对象中没有该方法。
     * @throws InvocationTargetRunTimeException 如果基础方法抛出异常。
     * @throws RunException                     如果执行方法时发生除方法自身错误外的其他错误。
     */
    public static Object executeMethod (Object object, String methodName, Class<?>[] parameterTypes,
                                        Object[] parameters) throws NoSuchMethodException {
        Class<?> clazz = object.getClass ();
        Method method = clazz.getMethod (methodName, parameterTypes);
        return executeMethod (object, method, parameters);
    }

    /**
     * 通过方法名执行一个对象的特定方法，该方法执行的方法是类的所有方法，包括公共、保护、默认（包）访问和私有方法，但不包括继承的方法。 该方法会根据参数自动判断参数类型。
     *
     * @param object     要执行方法的对象。
     * @param methodName 要执行方法的方法名。
     * @param parameter  要执行方法的参数。
     *
     * @return 执行返回结果 如果对象中没有该方法。
     * @throws NoSuchMethodException            如果对象中没有该方法。
     * @throws InvocationTargetRunTimeException 如果基础方法抛出异常
     * @throws RunException                     如果执行方法时发生除方法自身错误外的其他错误
     */
    public static Object executeClassMethod (Object object, String methodName, Object... parameter)
            throws NoSuchMethodException {
        Class<?>[] parameterTypes = ObjectParameterUtils.processParameterToParameterType (parameter);
        return executeClassMethod (object, methodName, parameterTypes, parameter);
    }

    /**
     * 通过方法名执行一个对象的特定方法，该方法执行的方法是类的所有方法，包括公共、保护、默认（包）访问和私有方法，但不包括继承的方法。
     *
     * @param object         要执行方法的对象。
     * @param methodName     要执行方法的方法名。
     * @param parameterTypes 参数的类型。
     * @param parameter      要执行方法的参数。
     *
     * @return 执行返回结果 如果对象中没有该方法。
     * @throws NoSuchMethodException            如果对象中没有该方法。
     * @throws InvocationTargetRunTimeException 如果基础方法抛出异常。
     * @throws RunException                     如果执行方法时发生除方法自身错误外的其他错误。
     */
    public static Object executeClassMethod (Object object, String methodName,
                                             Class<?>[] parameterTypes, Object[] parameter)
            throws NoSuchMethodException {
        Class<?> clazz = object.getClass ();
        Method method = clazz.getDeclaredMethod (methodName, parameterTypes);
        methodSetAccessible (method);
        try {
            return method.invoke (object, parameter);
        } catch (InvocationTargetException e) {
            throw new InvocationTargetRunTimeException (e.getTargetException ());
        } catch (Exception e) {
            throw new RunException (e);
        }
    }

    /**
     * 通过方法名执行一个类的特定静态方法，该方法执行的方法是类的指定公共静态方法。
     *
     * @param clazz          要执行方法的类。
     * @param methodName     要执行方法的方法名。
     * @param parameterTypes 参数的类型。
     * @param parameter      要执行方法的参数。
     *
     * @return 执行返回结果。
     * @throws NoSuchMethodException            如果类中没有该方法。
     * @throws InvocationTargetRunTimeException 如果基础方法抛出异常。
     * @throws RunException                     如果执行方法时发生除方法自身错误外的其他错误。
     */
    public static Object executeStaticMethod (Class<?> clazz, String methodName,
                                              Class<?>[] parameterTypes, Object[] parameter)
            throws NoSuchMethodException {
        Method method = clazz.getMethod (methodName, parameterTypes);
        methodSetAccessible (method);
        try {
            return method.invoke (null, parameter);
        } catch (InvocationTargetException e) {
            throw new InvocationTargetRunTimeException (e.getTargetException ());
        } catch (Exception e) {
            throw new RunException (e);
        }
    }

    /**
     * 通过方法名执行一个类的特定静态方法，该方法执行的方法是类的指定公共静态方法。该方法会根据参数自动判断参数类型。
     *
     * @param clazz      要执行方法的类。
     * @param methodName 要执行方法的方法名。
     * @param parameter  要执行方法的参数。
     *
     * @return 执行返回结果。
     * @throws NoSuchMethodException            如果类中没有该方法。
     * @throws InvocationTargetRunTimeException 如果基础方法抛出异常。
     * @throws RunException                     如果执行方法时发生除方法自身错误外的其他错误。
     */
    public static Object executeStaticMethod (Class<?> clazz, String methodName, Object... parameter)
            throws NoSuchMethodException {
        Class<?>[] parameterTypes = ObjectParameterUtils.processParameterToParameterType (parameter);
        return executeStaticMethod (clazz, methodName, parameterTypes, parameter);
    }

    /**
     * 通过方法名执行一个类的特定静态方法，该方法执行的方法是类的指定公共静态方法。
     *
     * @param className      要执行方法的类的唯一限定名。
     * @param methodName     要执行方法的方法名。
     * @param parameterTypes 参数的类型。
     * @param parameter      要执行方法的参数。
     *
     * @return 执行返回结果。
     * @throws TypeNotPresentException          如果没找到限定名的类则抛出该异常。
     * @throws NoSuchMethodException            如果类中没有该方法。
     * @throws InvocationTargetRunTimeException 如果基础方法抛出异常。
     * @throws RunException                     如果执行方法时发生除方法自身错误外的其他错误
     */
    public static Object executeStaticMethod (String className, String methodName,
                                              Class<?>[] parameterTypes, Object[] parameter)
            throws NoSuchMethodException {
        Class<?> clazz = ClassUtils.applicationClass (className);
        return executeStaticMethod (clazz, methodName, parameterTypes, parameter);
    }

    /**
     * 通过方法名执行一个类的特定静态方法，该方法执行的方法是类的指定公共静态方法。该方法会根据参数自动判断参数类型。
     *
     * @param className  要执行方法的类的唯一限定名。
     * @param methodName 要执行方法的方法名。
     * @param parameter  要执行方法的参数。
     *
     * @return 执行返回结果。
     * @throws TypeNotPresentException          如果没找到限定名的类则抛出该异常。
     * @throws NoSuchMethodException            如果类中没有该方法。
     * @throws InvocationTargetRunTimeException 如果基础方法抛出异常。
     * @throws RunException                     如果执行方法时发生除方法自身错误外的其他错误。
     */
    public static Object executeStaticMethod (String className, String methodName,
                                              Object... parameter) throws NoSuchMethodException {
        Class<?>[] parameterTypes = ObjectParameterUtils.processParameterToParameterType (parameter);
        return executeStaticMethod (className, methodName, parameterTypes, parameter);
    }

    /**
     * 创建一个对象实例。该方法会根据参数自动判断参数类型。
     *
     * @param <T>        实例对象的类型。
     * @param clazz      要创建对象的类的{@link Class}对象。
     * @param parameters 要传递给构造方法的参数。
     *
     * @return 对应的实例。
     * @throws NoSuchMethodException            如果没有指定的构造方法。
     * @throws InstantiationRuntimeException    生成类的实例时发生错误。
     * @throws InvocationTargetRunTimeException 如果构造方法抛出异常。
     */
    public static <T> T createObject (Class<T> clazz, Object... parameters)
            throws NoSuchMethodException {
        if (clazz == null) {
            throw new InstantiationRuntimeException ("不能'null' 创建实例。");
        }
        Class<?>[] parameterTypes = ObjectParameterUtils
                .processParameterToParameterType (parameters);
        return createObject (clazz, parameterTypes, parameters);
    }

    /**
     * 创建一个对象实例。
     *
     * @param <T>            实例对象的类型
     * @param clazz          要创建对象的类的{@link Class}对象。
     * @param parameterTypes 参数的类型。
     * @param parameters     要传递给构造方法的参数。
     *
     * @return 对应的实例。
     * @throws NoSuchMethodException            如果没有指定的构造方法。
     * @throws InstantiationRuntimeException    生成类的实例时发生错误。
     * @throws InvocationTargetRunTimeException 如果构造方法抛出异常。
     */
    public static <T> T createObject (Class<T> clazz, Class<?>[] parameterTypes, Object[] parameters)
            throws NoSuchMethodException {
        if (clazz == null) {
            throw new InstantiationRuntimeException ("不能 'null' 创建实例。");
        }
        Constructor<T> constructor;
        if (ArrayUtils.isEmpty (parameterTypes)) {
            constructor = clazz.getDeclaredConstructor ();
            parameters = null;
        } else {
            constructor = clazz.getDeclaredConstructor (parameterTypes);
        }
        return createObject (constructor, parameters);
    }

    /**
     * 使用构造方法的 Constructor 对象创建一个对象实例。
     *
     * @param constructor 构造方法的 Constructor 对象。
     * @param parameters  要传递给构造方法的参数。
     *
     * @return 对应的实例。
     * @throws NullPointerException             如果 constructor 为 null 。
     * @throws InstantiationRuntimeException    生成类的实例时发生错误。
     * @throws InvocationTargetRunTimeException 如果构造方法抛出异常。
     */
    public static <T> T createObject (Constructor<T> constructor, Object... parameters) {
        if (!Modifier.isPublic (constructor.getModifiers ())) {
            constructorSetAccessible (constructor);
        }
        try {
            return constructor.newInstance (parameters);
        } catch (IllegalArgumentException e) {
            throw new InstantiationRuntimeException ("向该构造方法传递了一个不正确的参数。");
        } catch (InstantiationException e) {
            throw new InstantiationRuntimeException ("无法为接口或者抽象类创建实例。");
        } catch (IllegalAccessException e) {
            throw new InstantiationRuntimeException ("预创建的对象无法构造。");
        } catch (InvocationTargetException e) {
            throw new InvocationTargetRunTimeException (e.getTargetException ());
        }
    }

    /**
     * 创建一个对象实例。该方法会根据参数自动判断参数类型。
     *
     * @param <T>        实例对象的类型。
     * @param className  要创建对象的类的完全限定名。
     * @param parameters 要传递给构造方法的参数。
     *
     * @return 对应的实例。
     * @throws NoSuchMethodException         如果没有指定的构造方法。
     * @throws InstantiationRuntimeException 生成类的实例时发生错误。
     * @throws TypeNotPresentException       如果构造方法抛出异常。
     * @see #createObject(Class, Object...)
     */
    @SuppressWarnings("unchecked")
    public static <T> T createObject (String className, Object... parameters)
            throws NoSuchMethodException {
        return (T) createObject (ClassUtils.applicationClass (className), parameters);
    }

    /**
     * 创建一个对象实例。该方法会根据参数自动判断参数类型。
     *
     * @param <T>         实例对象的类型。
     * @param className   要创建对象的类的完全限定名。
     * @param classLoader 用于创建/查找实例 {@link Class} 的类加载器。
     * @param parameters  要传递给构造方法的参数。
     *
     * @return 对应的实例。
     * @throws NoSuchMethodException         如果没有指定的构造方法。
     * @throws InstantiationRuntimeException 生成类的实例时发生错误。
     * @throws TypeNotPresentException       如果构造方法抛出异常。
     * @see #createObject(Class, Object...)
     */
    @SuppressWarnings("unchecked")
    public static <T> T createObject (String className, ClassLoader classLoader,
                                      Object... parameters) throws NoSuchMethodException {
        return (T) createObject (ClassUtils.applicationClass (className, classLoader), parameters);
    }

    /**
     * 创建一个对象实例。
     *
     * @param <T>         实例对象的类型。
     * @param className   要创建对象的类的完全限定名。
     * @param classLoader 用于创建/查找实例 {@link Class} 的类加载器。
     *
     * @return 对应的实例。
     * @throws NoSuchMethodException            如果没有指定的构造方法。
     * @throws InstantiationRuntimeException    生成类的实例时发生错误。
     * @throws InvocationTargetRunTimeException 如果构造方法抛出异常。
     * @throws TypeNotPresentException          如果没找到限定名的类则抛出该异常。
     */
    @SuppressWarnings("unchecked")
    public static <T> T createObject (String className, ClassLoader classLoader)
            throws NoSuchMethodException {
        return (T) createObject (ClassUtils.applicationClass (className, classLoader), null, null);
    }

    /**
     * 创建一个对象实例。
     *
     * @param <T>       实例对象的类型。
     * @param className 要创建对象的类的完全限定名。
     *
     * @return 对应的实例。
     * @throws NoSuchMethodException            如果没有指定的构造方法。
     * @throws InstantiationRuntimeException    生成类的实例时发生错误。
     * @throws InvocationTargetRunTimeException 如果构造方法抛出异常。
     * @throws TypeNotPresentException          如果没找到限定名的类则抛出该异常。
     */
    @SuppressWarnings("unchecked")
    public static <T> T createObject (String className) throws NoSuchMethodException {
        return (T) createObject (ClassUtils.applicationClass (className), null, null);
    }

    /**
     * 创建一个对象实例。
     *
     * @param <T>   实例对象的类型。
     * @param clazz 要创建对象的类的{@link Class}对象。
     *
     * @return 对应的实例。
     * @throws NoSuchMethodException            如果没有指定的构造方法。
     * @throws InstantiationRuntimeException    生成类的实例时发生错误。
     * @throws InvocationTargetRunTimeException 如果构造方法抛出异常。
     */
    public static <T> T createObject (Class<T> clazz) throws NoSuchMethodException {
        return createObject (clazz, null, null);
    }

    /**
     * 创建一个对象实例。
     *
     * @param <T>            实例对象的类型
     * @param className      要创建对象的类的完全限定名。
     * @param classLoader    用于创建/查找实例 {@link Class} 的类加载器。
     * @param parameterTypes 参数的类型。
     * @param parameters     要传递给构造方法的参数。
     *
     * @return 对应的实例。
     * @throws NoSuchMethodException            如果没有指定的构造方法。
     * @throws InstantiationRuntimeException    生成类的实例时发生错误。
     * @throws InvocationTargetRunTimeException 如果构造方法抛出异常。
     * @throws TypeNotPresentException          如果没找到限定名的类则抛出该异常。
     * @see #createObject(Class, Object...)
     */
    @SuppressWarnings("unchecked")
    public static <T> T createObject (String className, ClassLoader classLoader,
                                      Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException {
        return (T) createObject (ClassUtils.applicationClass (className, classLoader), parameterTypes,
                                 parameters);
    }

    /**
     * 创建一个对象实例。
     *
     * @param <T>            实例对象的类型
     * @param className      要创建对象的类的完全限定名。
     * @param parameterTypes 参数的类型。
     * @param parameters     要传递给构造方法的参数。
     *
     * @return 对应的实例。
     * @throws NoSuchMethodException            如果没有指定的构造方法。
     * @throws InstantiationRuntimeException    生成类的实例时发生错误。
     * @throws InvocationTargetRunTimeException 如果构造方法抛出异常。
     * @throws TypeNotPresentException          如果没找到限定名的类则抛出该异常。
     * @see #createObject(Class, Object...)
     */
    @SuppressWarnings("unchecked")
    public static <T> T createObject (String className, Class<?>[] parameterTypes,
                                      Object[] parameters) throws NoSuchMethodException {
        return (T) createObject (ClassUtils.applicationClass (className), parameterTypes, parameters);
    }

    /**
     * 将多个参数注入到对象实例中，该方法的字段名支持对象导航图的形式。
     *
     * @param object   要注入参数的对象。
     * @param paramMap 以字段名为key，参数值为value的{@link Map}。
     *
     * @throws RunException 发生异常时。
     */
    public static void injectionParameter (Object object, Map<String, ? extends Object> paramMap) {
        for (Map.Entry<String, ? extends Object> param : paramMap.entrySet ()) {
            setParameter (object, param.getKey (), param.getValue ());
        }
    }

    /**
     * 以 object 的 写入方法(setter) 对应的名称为 key，注入 parameterMap 中对应 key 的 value。
     * <p>
     * 如果注入类型与值的类型兼容，则直接注入。
     * <p>
     * 当注入的类型为 {@link List} 时，会使用 {@link ArrayList} 作为实现注入，当注入类型为 {@link Queue} 时，会使用
     * {@link LinkedList} 作为实现注入，当注入类型为 {@link Set} 时，会使用 {@link HashSet} 作为实现注入， 如果为 {@link Date}
     * 类型，则将值转化为 long 作为 {@link Date} 的构造参数值构造新实例注入。当类型为其他接口时，抛出异常。
     * <p>
     * 如果 写入方法 中对应名称在 parameterMap 中没有相应的值 ，那么，如果 写入方法 对应的类型为 Collection/数组 ，则注空的
     * Collection实例/数组，如果为其他，则注入 null。
     *
     * @param object       要注入 parameterMap 值的对象。
     * @param parameterMap 要将值注入 object 的 Map。
     *
     * @throws NoSuchMethodException 在需要创建新实例时，如果该实例没有默认构造方法。
     * @throws ClassCastException    如果出现转型错误。
     */
    public static void injectionObject (Object object, Map<String, ? extends Object> parameterMap)
            throws NoSuchMethodException {
        if (parameterMap.size () != 0 && object != null) {
            // 解析 object
            BeanInfo beanInfo = ObjectUtils.getBeanInfo (object.getClass ());
            // 得到所有字段信息
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors ();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                Class<?> propertyType = propertyDescriptor.getPropertyType ();
                String fieldName = propertyDescriptor.getName ();
                Object entryValue = parameterMap.get (fieldName);
                Object value = TypeConverterUtils.convertValue (entryValue, propertyType);
                if ("class".equals (propertyDescriptor.getName ())) {
                    continue;
                }
                executeMethod (object, propertyDescriptor.getWriteMethod (), value);
            }
        }
    }

    /**
     * 对象实例注入。
     *
     * @param object        要注入对象实例的对象。
     * @param parameterName 字段名称或者对象导航图。
     * @param value         值。
     *
     * @throws RunException 发生异常时。
     */
    public static void setParameter (Object object, String parameterName, Object value) {
        Assert.notNull (object, "{object} 不能为 'null' 。");
        Assert.notNull (parameterName, "{parameterName} 不能为 'null' 。");
        BeanWrapper beanWrapper = BEAN_WRAPPER_PROVIDER.newBeanWrapper (object);
        try {
            beanWrapper.setPropertyValue (parameterName, value);
        } catch (RunException e) {
            throw e;
        } catch (Exception e) {
            throw new RunException (e);
        }
    }

    /**
     * 调用对应对象的setter方法。该方法不会提供类型转换。
     *
     * @param object 需要调用setter方法的对象。
     * @param name   需要调用setter方法的字段名。
     * @param value  需要传入setter方法的参数的值。
     *
     * @throws RunException 发生异常时。
     */
    public static void setter (Object object, String name, Object value)
            throws NoSuchMethodException {
        try {
            executeMethod (object, getSetterMethod (object.getClass (), name), value);
        } catch (RunException e) {
            throw e;
        } catch (Exception e) {
            throw new RunException (e);
        }
    }

    /**
     * 以 parameterName 从 object 中取值。
     *
     * @param object        需要取值的对象。
     * @param parameterName 字段名称或者对象导航图。
     *
     * @return object 中 parameterName 对应的值。
     * @throws RunException 发生异常时。
     */
    public static Object getParameter (Object object, String parameterName) {
        Assert.notNull (object, "{object} 不能为 'null' 。");
        Assert.notNull (parameterName, "{parameterName} 不能为 'null' 。");
        BeanWrapper beanWrapper = BEAN_WRAPPER_PROVIDER.newBeanWrapper (object);
        List<PropertyWrapper> propertyWrappers = beanWrapper.getPropertyWrappers (parameterName);
        if (propertyWrappers.isEmpty ()) {
            throw new RunException (
                    new NoSuchMethodException ("没有表达式 '" + parameterName + "' 对应的属性的get方法。"));
        }
        for (PropertyWrapper propertyWrapper : propertyWrappers) {
            PropertyAccessor propertyAccessor = propertyWrapper.createPropertyAccessor (object);
            try {
                object = propertyAccessor.get ();
            } catch (RunException e) {
                throw e;
            } catch (Exception e) {
                throw new RunException (e);
            }
        }
        return object;
    }

    /**
     * 调用对应对象的getter方法。
     *
     * @param object 需要调用getter方法的对象。
     * @param name   需要调用getter方法的字段名。
     *
     * @return getter方法的返回值。
     * @throws NoSuchMethodException            如果对象中没有字段的访问方法。
     * @throws InvocationTargetRunTimeException 如果基础方法抛出异常。
     * @throws RunException                     发生其他异常时。
     */
    public static Object getter (Object object, String name) throws NoSuchMethodException {
        return executeMethod (object, getGetterMethod (object.getClass (), name));
    }

    /**
     * 执行一个方法。
     *
     * @param object     需要执行方法的实例。
     * @param method     需要执行的方法的 Method 对象。
     * @param parameters 传递给方法的参数。
     *
     * @return 方法的返回值。
     * @throws NullPointerException             如果 method 为 null 。
     * @throws InvocationTargetRunTimeException 如果执行的方法内部抛出异常。
     * @throws RunException                     发生其他异常时。
     */
    public static Object executeMethod (Object object, Method method, Object... parameters) {
        if (!Modifier.isPublic (method.getModifiers ())) {
            methodSetAccessible (method);
        }
        try {
            return method.invoke (object, parameters);
        } catch (InvocationTargetException e) {
            throw new InvocationTargetRunTimeException (e.getTargetException ());
        } catch (Exception e) {
            throw new RunException (e);
        }
    }

    /**
     * 执行一个字段的get。
     *
     * @param object 需要执行字段get的实例。
     * @param field  需要执行的字段的 Field 对象。
     *
     * @return 字段的值。
     * @throws NullPointerException 如果 field 为 null 。
     * @throws RunException         发生异常时。
     */
    public static Object executeFieldGet (Object object, Field field) {
        if (!Modifier.isPublic (field.getModifiers ())) {
            fieldSetAccessible (field);
        }
        try {
            return field.get (object);
        } catch (Exception e) {
            throw new RunException (e);
        }
    }

    /**
     * 执行一个字段的set。
     *
     * @param object    需要执行字段set的实例。
     * @param field     需要执行的字段的 Field 对象。
     * @param parameter 进行字段 set 时的参数值。
     *
     * @throws NullPointerException 如果 field 为 null 。
     * @throws RunException         发生其他异常时。
     */
    public static void executeFieldSet (Object object, Field field, Object parameter) {
        if (!Modifier.isPublic (field.getModifiers ())) {
            fieldSetAccessible (field);
        }
        try {
            field.set (object, parameter);
        } catch (Exception e) {
            throw new RunException (e);
        }
    }

    /**
     * 得到一个类的构造方法，包括公共、保护、默认、私有的。
     *
     * @param clazz          需要得到构造方法的类的 Class 对象。
     * @param parameterTypes 构造方法的参数的 Class 对象。
     *
     * @return 对应的构造方法的 Constructor 对象，如果没有这样的构造方法则返回 null 。
     */
    public static Constructor<?> getConstructor (Class<?> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredConstructor (parameterTypes);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 得到一个类的公共方法。
     *
     * @param clazz          需要得到公共方法的类的 {@link Class} 对象。
     * @param methodName     需要得到方法的方法名。
     * @param parameterTypes 方法的参数类型数组。
     *
     * @return 对应的方法的 Method 对象，如果没有这样的方法则返回 null 。
     */
    public static Method getMethod (Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        MethodDescriptor methodDescriptor = getMethodDescriptor (clazz, methodName, parameterTypes);
        Method method;
        if (methodDescriptor == null || (method = methodDescriptor.getMethod ()) == null) {
            return null;
        }
        return method;
    }

    /**
     * 得到一个类的申明已知的方法，包括公共、保护、默认（包）访问和私有方法，但不包括继承的方法。
     *
     * @param clazz          需要得到方法的类的 {@link Class} 对象。
     * @param methodName     需要得到方法的方法名。
     * @param parameterTypes 方法的参数类型数组。
     *
     * @return 对应的方法的 Method 对象，如果没有这样的方法则返回 null 。
     */
    public static Method getDeclaredMethod (Class<?> clazz, String methodName,
                                            Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod (methodName, parameterTypes);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 得到一个类的公共字段。
     *
     * @param clazz     需要得到字段的类的 {@link Class} 对象。
     * @param fieldName 需要得到字段的字段名。
     *
     * @return 对应的字段的 Field 对象，如果没有这样的字段则返回 null 。
     */
    public static Field getField (Class<?> clazz, String fieldName) {
        try {
            return clazz.getField (fieldName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 得到一个类的申明已知的字段，包括公共、保护、默认（包）访问和私有字段，但不包括继承的字段。
     *
     * @param clazz     需要得到公共字段的类的 {@link Class} 对象。
     * @param fieldName 需要得到字段的字段名。
     *
     * @return 对应的字段的 Field 对象，如果没有这样的字段则返回 null 。
     */
    public static Field getDeclaredField (Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField (fieldName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 得到对象实例中的name对应属性的{@link Class}对象。
     *
     * @param clazz 要得到对象实例中的name对应属性的{@link Class}对象的对象实例的{@link Class}对象。
     * @param name  属性名。
     *
     * @return 对象实例中的name对应属性的{@link Class}对象。
     * @throws NoSuchFieldException 如果找不到带有指定名称的属性。。
     */
    public static Class<?> getPropertyClass (Class<?> clazz, String name)
            throws NoSuchFieldException {
        BeanInformation<?> beanInfo = ObjectUtils.getCacheBeanInfo (clazz);
        return getPropertyClass (beanInfo, name);
    }

    static Class<?> getPropertyClass (BeanInformation<?> beanInfo, String name)
            throws NoSuchFieldException {
        PropertyDescriptor propertyDescriptor = beanInfo.getPropertyDescriptor (name);
        if (propertyDescriptor == null) {
            throw new NoSuchFieldException (
                    "[" + beanInfo.getBeanDescriptor ().getName () + "] 中没有字段'" + name + "'.");
        }
        return propertyDescriptor.getPropertyType ();
    }

    /**
     * 得到对象实例中的name对应属性的{@link Class}对象。
     *
     * @param object 要得到对象实例中的name对应属性的{@link Class}对象的对象实例。
     * @param name   属性名。
     *
     * @return 对象实例中的name对应属性的{@link Class}对象。
     * @throws NoSuchFieldException 如果找不到带有指定名称的属性。
     */
    public static Class<?> getPropertyClass (Object object, String name)
            throws NoSuchFieldException {
        return getPropertyClass (object.getClass (), name);
    }

    /**
     * 向对象中fieldName对应的字段注入值parameter，包括公共、保护、默认（包）访问和私有字段，但不包括继承的字段。
     *
     * @param object    要注入值的对象。
     * @param fieldName 要注入值字段名。
     * @param parameter 值。
     *
     * @throws NoSuchFieldException 对象中没有 fieldName 指定的字段时。
     * @throws RunException         发生其他异常。
     */
    public static void executeFieldValue (Object object, String fieldName, Object parameter)
            throws NoSuchFieldException {
        Class<?> objectClass = object.getClass ();
        Field field = objectClass.getDeclaredField (fieldName);
        if (!Modifier.isPublic (field.getModifiers ())) {
            fieldSetAccessible (field);
        }
        try {
            field.set (object, parameter);
        } catch (Exception e) {
            throw new RunException (e);
        }
    }

    /**
     * 得到对象中fieldName对应的字段的值，包括公共、保护、默认（包）访问和私有字段，但不包括继承的字段。
     *
     * @param object    要得到值的对象。
     * @param fieldName 要得到值的字段名。
     *
     * @throws NoSuchFieldException 对象中没有 fieldName 指定的字段时。
     * @throws RunException         发生其他异常。
     */
    public static Object getFieldValue (Object object, String fieldName)
            throws NoSuchFieldException {
        Class<?> objectClass = object.getClass ();
        Field field = objectClass.getDeclaredField (fieldName);
        if (!Modifier.isPublic (field.getModifiers ())) {
            fieldSetAccessible (field);
        }
        try {
            return field.get (object);
        } catch (Exception e) {
            throw new RunException (e);
        }
    }

    /**
     * 判断对象中是否存在某个字段，该字段可以是公共、保护、默认（包）访问和私有的，但不包括继承的。
     *
     * @param object    要检测的对象。
     * @param fieldName 要检测的字段名。
     *
     * @return 存在返回 true , 不存在返回 false。
     * @throws RunException 发生异常时。
     */
    public static boolean fieldExists (Object object, String fieldName) {
        return fieldExists (object.getClass (), fieldName);
    }

    /**
     * 判断类中是否存在某个字段，该字段可以是公共、保护、默认（包）访问和私有的，但不包括继承的。
     *
     * @param clazz     要检测类的 Class 对象。
     * @param fieldName 要检测的字段名。
     *
     * @return 存在返回 true , 不存在返回 false。
     * @throws RunException 发生异常时。
     */
    public static boolean fieldExists (Class<?> clazz, String fieldName) {
        try {
            clazz.getDeclaredField (fieldName);
        } catch (SecurityException e) {
            throw new RunException (e);
        } catch (NoSuchFieldException e) {
            return false;
        }
        return true;
    }

    /**
     * 判断对象中是否存在某个方法，该方法可以是公共、保护、默认（包）访问和私有的，但不包括继承的。
     *
     * @param object     要检测的对象。
     * @param methodName 要检测的方法名。
     * @param paramClass 方法的签名。
     *
     * @return 存在返回 true , 不存在返回 false。
     * @throws RunException 发生异常时。
     */
    public static boolean methodExists (Object object, String methodName, Class<?>... paramClass) {
        return methodExists (object.getClass (), methodName, paramClass);
    }

    /**
     * 判断类中是否存在某个方法，该方法可以是公共、保护、默认（包）访问和私有的，但不包括继承的。
     *
     * @param clazz      要检测类的 Class 对象。
     * @param methodName 要检测的方法名。
     * @param paramClass 方法的签名。
     *
     * @return 存在返回 true , 不存在返回 false。
     * @throws RunException 发生异常时。
     */
    public static boolean methodExists (Class<?> clazz, String methodName, Class<?>... paramClass) {
        try {
            clazz.getDeclaredMethod (methodName, paramClass);
        } catch (SecurityException e) {
            throw new RunException (e);
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    /**
     * 得到指定的 clazz 里 name 对应的写入方法。
     *
     * @param clazz 指定的 Class。
     * @param name  写入方法的 name。
     *
     * @return 返回对应的写入方法的 Method 对象。
     * @throws NoSuchMethodException 如果没有找到这样的方法。
     */
    public static Method getSetterMethod (Class<?> clazz, String name) throws NoSuchMethodException {
        BeanInformation<?> beanInfo = ObjectUtils.getCacheBeanInfo (clazz);
        PropertyDescriptor propertyDescriptor = beanInfo.getPropertyDescriptor (name);
        Method writeMethod;
        if (propertyDescriptor == null
                || (writeMethod = propertyDescriptor.getWriteMethod ()) == null) {
            throw new NoSuchMethodException (
                    "[" + clazz.getName () + "] 中没有字段 '" + name + "' 对应的访问方法。");
        }
        return writeMethod;
    }

    /**
     * 得到指定的 object 里 name 对应的写入方法。
     *
     * @param object 指定的对象。
     * @param name   写入方法的 name。
     *
     * @return 返回对应的写入方法的 Method 对象。
     * @throws NoSuchMethodException 如果没有找到这样的方法。
     */
    public static Method getSetterMethod (Object object, String name) throws NoSuchMethodException {
        return getSetterMethod (object.getClass (), name);
    }

    /**
     * 得到指定的 clazz 里所有的写入方法。
     *
     * @param clazz 指定的 Class。
     *
     * @return 返回对应的写入方法的 Method 数组对象。
     */
    public static Method[] getSetterMethods (Class<?> clazz) {
        BeanInformation<?> beanInfo = ObjectUtils.getCacheBeanInfo (clazz);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors ();
        Method[] methods = new Method[propertyDescriptors.length];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = propertyDescriptors[i].getWriteMethod ();
        }
        return methods;
    }

    /**
     * 得到指定的 object 里所有的写入方法。
     *
     * @param object 指定的对象。
     *
     * @return 返回对应的写入方法的 Method 数组对象。
     */
    public static Method[] getSetterMethods (Object object) {
        return getSetterMethods (object.getClass ());
    }

    /**
     * 得到指定的 clazz 里 name 对应的读取方法。
     *
     * @param clazz 指定的 Class。
     * @param name  读取方法的 name。
     *
     * @return 返回对应的读取方法的 Method 对象。
     * @throws NoSuchMethodException 如果没有找到这样的方法。
     */
    public static Method getGetterMethod (Class<?> clazz, String name) throws NoSuchMethodException {
        BeanInformation<?> beanInfo = ObjectUtils.getCacheBeanInfo (clazz);
        PropertyDescriptor propertyDescriptor = beanInfo.getPropertyDescriptor (name);
        Method readMethod;
        if (propertyDescriptor == null
                || (readMethod = propertyDescriptor.getReadMethod ()) == null) {
            throw new NoSuchMethodException (
                    "[" + clazz.getName () + "] 中没有字段 [" + name + "]对应的读取方法.");
        }
        return readMethod;
    }

    /**
     * 得到指定的 object 里 name 对应的读取方法。
     *
     * @param object 指定的对象。
     * @param name   读取方法的 name。
     *
     * @return 返回对应的读取方法的 Method 对象。
     * @throws NoSuchMethodException 如果没有找到这样的方法。
     */
    public static Method getGetterMethod (Object object, String name) throws NoSuchMethodException {
        return getGetterMethod (object.getClass (), name);
    }

    /**
     * 得到指定的 clazz 里所有的读取方法。
     *
     * @param clazz 指定的 Class。
     *
     * @return 返回对应的读取方法的 Method 数组对象。
     */
    public static Method[] getGetterMethods (Class<?> clazz) {
        BeanInformation<?> beanInfo = ObjectUtils.getCacheBeanInfo (clazz);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors ();
        Method[] methods = new Method[propertyDescriptors.length];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = propertyDescriptors[i].getReadMethod ();
        }
        return methods;
    }

    /**
     * 得到指定的 object 里所有的读取方法。
     *
     * @param object 指定的对象。
     *
     * @return 返回对应的读取方法的 Method 数组对象。
     */
    public static Method[] getGetterMethods (Object object) {
        return getGetterMethods (object.getClass ());
    }

    /**
     * 得到一个 name 指定的类 clazz 中的一个 PropertyDescriptor 描述。
     *
     * @param clazz 要得到 PropertyDescriptor 描述 的类的 Class 对象。
     * @param name  PropertyDescriptor 描述 在 clazz 中的 name。
     *
     * @return 合适的 PropertyDescriptor ，如果不存在则返回 null。
     */
    public static PropertyDescriptor getPropertyDescriptor (Class<?> clazz, String name) {
        return ObjectUtils.getCacheBeanInfo (clazz).getPropertyDescriptor (name);
    }

    /**
     * 得到一个 name 指定的实例 object 中的一个 PropertyDescriptor。
     *
     * @param object 要得到 PropertyDescriptor 描述 的 实例对象。
     * @param name   PropertyDescriptor 描述 在 object 中的 name。
     *
     * @return 合适的 PropertyDescriptor ，如果不存在则返回 null。
     */
    public static PropertyDescriptor getPropertyDescriptor (Object object, String name) {
        return getPropertyDescriptor (object.getClass (), name);
    }

    /**
     * 得到一个 name 指定的类 clazz 中的一个 EventSetDescriptor 描述。
     *
     * @param clazz 要得到 EventSetDescriptor 描述 的类的 Class 对象。
     * @param name  EventSetDescriptor 描述 在 clazz 中的 name。
     *
     * @return 合适的 EventSetDescriptor ，如果不存在则返回 null。
     */
    public static EventSetDescriptor getEventSetDescriptor (Class<?> clazz, String name) {
        return ObjectUtils.getCacheBeanInfo (clazz).getEventSetDescriptor (name);
    }

    /**
     * 得到一个 name 指定的实例 object 中的一个 EventSetDescriptor。
     *
     * @param object 要得到 EventSetDescriptor 描述 的 实例对象。
     * @param name   EventSetDescriptor 描述 在 object 中的 name。
     *
     * @return 合适的 EventSetDescriptor ，如果不存在则返回 null。
     */
    public static EventSetDescriptor getEventSetDescriptor (Object object, String name) {
        return getEventSetDescriptor (object.getClass (), name);
    }

    /**
     * 得到一个 name 指定的类 clazz 中的一个 MethodDescriptor 描述。
     *
     * @param clazz   要得到 MethodDescriptor 描述 的类的 Class 对象。
     * @param name    MethodDescriptor 描述 在 clazz 中的 name。
     * @param classes MethodDescriptor 的参数的类型数组。
     *
     * @return 合适的 MethodDescriptor ，如果不存在则返回 null。
     */
    public static MethodDescriptor getMethodDescriptor (Class<?> clazz, String name,
                                                        Class<?>... classes) {
        return ObjectUtils.getCacheBeanInfo (clazz).getMethodDescriptor (name, classes);
    }

    /**
     * 得到一个 name 指定的实例 object 中的一个 MethodDescriptor。
     *
     * @param object  要得到 MethodDescriptor 描述 的 实例对象。
     * @param name    MethodDescriptor 描述 在 object 中的 name。
     * @param classes MethodDescriptor 的参数的类型数组。
     *
     * @return 合适的 MethodDescriptor ，如果不存在则返回 null。
     */
    public static MethodDescriptor getMethodDescriptor (Object object, String name,
                                                        Class<?>... classes) {
        return getMethodDescriptor (object.getClass (), name, classes);
    }

    /**
     * 得到一个实例 object 中的一个 EventSetDescriptor 数组。
     *
     * @param object 要得到 EventSetDescriptor数组 描述 的 实例对象。
     *
     * @return 合适的 EventSetDescriptor数组。
     */
    public static EventSetDescriptor[] getEventSetDescriptors (Object object) {
        return getEventSetDescriptors (object.getClass ());
    }

    /**
     * 得到一个 {@link Class} 中的一个 EventSetDescriptor 数组。
     *
     * @param clazz 要得到 EventSetDescriptor数组 描述 的 {@link Class}对象。
     *
     * @return 合适的 EventSetDescriptor数组。
     */
    public static EventSetDescriptor[] getEventSetDescriptors (Class<?> clazz) {
        return ObjectUtils.getCacheBeanInfo (clazz).getEventSetDescriptors ();
    }

    /**
     * 得到一个实例 object 中的一个 MethodDescriptor 数组。
     *
     * @param object 要得到 MethodDescriptor数组 描述 的 实例对象。
     *
     * @return 合适的 MethodDescriptor数组。
     */
    public static MethodDescriptor[] getMethodDescriptors (Object object) {
        return getMethodDescriptors (object.getClass ());
    }

    /**
     * 得到一个 {@link Class} 中的一个 MethodDescriptor 数组。
     *
     * @param clazz 要得到 MethodDescriptor数组 描述 的 {@link Class}对象。
     *
     * @return 合适的 MethodDescriptor数组。
     */
    public static MethodDescriptor[] getMethodDescriptors (Class<?> clazz) {
        return ObjectUtils.getCacheBeanInfo (clazz).getMethodDescriptors ();
    }

    /**
     * 得到一个实例 object 中的一个 PropertyDescriptor 数组。
     *
     * @param object 要得到 PropertyDescriptor数组 描述 的 实例对象。
     *
     * @return 合适的 PropertyDescriptor数组。
     */
    public static PropertyDescriptor[] getPropertyDescriptors (Object object) {
        return getPropertyDescriptors (object.getClass ());
    }

    /**
     * 得到一个 {@link Class} 中的一个 PropertyDescriptor 数组。
     *
     * @param clazz 要得到 PropertyDescriptor数组 描述 的 {@link Class}对象。
     *
     * @return 合适的 PropertyDescriptor数组。
     */
    public static PropertyDescriptor[] getPropertyDescriptors (Class<?> clazz) {
        return ObjectUtils.getCacheBeanInfo (clazz).getPropertyDescriptors ();
    }

    /**
     * 清除该工具缓存的类信息。
     *
     * @see ObjectUtils#clearCaChes()
     */
    public static void clearCaChes () {
        ObjectUtils.clearCaChes ();
    }

    /**
     * 得到对象 object 中的所有字段（包括公共的、保护的、默认的、私有的以及继承的）。
     *
     * @param object 要得到所有字段信息的对象。
     *
     * @return object 所对应的字段Set。
     */
    public static Set<Field> getAllField (Object object) {
        return getAllField (object.getClass ());
    }

    /**
     * 得到 clazz 中的所有字段（包括公共的、保护的、默认的、私有的以及继承的）。
     *
     * @param clazz 要得到所有字段信息的 {@link Class} 对象。
     *
     * @return clazz 所对应的字段Set。
     */
    public static Set<Field> getAllField (Class<?> clazz) {
        return ObjectUtils.getCacheBeanInfo (clazz).getAllFields ();
    }

    /**
     * 得到对象 object 中的所有接口。
     *
     * @param object 要得到所有接口信息的对象。
     *
     * @return object 所对应的接口数组。
     */
    public static Class<?>[] getAllInterface (Object object) {
        return getAllInterface (object.getClass ());
    }

    /**
     * 得到 clazz 中的所有接口。
     *
     * @param clazz 要得到所有接口。
     *
     * @return clazz 所对应的接口数组。
     */
    public static Class<?>[] getAllInterface (Class<?> clazz) {
        return ObjectUtils.getCacheBeanInfo (clazz).getAllInterfaces ();
    }

    /**
     * 判定指定的方法是否为 {@link Object#equals(Object)} 方法。
     *
     * @param method 要判定的方法。
     *
     * @return 如果 method 代表 equals 方法返回 true ，否则返回 false 。
     */
    public static boolean isEqualsMethod (Method method) {
        if (method == null || !"equals".equals (method.getName ())) {
            return false;
        }
        Class<?>[] paramTypes = method.getParameterTypes ();
        return (paramTypes.length == 1 && paramTypes[0] == Object.class);
    }

    /**
     * 判定指定的方法是否为 {@link Object#hashCode()} 方法。
     *
     * @param method 要判定的方法。
     *
     * @return 如果 method 代表 hashCode 方法返回 true ，否则返回 false 。
     */
    public static boolean isHashCodeMethod (Method method) {
        return (method != null && "hashCode".equals (method.getName ())
                && method.getParameterTypes ().length == 0);
    }

    /**
     * 判定指定的方法是否为 {@link Object#toString()} 方法。
     *
     * @param method 要判定的方法。
     *
     * @return 如果 method 代表 toString 方法返回 true ，否则返回 false 。
     */
    public static boolean isToStringMethod (Method method) {
        return (method != null && "toString".equals (method.getName ())
                && method.getParameterTypes ().length == 0);
    }

    /**
     * 判定指定的方法是否为 {@link Object#getClass()} 方法。
     *
     * @param method 要判定的方法。
     *
     * @return 如果 method 代表 getClass 方法返回 true ，否则返回 false 。
     */
    public static boolean isGetClassMethod (Method method) {
        return (method != null && "getClass".equals (method.getName ())
                && method.getParameterTypes ().length == 0);
    }

    /**
     * 将此method的 accessible 标志设置为指示的布尔值。值为 true 则指示反射的method在使用时应该取消 Java 语言访问检查。
     * <p>
     * 如果存在安全管理器,则在启用特权的情况下执行 {@link Method#setAccessible(boolean)}。
     *
     * @param method 要将 accessible 设置为 true的 Method。
     *
     * @see AccessController#doPrivileged(PrivilegedAction)
     */
    public static void methodSetAccessible (Method method) {
        setAccessible (method);
    }

    /**
     * 将此field的 accessible 标志设置为指示的布尔值。值为 true 则指示反射的field在使用时应该取消 Java 语言访问检查。
     * <p>
     * 如果存在安全管理器,则在启用特权的情况下执行 {@link Field#setAccessible(boolean)}。
     *
     * @param field 要将 accessible 设置为 true的 Field。
     *
     * @see AccessController#doPrivileged(PrivilegedAction)
     */
    public static void fieldSetAccessible (Field field) {
        setAccessible (field);
    }

    /**
     * 将此constructor的 accessible 标志设置为指示的布尔值。值为 true 则指示反射的constructor在使用时应该取消 Java 语言访问检查。
     * <p>
     * 如果存在安全管理器,则在启用特权的情况下执行 {@link Constructor#setAccessible(boolean)}。
     *
     * @param constructor 要将 accessible 设置为 true的 Constructor。
     *
     * @see AccessController#doPrivileged(PrivilegedAction)
     */
    public static void constructorSetAccessible (Constructor<?> constructor) {
        setAccessible (constructor);
    }

    private static void setAccessible (final AccessibleObject accessibleObject) {
        if (!accessibleObject.isAccessible ()) {
            if (System.getSecurityManager () != null) {
                AccessController.doPrivileged (new PrivilegedAction<Object> () {
                    @Override
                    public Object run () {
                        accessibleObject.setAccessible (true);
                        return null;
                    }
                });
            } else {
                accessibleObject.setAccessible (true);
            }
        }
    }
}
