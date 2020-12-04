
package org.liujk.java.framework.base.utils.lang.beans.reference;



import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.lang.ReflectionUtils;
import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.base.utils.lang.beans.converter.TypeConverter;
import org.liujk.java.framework.base.utils.lang.beans.converter.TypeConverterManager;
import org.liujk.java.framework.base.utils.lang.beans.converter.TypeConverterUtils;
import org.liujk.java.framework.base.utils.lang.beans.property.PropertyAccessor;
import org.liujk.java.framework.base.utils.lang.beans.property.PropertyWrapper;
import org.liujk.java.framework.base.utils.lang.beans.property.PropertyWrapperProvider;
import org.liujk.java.framework.base.utils.lang.beans.property.SimplePropertyWrapperProvider;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 说明：
 * <p>
 * BeanWrapper 的标准实现。
 * <p>
 * 该包装器默认使用 {@link SimpleFieldTypeScannerProvider} 提供的类型搜索器来查找泛型的参数类型,使用
 * {@link SimplePropertyWrapperProvider} 提供的属性包装器来包装属性,使用
 * {@link TypeConverterUtils#getTypeConverterManager()} 得到的类型转换器管理器来完成类型转换。
 * <p>
 * 在使用 {@link #setPropertyValue(String, Object)}
 * 为路径表达式指定的属性写入值时，如果遇到表达式中指定的属性值为null（即非表达式最后一段的值为null），则会使用
 * {@link PropertyAccessor#propertyNewInstance()} 创建实例并写入。
 * <p>
 * 该实现所使用的路径表达式规则：属性之间用'.'分割，如果属性为List、数组、Map等类型，那么List和数组的下标、Map的键用'.'分割。
 * 如果map中的键出现了'.'，则可以使用'[]'包裹键，这时该键无需使用'.'分割。List和数组的下标也可以使用'[]'
 * <p>
 *
 * <pre>
 * 被包装的bean的类如下：
 * <code>
 * public class Bean {
 *     private List&lt;Map&lt;String, Bean&gt;&gt; beans;
 *
 *     private String name;
 *
 *     // getter setter
 * }
 * </code>
 * bean包装器如下：
 * <code>
 * StanderdBeanWrapper beanWrapper = new StanderdBeanWrapper(new Bean());
 * </code>
 * 那么：
 * <ul>
 * <li><code>beanWrapper.setPropertyValue("beans[0]['bean1']name", "simpleBean");</code>
 * beans[0]表示Bean的beans属性（这里是个List）的第0个下标的值，['bean1']接着表示该值中key为bean1的值（由泛型可知beans[0]为一个Map），
 * name表示该值的name属性（同样由泛型可知该值为Bean类型）。整个方法结合表达式就是将被包装的bean里的List类型beans属性中的下标为0的Map类型
 * 属性的bean1这个key对应的Bean的实例的name属性值设置为simpleBean。
 * <li><code>beanWrapper.setPropertyValue("beans.0.bean1.name", "simpleBean");</code>
 * 同上，只是全部以'.'分割路径。
 * </li>
 * </ul>
 * </pre>
 *
 */
public class StandardBeanWrapper implements BeanWrapper {

    private static final Pattern EXPRESSION_TYPE_RULE = Pattern
            .compile ("(?:\\[?[\"']((?:(?![^\\\\][\"']\\]).)*[^\\\\])[\"']\\])|"
                              + "(?:\\[[\"]((?:(?![\"]\\]).)*)[\"\']\\])|"
                              + "(?:\\[[\']((?:(?![\']\\]).)*)[\']\\])|" + "(?:([^\\.\\[\\]]+)[\\.\\]\\[]?)|"
                              + "(?:([^\\.\\[\\]]*)[\\.\\]\\[])");

    private static final String WILDCARDS = "*";

    private static final String ESCAPE = "\\";

    private static final String ASTERISK = ESCAPE
            + WILDCARDS;

    private static final FieldTypeScannerProvider DEFAULT_FIELD_TYPE_SCANNER_PROVIDER
            = new SimpleFieldTypeScannerProvider ();

    private static final PropertyWrapperProvider DEFAULT_PROPERTY_WRAPPER_PROVIDER
            = new SimplePropertyWrapperProvider ();

    private static final PropertyAccessCallback PROPERTY_ACCESS_CALLBACK = new PropertyAccessCallbackImpl ();
    final boolean isCache;
    private final Object bean;
    private final ConcurrentMap<String, List<PropertyWrapper>> propertyWrapperCache;
    private FieldTypeScannerProvider fieldTypeScannerProvider = DEFAULT_FIELD_TYPE_SCANNER_PROVIDER;
    private PropertyWrapperProvider propertyWrapperProvider = DEFAULT_PROPERTY_WRAPPER_PROVIDER;
    private TypeConverterManager typeConverterManager = TypeConverterUtils
            .getTypeConverterManager ();
    private Class<?> defaultType;

    /**
     * 构造一个 StanderdBeanWrapper 。
     * <p>
     * 通过该构造方法创建的包装器会缓存解析过的属性路径表达式对应的结果。
     *
     * @param bean 需要被包装的 bean 的实例。
     */
    public StandardBeanWrapper (Object bean) {
        this (bean, true);
    }

    /**
     * 使用指定的缓存规则构造一个 StanderdBeanWrapper 。
     *
     * @param bean    需要被包装的 bean 的实例。
     * @param isCache 如果为 true 表示该实例会缓存解析过的属性路径表达式对应的结果。
     */
    public StandardBeanWrapper (Object bean, boolean isCache) {
        this.bean = bean;
        if (isCache) {
            this.propertyWrapperCache = new ConcurrentHashMap<String, List<PropertyWrapper>> ();
        } else {
            this.propertyWrapperCache = null;
        }
        this.isCache = isCache;
    }

    static List<String> parseParameterName (String propertyPath) {
        List<String> parameterNames = new ArrayList<String> ();
        Matcher matcher = EXPRESSION_TYPE_RULE.matcher (propertyPath);
        while (matcher.find ()) {
            addParameterName (parameterNames, matcher.group (1));
            addParameterName (parameterNames, matcher.group (2));
            addParameterName (parameterNames, matcher.group (3));
            addParameterName (parameterNames, matcher.group (4));
        }
        return parameterNames;
    }

    private static void addParameterName (List<String> parameterNames, String name) {
        if (name != null) {
            parameterNames.add (name);
        }
    }

    /**
     * 设置无法得知类型的时候使用的默认类型。
     *
     * @param defaultType 无法得知类型的时候使用的默认类型。
     */
    public void setDefaultType (Class<?> defaultType) {
        this.defaultType = defaultType;
    }

    @Override
    public Object getWrappedInstance () {
        return this.bean;
    }

    @Override
    public PropertyWrapper getPropertyWrapper (String propertyName) {
        List<PropertyWrapper> propertyWrappers = getPropertyWrappers (propertyName);
        if (propertyWrappers.isEmpty ()) {
            return null;
        }
        return propertyWrappers.get (0);
    }

    @Override
    public List<PropertyWrapper> getPropertyWrappers (String propertyPath) {
        if (StringUtils.isEmpty (propertyPath)) {
            return new ArrayList<PropertyWrapper> (0);
        }
        List<PropertyWrapper> propertyWrappers = null;
        if (this.isCache) {
            propertyWrappers = this.propertyWrapperCache.get (propertyPath);
        }
        if (propertyWrappers == null) {
            List<String> propertyNames = parseParameterName (propertyPath);
            if (propertyNames.isEmpty ()) {
                return new ArrayList<PropertyWrapper> (0);
            }
            Class<?> beanType = getWrappedInstance ().getClass ();
            FieldTypeScanner fieldTypeScanner = null;
            if (!(beanType.isArray () || List.class.isAssignableFrom (beanType)
                    || Collection.class.isAssignableFrom (beanType)
                    || Map.class.isAssignableFrom (beanType))) {
                PropertyDescriptor propertyDescriptor = getPropertyDescriptor (
                        getWrappedInstance ().getClass (), propertyNames.get (0));
                if (propertyDescriptor == null) {
                    return new ArrayList<PropertyWrapper> (0);
                }
                FieldTypeScanner.Mode mode = FieldTypeScanner.Mode.SETTER_METHOD;
                Method method = propertyDescriptor.getWriteMethod ();
                if (method == null) {
                    method = propertyDescriptor.getReadMethod ();
                    mode = FieldTypeScanner.Mode.GETTER_METHOD;
                }
                if (method == null) {
                    return new ArrayList<PropertyWrapper> (0);
                }
                fieldTypeScanner = this.fieldTypeScannerProvider.newFieldTypeScanner (
                        getWrappedInstance ().getClass (), mode, method, this.defaultType);
            }
            propertyWrappers = new ArrayList<PropertyWrapper> (3);
            Object currentBean = getWrappedInstance ();
            Object nextBean = null;
            Class<?> currentBeanType = getWrappedInstance ().getClass ();
            boolean skipScanner = true;
            for (int i = 0; i < propertyNames.size (); i++) {
                PropertyWrapper propertyWrapper;
                if (fieldTypeScanner == null && currentBeanType.isArray ()) {
                    Object[] t = getArrayPropertyWrapper (propertyNames.get (i), currentBean,
                                                          currentBeanType);
                    nextBean = (t == null ? null : t[0]);
                    propertyWrapper = (PropertyWrapper) (t == null ? null : t[1]);
                    skipScanner = false;
                } else if (fieldTypeScanner == null
                        && List.class.isAssignableFrom (currentBeanType)) {
                    Object[] t = getListPropertyWrapper (propertyNames.get (i), currentBean,
                                                         currentBeanType);
                    nextBean = (t == null ? null : t[0]);
                    propertyWrapper = (PropertyWrapper) (t == null ? null : t[1]);
                    skipScanner = false;
                } else if (fieldTypeScanner == null
                        && Collection.class.isAssignableFrom (currentBeanType)) {
                    Object[] t = getCollectionPropertyWrapper (propertyNames.get (i), currentBean,
                                                               currentBeanType);
                    nextBean = (t == null ? null : t[0]);
                    propertyWrapper = (PropertyWrapper) (t == null ? null : t[1]);
                    skipScanner = false;
                } else if (fieldTypeScanner == null
                        && Map.class.isAssignableFrom (currentBeanType)) {
                    Object[] t = getMapPropertyWrapper (propertyNames.get (i), currentBean,
                                                        currentBeanType);
                    nextBean = (t == null ? null : t[0]);
                    propertyWrapper = (PropertyWrapper) (t == null ? null : t[1]);
                    skipScanner = false;
                } else if (skipScanner) {
                    propertyWrapper = this.propertyWrapperProvider.newPropertyWrapper (
                            currentBeanType, propertyNames.get (i), null, null, null);
                    nextBean = getNextBean (currentBeanType, propertyWrapper);
                    skipScanner = false;
                } else {
                    propertyWrapper = this.propertyWrapperProvider.newPropertyWrapper (
                            currentBeanType, propertyNames.get (i), null, null, fieldTypeScanner);
                    nextBean = getNextBean (currentBeanType, propertyWrapper);
                }
                if (propertyWrapper == null) {
                    return new ArrayList<PropertyWrapper> (0);
                }
                if (fieldTypeScanner != null && fieldTypeScanner.hasNext ()) {
                    // 如果无法扫描出类型，则通过实例得到类型判断
                }
                propertyWrappers.add (propertyWrapper);
                if (fieldTypeScanner == null || !fieldTypeScanner.hasNext ()) {
                    if (i != propertyNames.size () - 1) {
                        PropertyDescriptor pd = getPropertyDescriptor (propertyWrapper.getType (),
                                                                       propertyNames.get (i + 1));
                        if (pd != null) {
                            Method wm = pd.getWriteMethod ();
                            if (wm != null) {
                                if (fieldTypeScanner == null) {
                                    fieldTypeScanner = this.fieldTypeScannerProvider
                                            .newFieldTypeScanner (currentBeanType,
                                                                  FieldTypeScanner.Mode.SETTER_METHOD, wm,
                                                                  this.defaultType);
                                } else {
                                    fieldTypeScanner.rest (FieldTypeScanner.Mode.SETTER_METHOD, wm,
                                                           fieldTypeScanner.getDefaultClass ());
                                }
                            } else {
                                Method rm = pd.getReadMethod ();
                                if (rm != null) {
                                    if (fieldTypeScanner == null) {
                                        fieldTypeScanner = this.fieldTypeScannerProvider
                                                .newFieldTypeScanner (currentBeanType,
                                                                      FieldTypeScanner.Mode.GETTER_METHOD, rm,
                                                                      this.defaultType);
                                    } else {
                                        fieldTypeScanner.rest (FieldTypeScanner.Mode.GETTER_METHOD,
                                                               rm, fieldTypeScanner.getDefaultClass ());
                                    }
                                }
                            }
                        }
                    }
                    skipScanner = true;
                }
                // 该注释后面的 currentBean 和 currentBeanType 为下一次解析的 bean 和 beanType
                currentBeanType = propertyWrapper.getType ();
                if (nextBean != null) {
                    currentBean = nextBean;
                }
            }
        }
        if (this.isCache) {
            this.propertyWrapperCache.putIfAbsent (propertyPath, propertyWrappers);
            return new ArrayList<PropertyWrapper> (propertyWrappers);
        }
        return propertyWrappers;
    }

    private Object getNextBean (Object currentBean, PropertyWrapper propertyWrapper) {
        if (currentBean != null) {
            try {
                return propertyWrapper.createPropertyAccessor (currentBean).get ();
            } catch (UnsupportedOperationException e) {
                // 正常，不处理
                return null;
            } catch (Exception e) {
                // 不处理
                return null;
            }
        }
        return null;
    }

    private Object[] getMapPropertyWrapper (String propertyName, Object currentBean,
                                            Class<?> currentBeanType) {
        PropertyWrapper propertyWrapper;
        Map<?, ?> currentMap = (Map<?, ?>) currentBean;
        if (CollectionUtils.isEmpty (currentMap)) {
            return null;
        }
        Object element = null;
        if (WILDCARDS.equals (propertyName)) {
            element = null;
            for (Object object : currentMap.values ()) {
                if (object != null) {
                    element = object;
                    break;
                }
            }
            propertyWrapper = getPropertyWrapper (propertyName, currentBeanType, element);
        } else {
            String key;
            if (ASTERISK.equals (propertyName)) {
                key = WILDCARDS;
            } else {
                key = propertyName;
            }
            element = currentMap.get (key);
            propertyWrapper = getPropertyWrapper (propertyName, currentBeanType, element);
        }
        return new Object[]{element, propertyWrapper};
    }

    private Object[] getCollectionPropertyWrapper (String propertyName, Object currentBean,
                                                   Class<?> currentBeanType) {
        PropertyWrapper propertyWrapper;
        Collection<?> currentCollection = (Collection<?>) currentBean;
        if (CollectionUtils.isEmpty (currentCollection)) {
            return null;
        }
        Object element = null;
        if (WILDCARDS.equals (propertyName)) {
            element = null;
            for (Object object : currentCollection) {
                if (object != null) {
                    element = object;
                    break;
                }
            }
            propertyWrapper = getPropertyWrapper (propertyName, currentBeanType, element);
        } else {
            return null;
        }
        return new Object[]{element, propertyWrapper};
    }

    private Object[] getArrayPropertyWrapper (String propertyName, Object currentBean,
                                              Class<?> currentBeanType) {
        PropertyWrapper propertyWrapper;
        int length;
        if (currentBean == null || (length = Array.getLength (currentBean)) == 0) {
            return null;
        }
        Object element = null;
        if (WILDCARDS.equals (propertyName)) {
            if (currentBean instanceof Object[]) {
                for (Object object : (Object[]) currentBean) {
                    if (object != null) {
                        element = object;
                        break;
                    }
                }
            } else {
                for (int i = 0; i < length; i++) {
                    Object object = Array.get (currentBean, i);
                    if (object != null) {
                        element = object;
                        break;
                    }
                }
            }
            propertyWrapper = getPropertyWrapper (propertyName, currentBeanType, element);
        } else {
            int index;
            try {
                index = Integer.parseInt (propertyName);
            } catch (NumberFormatException e) {
                return null;
            }
            try {
                element = Array.get (currentBean, index);
            } catch (ArrayIndexOutOfBoundsException e) {
                return null;
            }
            propertyWrapper = getPropertyWrapper (propertyName, currentBeanType, element);
        }
        return new Object[]{element, propertyWrapper};
    }

    private Object[] getListPropertyWrapper (String propertyName, Object currentBean,
                                             Class<?> currentBeanType) {
        PropertyWrapper propertyWrapper;
        List<?> currentList = (List<?>) currentBean;
        if (CollectionUtils.isEmpty (currentList)) {
            return null;
        }
        Object element = null;
        if (WILDCARDS.equals (propertyName)) {
            for (Object object : currentList) {
                if (object != null) {
                    element = object;
                    break;
                }
            }
            propertyWrapper = getPropertyWrapper (propertyName, currentBeanType, element);
        } else {
            int index;
            try {
                index = Integer.parseInt (propertyName);
            } catch (NumberFormatException e) {
                return null;
            }
            try {
                element = currentList.get (index);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
            propertyWrapper = getPropertyWrapper (propertyName, currentBeanType, element);
        }
        return new Object[]{element, propertyWrapper};
    }

    private PropertyWrapper getPropertyWrapper (String propertyName, Class<?> currentBeanType,
                                                Object element) {
        if (element == null) {
            return null;
        }
        return this.propertyWrapperProvider.newPropertyWrapper (currentBeanType, propertyName,
                                                                element.getClass (), null, null);
    }

    @Override
    public boolean setPropertyValue (String propertyPath, Object value) throws Exception {
        return setPropertyValue0 (propertyPath, value, PROPERTY_ACCESS_CALLBACK);
    }

    @Override
    public boolean setPropertyValue (String propertyPath, Object value,
                                     PropertyAccessCallback callback) throws Exception {
        Assert.notNull (callback, "参数 {callback} 不能为 'null' 。");
        return setPropertyValue0 (propertyPath, value, callback);
    }

    private boolean setPropertyValue0 (String propertyPath, Object value,
                                       PropertyAccessCallback callback) throws Exception {
        List<PropertyWrapper> propertyWrappers = getPropertyWrappers (propertyPath);
        if (propertyWrappers.isEmpty ()) {
            return false;
        }
        Object target = getWrappedInstance ();
        if (propertyWrappers.size () == 1) {
            PropertyWrapper propertyWrapper = propertyWrappers.get (0);
            PropertyAccessor propertyAccessor = propertyWrapper.createPropertyAccessor (target);
            callback.lastPropertyWrite (value, propertyWrapper, propertyAccessor,
                                        this.typeConverterManager);
            return true;
        }
        for (int i = 0; i < propertyWrappers.size (); i++) {
            PropertyWrapper propertyWrapper = propertyWrappers.get (i);
            PropertyAccessor propertyAccessor = propertyWrapper.createPropertyAccessor (target);
            Object object;
            if (i == propertyWrappers.size () - 1) {
                object = callback.lastPropertyWrite (value, propertyWrapper, propertyAccessor,
                                                     this.typeConverterManager);
            } else {
                object = callback.propertyWrite (propertyWrapper, propertyAccessor,
                                                 this.typeConverterManager);
            }
            target = object;
        }
        return true;
    }

    /**
     * 通过 beanClass 和 propertyName 得到对应的 {@link PropertyDescriptor}
     *
     * @param beanClass    bean的 Class 对象。
     * @param propertyName 从 beanClass 中得到 PropertyDescriptor 的名称。
     *
     * @return 参数对应的 PropertyDescriptor 。
     */
    protected PropertyDescriptor getPropertyDescriptor (Class<?> beanClass, String propertyName) {
        return ReflectionUtils.getPropertyDescriptor (beanClass, propertyName);
    }

    /**
     * 得到该包装器解析属性路径表达式时所使用的字段类型扫描器提供者。
     *
     * @return 该包装器解析属性路径表达式时所使用的字段类型扫描器提供者。
     */
    public FieldTypeScannerProvider getFieldTypeScannerProvider () {
        return this.fieldTypeScannerProvider;
    }

    /**
     * 指定该包装器解析属性路径表达式时所使用的字段类型扫描器提供者。
     *
     * @param fieldTypeScannerProvider 解析属性路径表达式时所使用的字段类型扫描器提供者。
     */
    public final void setFieldTypeScannerProvider (
            FieldTypeScannerProvider fieldTypeScannerProvider) {
        if (fieldTypeScannerProvider == null) {
            return;
        }
        this.fieldTypeScannerProvider = fieldTypeScannerProvider;
        this.propertyWrapperCache.clear ();
    }

    /**
     * 得到定用于创建属性包装器的提供者。
     *
     * @return 定用于创建属性包装器的提供者。
     */
    public PropertyWrapperProvider getPropertyWrapperProvider () {
        return this.propertyWrapperProvider;
    }

    /**
     * 指定用于创建属性包装器的提供者。
     *
     * @param propertyWrapperProvider 用于创建属性包装器的提供者。
     */
    public final void setPropertyWrapperProvider (PropertyWrapperProvider propertyWrapperProvider) {
        if (propertyWrapperProvider == null) {
            return;
        }
        this.propertyWrapperProvider = propertyWrapperProvider;
        this.propertyWrapperCache.clear ();
    }

    /**
     * 得到当前包装器使用的类型转换器管理器。
     *
     * @return 当前包装器使用的类型转换器管理器。
     */
    public TypeConverterManager getTypeConverterManager () {
        return this.typeConverterManager;
    }

    /**
     * 指定当前包装器使用的类型转换器管理器。
     *
     * @param typeConverterManager 类型转换器管理器。
     */
    public final void setTypeConverterManager (TypeConverterManager typeConverterManager) {
        if (typeConverterManager == null) {
            return;
        }
        this.typeConverterManager = typeConverterManager;
    }

    private static class PropertyAccessCallbackImpl implements PropertyAccessCallback {

        @Override
        public Object propertyWrite (PropertyWrapper propertyWrapper,
                                     PropertyAccessor propertyAccessor, TypeConverterManager typeConverterManager)
                throws Exception {
            Object object;
            object = propertyAccessor.get ();
            if (object == null) {
                object = propertyAccessor.propertyNewInstance ();
            }
            propertyAccessor.set (object);
            return object;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Object lastPropertyWrite (Object value, PropertyWrapper propertyWrapper,
                                         PropertyAccessor propertyAccessor, TypeConverterManager typeConverterManager)
                throws Exception {
            Object object = value;
            if (object != null && !propertyWrapper.getType ().isAssignableFrom (object.getClass ())) {
                TypeConverter<?> typeConverter = typeConverterManager
                        .getTypeConverter (object.getClass (), propertyWrapper.getType ());
                if (typeConverter != null) {
                    object = typeConverter.convert (object, (Class) propertyWrapper.getType ());
                }
            }
            propertyAccessor.set (object);
            return object;
        }

    }

}