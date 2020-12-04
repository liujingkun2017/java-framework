
package org.liujk.java.framework.base.utils.lang;


import org.liujk.java.framework.base.exceptions.RunException;
import org.liujk.java.framework.base.utils.lang.beans.reference.*;
import org.liujk.java.framework.base.utils.lang.collection.ArrayWrapper;
import org.springframework.util.Assert;

import java.awt.*;
import java.beans.*;
import java.io.*;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;

/**
 * 说明：
 * <p>
 * 对象工具。 该工具会尽可能的缓存解析过的类信息以减小反射带来的性能消耗。
 * <p>
 * 如果没有特别说明，向该类的方法传递 null 都会抛出 {@link NullPointerException}
 *
 */
public class ObjectUtils {
    /**
     * 空 {@link Object} 数组
     */
    public static final Object[] EMPTY_OBJECTS = new Object[0];
    /**
     * 用于表示<code>null</code>的常量。
     * <p>
     * <p>
     * 例如，<code>HashMap.get(Object)</code>方法返回<code>null</code>有两种可能： 值不存在或值为
     * <code>null</code>。而这个singleton可用来区别这两种情形。
     * </p>
     * <p>
     * <p>
     * 另一个例子是，<code>Hashtable</code>的值不能为<code>null</code>。
     * </p>
     */
    public static final Object NULL = new Serializable() {
        private static final long serialVersionUID = 7092611880189329093L;

        private Object readResolve() {
            return NULL;
        }
    };
    /**
     * 缓存的 map
     */
    private static final Map<Class<?>, CacheBeanInfo<?>> CACHE_BEAN_INFO
            = new ConcurrentReferenceMap<Class<?>, CacheBeanInfo<?>>(
            ReferenceKeyType.WEAK, ReferenceValueType.SOFT, 1024);

    /**
     * 比较 o1 与 o2 是否相等。
     * <p>
     * 如果 o1 与 o2 其中一个为 null ，则使用 == 比较，否则使用 {@link Object#equals(Object)} 比较。
     * <p>
     * 对于数组，则使用 {@link Arrays} 里的方法比较。
     *
     * @param o1 一个对象。
     * @param o2 一个对象。
     * @return 如果 o1 与 o2 相等返回 true ，否则返回 false 。
     */
    public static boolean isEquals(Object o1, Object o2) {
        if (o1 == null) {
            if (null == o2) {
                return true;
            }
        } else {
            if (o1.equals(o2)) {
                return true;
            }
            if (o2 != null) {
                if (o1.getClass().isArray() && o2.getClass().isArray()) {
                    if (o1 instanceof Object[] && o2 instanceof Object[]) {
                        return Arrays.equals((Object[]) o1, (Object[]) o2);
                    }
                    if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
                        return Arrays.equals((boolean[]) o1, (boolean[]) o2);
                    }
                    if (o1 instanceof byte[] && o2 instanceof byte[]) {
                        return Arrays.equals((byte[]) o1, (byte[]) o2);
                    }
                    if (o1 instanceof char[] && o2 instanceof char[]) {
                        return Arrays.equals((char[]) o1, (char[]) o2);
                    }
                    if (o1 instanceof double[] && o2 instanceof double[]) {
                        return Arrays.equals((double[]) o1, (double[]) o2);
                    }
                    if (o1 instanceof float[] && o2 instanceof float[]) {
                        return Arrays.equals((float[]) o1, (float[]) o2);
                    }
                    if (o1 instanceof int[] && o2 instanceof int[]) {
                        return Arrays.equals((int[]) o1, (int[]) o2);
                    }
                    if (o1 instanceof long[] && o2 instanceof long[]) {
                        return Arrays.equals((long[]) o1, (long[]) o2);
                    }
                    if (o1 instanceof short[] && o2 instanceof short[]) {
                        return Arrays.equals((short[]) o1, (short[]) o2);
                    }
                }
            }
        }
        return false;
    }

    /**
     * 清除该工具缓存的类信息。
     */
    public static final void clearCaChes() {
        synchronized (CACHE_BEAN_INFO) {
            List<CacheBeanInfo<?>> beanInfos = new ArrayList<CacheBeanInfo<?>>(
                    CACHE_BEAN_INFO.values());
            for (CacheBeanInfo<?> cacheBeanInfo : beanInfos) {
                cacheBeanInfo.clear();
            }
        }
    }

    /**
     * 在 Java Bean 上进行内省，了解其所有属性、公开的方法和事件。如果 Java Bean 的 BeanInfo 类以前已经被内省，则从 BeanInfo 缓存中检索
     * BeanInfo 类。
     *
     * @param clazz 将要分析的 bean 类。
     * @return 描述目标 bean 的 BeanInfo 对象。
     */
    public static <T> BeanInformation<T> getBeanInfo(Class<T> clazz) {
        return getCacheBeanInfo(clazz);
    }

    /**
     * 在 Java Bean 上进行内省，了解其所有属性、公开的方法和事件。如果 Java Bean 的 BeanInfo 类以前已经被内省，则从 BeanInfo 缓存中检索
     * BeanInfo 类。
     *
     * @param object 将要分析的 对象。
     * @return 描述目标 对象 的 BeanInfo 对象。
     */
    @SuppressWarnings("unchecked")
    public static <T> BeanInformation<T> getBeanInfo(T object) {
        if (object == null) {
            return null;
        }
        return (BeanInformation<T>) getBeanInfo(object.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <T> CacheBeanInfo<T> getCacheBeanInfo(Class<T> clazz) {
        // 从缓存中取
        CacheBeanInfo<T> beanInfo = (CacheBeanInfo<T>) CACHE_BEAN_INFO.get(clazz);
        if (beanInfo == null) {
            synchronized (CACHE_BEAN_INFO) {
                if ((beanInfo = (CacheBeanInfo<T>) CACHE_BEAN_INFO.get(clazz)) == null) {
                    // 如果缓存中没有则内省
                    try {
                        beanInfo = new CacheBeanInfo<T>(clazz, Introspector.getBeanInfo(clazz));
                        // 加入缓存
                        CACHE_BEAN_INFO.put(clazz, beanInfo);
                        // 清除系统内省的缓存
                        Introspector.flushCaches();
                    } catch (IntrospectionException e) {
                        throw new RunException("解析对象信息时发生异常", e);
                    }
                }
            }
        }
        return beanInfo;
    }

    /**
     * 序列化对象obj并且返回对应的字节数组。
     *
     * @param obj 需要序列化的对象。
     * @return obj序列化后的字节数组。
     * @throws IOException 发送I/O异常时。
     */
    public static byte[] serialObject(Object obj) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(64);
        ObjectOutputStream out = new ObjectOutputStream(stream);
        out.writeObject(obj);
        return stream.toByteArray();
    }

    /**
     * 反序列化对象字节数组为对象。
     *
     * @param objectBytes
     * @param objectBytes 对象类型。
     * @return 反序列化得到的对象。
     * @throws IOException            发送I/O错误。
     * @throws ClassNotFoundException 如果没有找到对象的类。
     */
    public static Object deserialObject(byte[] objectBytes)
            throws IOException, ClassNotFoundException {
        if (objectBytes == null) {
            return null;
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(objectBytes);
        ObjectInputStream in = new ObjectInputStream(stream);
        return in.readObject();
    }

    /**
     * 反序列化对象字节数组为对象。
     *
     * @param objectBytes
     * @param type        对象类型。
     * @return 反序列化得到的对象。
     * @throws IllegalArgumentException 如果 type 为 null 。
     * @throws IOException              发送I/O错误。
     * @throws ClassNotFoundException   如果没有找到对象的类。
     * @throws ClassCastException       如果类型转换失败。
     */
    public static <T> T deserialObject(byte[] objectBytes, Class<T> type)
            throws IOException, ClassNotFoundException {
        Assert.notNull(type, "{type}不能为'null'。");
        return type.cast(deserialObject(objectBytes));
    }

    /*------------------------------------------------------------------------------------
     *                         以下是ObjectUtil 的代码复制合并，新增方法请在此注释之上                                             -
     * ----------------------------------------------------------------------------------*/

    /*
     * ========================================================================== ==
     */
    /* 常量和singleton。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 如果对象为<code>null</code>，则返回指定默认对象，否则返回对象本身。
     * <p>
     *
     * <pre>
     * ObjectUtil.defaultIfNull(null, null)      = null
     * ObjectUtil.defaultIfNull(null, "")        = ""
     * ObjectUtil.defaultIfNull(null, "zz")      = "zz"
     * ObjectUtil.defaultIfNull("abc", *)        = "abc"
     * ObjectUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     *
     * @param object       要测试的对象
     * @param defaultValue 默认值
     * @return 对象本身或默认对象
     */
    public static Object defaultIfNull(Object object, Object defaultValue) {
        return (object != null) ? object : defaultValue;
    }

    /*
     * ========================================================================== ==
     */
    /* 默认值函数。 */
    /*                                                                              */
    /* 当对象为null时，将对象转换成指定的默认对象。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 比较两个对象是否完全相等。
     * <p>
     * <p>
     * 此方法可以正确地比较多维数组。
     * <p>
     *
     * <pre>
     * ObjectUtil.equals(null, null)                  = true
     * ObjectUtil.equals(null, "")                    = false
     * ObjectUtil.equals("", null)                    = false
     * ObjectUtil.equals("", "")                      = true
     * ObjectUtil.equals(Boolean.TRUE, null)          = false
     * ObjectUtil.equals(Boolean.TRUE, "true")        = false
     * ObjectUtil.equals(Boolean.TRUE, Boolean.TRUE)  = true
     * ObjectUtil.equals(Boolean.TRUE, Boolean.FALSE) = false
     * </pre>
     * <p>
     * </p>
     *
     * @param object1 对象1
     * @param object2 对象2
     * @return 如果相等, 则返回<code>true</code>
     */
    public static boolean equals(Object object1, Object object2) {
        return ObjectUtils.isEquals(object1, object2);
    }

    /*
     * ========================================================================== ==
     */
    /* 比较函数。 */
    /*                                                                              */
    /* 以下方法用来比较两个对象是否相同。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 取得对象的hash值, 如果对象为<code>null</code>, 则返回<code>0</code>。
     * <p>
     * <p>
     * 此方法可以正确地处理多维数组。
     * </p>
     *
     * @param object 对象
     * @return hash值
     */
    public static int hashCode(Object object) {
        return ArrayUtils.hashCode(object);
    }

    /*
     * ========================================================================== ==
     */
    /* Hashcode函数。 */
    /*                                                                              */
    /* 以下方法用来取得对象的hash code。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 取得对象的原始的hash值, 如果对象为<code>null</code>, 则返回<code>0</code>。
     * <p>
     * <p>
     * 该方法使用<code>System.identityHashCode</code>来取得hash值，该值不受对象本身的 <code>hashCode</code>方法的影响。
     * </p>
     *
     * @param object 对象
     * @return hash值
     */
    public static int identityHashCode(Object object) {
        return (object == null) ? 0 : System.identityHashCode(object);
    }

    /**
     * 取得对象自身的identity，如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出。
     * <p>
     *
     * <pre>
     * ObjectUtil.identityToString(null)          = null
     * ObjectUtil.identityToString("")            = "java.lang.String@1e23"
     * ObjectUtil.identityToString(Boolean.TRUE)  = "java.lang.Boolean@7fa"
     * ObjectUtil.identityToString(new int[0])    = "int[]@7fa"
     * ObjectUtil.identityToString(new Object[0]) = "java.lang.Object[]@7fa"
     * </pre>
     *
     * @param object 对象
     * @return 对象的identity，如果对象是<code>null</code>，则返回<code>null</code>
     */
    public static String identityToString(Object object) {
        if (object == null) {
            return null;
        }

        return appendIdentityToString(null, object).toString();
    }

    /*
     * ========================================================================== ==
     */
    /* 取得对象的identity。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 取得对象自身的identity，如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出。
     * <p>
     *
     * <pre>
     * ObjectUtil.identityToString(null, "NULL")            = "NULL"
     * ObjectUtil.identityToString("", "NULL")              = "java.lang.String@1e23"
     * ObjectUtil.identityToString(Boolean.TRUE, "NULL")    = "java.lang.Boolean@7fa"
     * ObjectUtil.identityToString(new int[0], "NULL")      = "int[]@7fa"
     * ObjectUtil.identityToString(new Object[0], "NULL")   = "java.lang.Object[]@7fa"
     * </pre>
     *
     * @param object  对象
     * @param nullStr 如果对象为<code>null</code>，则返回该字符串
     * @return 对象的identity，如果对象是<code>null</code>，则返回指定字符串
     */
    public static String identityToString(Object object, String nullStr) {
        if (object == null) {
            return nullStr;
        }

        return appendIdentityToString(null, object).toString();
    }

    /**
     * 将对象自身的identity——如同对象没有覆盖<code>toString()</code>方法时，
     * <code>Object.toString()</code>的原始输出——追加到<code>StringBuffer</code>中。
     * <p>
     *
     * <pre>
     * ObjectUtil.appendIdentityToString(*, null)            = null
     * ObjectUtil.appendIdentityToString(null, "")           = "java.lang.String@1e23"
     * ObjectUtil.appendIdentityToString(null, Boolean.TRUE) = "java.lang.Boolean@7fa"
     * ObjectUtil.appendIdentityToString(buf, Boolean.TRUE)  = buf.append("java.lang.Boolean@7fa")
     * ObjectUtil.appendIdentityToString(buf, new int[0])    = buf.append("int[]@7fa")
     * ObjectUtil.appendIdentityToString(buf, new Object[0]) = buf.append("java.lang.Object[]@7fa")
     * </pre>
     *
     * @param buffer <code>StringBuffer</code>对象，如果是<code>null</code>，则创建新的
     * @param object 对象
     * @return <code>StringBuffer</code>对象，如果对象为<code>null</code>，则返回 <code>null</code>
     */
    public static StringBuffer appendIdentityToString(StringBuffer buffer, Object object) {
        if (object == null) {
            return null;
        }

        if (buffer == null) {
            buffer = new StringBuffer();
        }

        buffer.append(ClassUtils.getClassNameForObject(object));

        return buffer.append('@').append(Integer.toHexString(identityHashCode(object)));
    }

    /**
     * 复制一个对象。如果对象为<code>null</code>，则返回<code>null</code>。
     * <p>
     * <p>
     * 此方法调用<code>Object.clone</code>方法，默认只进行“浅复制”。 对于数组，调用 <code>ArrayUtil.clone</code>方法更高效。
     * </p>
     *
     * @param array 要复制的数组
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     * @throws CloneNotSupportedException
     */
    @SuppressWarnings("rawtypes")
    public static Object clone(Object array) throws CloneNotSupportedException {
        if (array == null) {
            return null;
        }

        // 对数组特殊处理
        if (array instanceof Object[]) {
            return ArrayUtils.clone((Object[]) array);
        }

        if (array instanceof long[]) {
            return ArrayUtils.clone((long[]) array);
        }

        if (array instanceof int[]) {
            return ArrayUtils.clone((int[]) array);
        }

        if (array instanceof short[]) {
            return ArrayUtils.clone((short[]) array);
        }

        if (array instanceof byte[]) {
            return ArrayUtils.clone((byte[]) array);
        }

        if (array instanceof double[]) {
            return ArrayUtils.clone((double[]) array);
        }

        if (array instanceof float[]) {
            return ArrayUtils.clone((float[]) array);
        }

        if (array instanceof boolean[]) {
            return ArrayUtils.clone((boolean[]) array);
        }

        if (array instanceof char[]) {
            return ArrayUtils.clone((char[]) array);
        }

        // Not cloneable
        if (!(array instanceof Cloneable)) {
            throw new CloneNotSupportedException(
                    "Object of class " + array.getClass().getName() + " is not Cloneable");
        }

        // 用reflection调用clone方法
        Class clazz = array.getClass();

        try {
            @SuppressWarnings("unchecked")
            Method cloneMethod = clazz.getMethod("clone", ClassUtils.EMPTY_CLASSES);

            return cloneMethod.invoke(array, ObjectUtils.EMPTY_OBJECTS);
        } catch (NoSuchMethodException e) {
            throw new CloneNotSupportedException(e + "");
        } catch (IllegalArgumentException e) {
            throw new CloneNotSupportedException(e + "");
        } catch (IllegalAccessException e) {
            throw new CloneNotSupportedException(e + "");
        } catch (InvocationTargetException e) {
            throw new CloneNotSupportedException(e + "");
        }
    }

    /*
     * ========================================================================== ==
     */
    /* Clone函数。 */
    /*                                                                              */
    /* 以下方法调用Object.clone方法，默认是“浅复制”（shallow copy）。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 检查两个对象是否属于相同类型。<code>null</code>将被看作任意类型。
     *
     * @param object1 对象1
     * @param object2 对象2
     * @return 如果两个对象有相同的类型，则返回<code>true</code>
     */
    public static boolean isSameType(Object object1, Object object2) {
        if ((object1 == null) || (object2 == null)) {
            return true;
        }

        return object1.getClass().equals(object2.getClass());
    }

    /*
     * ========================================================================== ==
     */
    /* 比较对象的类型。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 取得对象的<code>toString()</code>的值，如果对象为<code>null</code>，则返回空字符串 <code>""</code>。
     * <p>
     *
     * <pre>
     * ObjectUtil.toString(null)         = ""
     * ObjectUtil.toString("")           = ""
     * ObjectUtil.toString("bat")        = "bat"
     * ObjectUtil.toString(Boolean.TRUE) = "true"
     * ObjectUtil.toString([1, 2, 3])    = "[1, 2, 3]"
     * </pre>
     *
     * @param object 对象
     * @return 对象的<code>toString()</code>的返回值，或空字符串<code>""</code>
     */
    public static String toString(Object object) {
        return (object == null) ? StringUtils.EMPTY_STRING
                : (object.getClass().isArray() ? ArrayUtils.toString(object) : object.toString());
    }

    /*
     * ========================================================================== ==
     */
    /* toString方法。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 取得对象的<code>toString()</code>的值，如果对象为<code>null</code>，则返回指定字符串。
     * <p>
     *
     * <pre>
     * ObjectUtil.toString(null, null)           = null
     * ObjectUtil.toString(null, "null")         = "null"
     * ObjectUtil.toString("", "null")           = ""
     * ObjectUtil.toString("bat", "null")        = "bat"
     * ObjectUtil.toString(Boolean.TRUE, "null") = "true"
     * ObjectUtil.toString([1, 2, 3], "null")    = "[1, 2, 3]"
     * </pre>
     *
     * @param object  对象
     * @param nullStr 如果对象为<code>null</code>，则返回该字符串
     * @return 对象的<code>toString()</code>的返回值，或指定字符串
     */
    public static String toString(Object object, String nullStr) {
        return (object == null) ? nullStr
                : (object.getClass().isArray() ? ArrayUtils.toString(object) : object.toString());
    }

    /**
     * 断言对象不能为空
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> T notNull(T obj) {
        return notNull(obj, null);
    }

    public static <T> T notNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }

    /**
     * 用做缓存的 BeanInfo。
     */
    static class CacheBeanInfo<T> implements BeanInformation<T> {

        private final BeanInfo beanInfo;

        private final ClassReference clazzReference;

        private final Map<String, PropertyDescriptor> propertyDescriptorMap
                = new HashMap<String, PropertyDescriptor>();
        private final Map<String, Map<ArrayWrapper, MethodDescriptor>> methodDescriptorMap
                = new HashMap<String, Map<ArrayWrapper, MethodDescriptor>>();
        private final Map<String, EventSetDescriptor> eventSetDescriptorMap
                = new HashMap<String, EventSetDescriptor>();
        private final Set<Field> fields;
        private final Reference<Class<?>>[] interfaceReferences;
        private volatile Reference<Class<?>[]> interfaces;

        @SuppressWarnings("unchecked")
        CacheBeanInfo(Class<T> clazz, BeanInfo beanInfo) {
            this.beanInfo = beanInfo;
            this.clazzReference = new ClassReference(clazz);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            if (propertyDescriptors != null) {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    this.propertyDescriptorMap.put(propertyDescriptor.getName(),
                            propertyDescriptor);
                }
            }
            MethodDescriptor[] methodDescriptors = beanInfo.getMethodDescriptors();
            if (methodDescriptors != null) {
                for (MethodDescriptor methodDescriptor : methodDescriptors) {
                    Map<ArrayWrapper, MethodDescriptor> mdm = this.methodDescriptorMap
                            .get(methodDescriptor.getName());
                    if (mdm == null) {
                        mdm = new HashMap<ArrayWrapper, MethodDescriptor>();
                        this.methodDescriptorMap.put(methodDescriptor.getName(), mdm);
                    }
                    mdm.put(new ArrayWrapper(
                                    ClassUtils.toStrings(methodDescriptor.getMethod().getParameterTypes())),
                            methodDescriptor);
                }
            }
            EventSetDescriptor[] eventSetDescriptors = beanInfo.getEventSetDescriptors();
            if (eventSetDescriptors != null) {
                for (EventSetDescriptor eventSetDescriptor : eventSetDescriptors) {
                    this.eventSetDescriptorMap.put(eventSetDescriptor.getName(),
                            eventSetDescriptor);
                }
            }
            Set<Field> fields = new HashSet<Field>();
            allField(clazz, fields);
            this.fields = Collections.unmodifiableSet(fields);
            Set<Reference<Class<?>>> interfaces = new HashSet<Reference<Class<?>>>();
            allInterface(clazz, interfaces);
            this.interfaceReferences = (Reference<Class<?>>[]) interfaces
                    .toArray(new Reference<?>[interfaces.size()]);
        }

        private static void allField(Class<?> clazz, Set<Field> fields) {
            if (clazz == null) {
                return;
            }
            // 添加自己的 Field
            for (Field field : clazz.getDeclaredFields()) {
                fields.add(field);
            }
            // 解析父类的 Field
            allField(clazz.getSuperclass(), fields);
        }

        private static void allInterface(Class<?> clazz,
                                         Set<Reference<Class<?>>> interfaceClasses) {
            if (clazz == null) {
                return;
            }
            // 添加自己的 interface
            for (Class<?> interfaceClass : clazz.getInterfaces()) {
                interfaceClasses.add(new WeakReference<Class<?>>(interfaceClass));
            }
            // 解析父类的 interface
            allInterface(clazz.getSuperclass(), interfaceClasses);
        }

        synchronized void clear() {
            CACHE_BEAN_INFO.remove(this.clazzReference.get());
        }

        @Override
        public Set<Field> getAllFields() {
            return this.fields;
        }

        public Class<?>[] getAllInterfaces() {
            Class<?>[] classes;
            if (this.interfaces == null || (classes = this.interfaces.get()) == null) {
                classes = new Class<?>[this.interfaceReferences.length];
                for (int i = 0; i < classes.length; i++) {
                    classes[i] = this.interfaceReferences[i].get();
                }
                this.interfaces = new SoftReference<Class<?>[]>(classes);
            }
            return classes;
        }

        @Override
        public EventSetDescriptor getEventSetDescriptor(String name) {
            return this.eventSetDescriptorMap.get(name);
        }

        @Override
        public PropertyDescriptor getPropertyDescriptor(String name) {
            return this.propertyDescriptorMap.get(name);
        }

        @Override
        public MethodDescriptor getMethodDescriptor(String name, Class<?>... classes) {
            Map<ArrayWrapper, MethodDescriptor> mdm = this.methodDescriptorMap.get(name);
            if (mdm == null) {
                return null;
            }
            String[] classStrArray = ClassUtils.toStrings(classes);
            if (classStrArray == null) {
                return mdm.get(null);
            }
            return mdm.get(new ArrayWrapper(classStrArray));
        }

        @Override
        public BeanInfo[] getAdditionalBeanInfo() {
            return this.beanInfo.getAdditionalBeanInfo();
        }

        @Override
        public BeanDescriptor getBeanDescriptor() {
            return this.beanInfo.getBeanDescriptor();
        }

        @Override
        public int getDefaultEventIndex() {
            return this.beanInfo.getDefaultEventIndex();
        }

        @Override
        public int getDefaultPropertyIndex() {
            return this.beanInfo.getDefaultPropertyIndex();
        }

        @Override
        public EventSetDescriptor[] getEventSetDescriptors() {
            return this.beanInfo.getEventSetDescriptors();
        }

        @Override
        public Image getIcon(int iconKind) {
            return this.beanInfo.getIcon(iconKind);
        }

        @Override
        public MethodDescriptor[] getMethodDescriptors() {
            return this.beanInfo.getMethodDescriptors();
        }

        @Override
        public PropertyDescriptor[] getPropertyDescriptors() {
            return this.beanInfo.getPropertyDescriptors();
        }

        /**
         * 将 {@link Class} 弱引用的包装器，防止 {@link Class} 保持强引用而不被回收，并且在回收 {@link Class} 会清空缓存中的记录。
         */
        class ClassReference extends FinalizableWeakReference<Class<T>> {

            ClassReference(Class<T> referent) {
                super(referent);
            }

            @Override
            public void finalizeReferent() {
                synchronized (CACHE_BEAN_INFO) {
                    CACHE_BEAN_INFO.values().remove(CacheBeanInfo.this);
                }
            }
        }

    }
}
