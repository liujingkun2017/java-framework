
package org.liujk.java.framework.base.utils.lang.beans;




import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.liujk.java.framework.base.exceptions.Exceptions;
import org.liujk.java.framework.base.utils.lang.*;
import org.liujk.java.framework.base.utils.lang.beans.collection.LinkedMultiValueMap;
import org.liujk.java.framework.base.utils.lang.beans.collection.MultiValueMap;
import org.liujk.java.framework.base.utils.lang.beans.converter.TypeConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * 说明：
 * <p>
 * 对象属性复制工具类,采用javassit生成源代码实现属性复制.并能实现包装类型和基础类型之间的转换
 * <h3>Usage Examples</h3>
 * <p/>
 * 该工具使用 {@link TypeConverterUtils} 完成类型转换。
 * <p>
 * <p>
 * <pre class="code">
 * {
 * &#064;code
 * TestBean testBean = new TestBean();
 * Copier.copy(TestBean1.createTest(), testBean);
 * Copier.copy(TestBean1.createTest(), testBean, &quot;t1&quot;);
 * }
 * </pre>
 * <p/>
 * 使用时请确保classpath中有:
 * <p/>
 * <p>
 * <p>
 * <pre class="code">
 * {@code
 * <dependency>
 * <groupId>org.javassist</groupId>
 * <artifactId>javassist</artifactId>
 * </dependency>
 * }
 * </pre>
 *
 * @see TypeConverterUtils
 */
public class Copier extends AbstractBeanUtils {

    private static final Logger logger = LoggerFactory.getLogger (Copier.class);

    private static final String PACKAGE_NAME = getPackageName (Copier.class);
    /**
     * Copy实现类对象缓存
     */
    private static final Map<Key, Copy>
            COPIER_MAP = new ConcurrentHashMap<Key, Copy> ();
    /**
     * 生成class dump路径
     */
    public static String dumpClass = null;
    /**
     * 打印生成源代码
     */
    public static boolean logSource = false;

    /**
     * @param from            源对象
     * @param toClass         目标类型
     * @param ignorePropeties 忽略目标对象(toClass)的属性，如果没有则不传。
     *
     * @return 复制结果
     */
    public static <T> T copy (Object from, Class<T> toClass, String... ignorePropeties) {
        return copy (from, toClass, CopyStrategy.CONTAIN_NULL, ignorePropeties);
    }

    /**
     * @param from            源对象
     * @param toClass         目标类型
     * @param strategy        复制策略
     * @param ignorePropeties 忽略参数，如果没有则不传。
     *
     * @return 复制结果
     */
    public static <T> T copy (Object from, Class<T> toClass, CopyStrategy strategy,
                              String... ignorePropeties) {
        notNull (toClass, "toClass不能为空");
        try {
            T target = toClass.newInstance ();
            copy (from, target, strategy, NoMatchingRule.IGNORE, ignorePropeties);
            return target;
        } catch (Exception e) {
            throw Exceptions.newRuntimeExceptionWithoutStackTrace (e);
        }
    }

    /**
     * 属性复制，复制时会做类型转换，复制时包括null值，并且忽略不兼容的属性。
     *
     * @param from            源对象
     * @param to              目标对象
     * @param ignorePropeties 忽略属性，如果没有则不传。
     */
    public static void copy (Object from, Object to, String... ignorePropeties) {
        copy (from, to, CopyStrategy.CONTAIN_NULL, NoMatchingRule.IGNORE, ignorePropeties);
    }

    /**
     * 属性复制
     *
     * @param from            源对象
     * @param to              目标对象
     * @param strategy        复制策略
     * @param noMatchingRule  当拷贝属性不兼容时的处理规则。
     * @param ignorePropeties 忽略属性
     */
    public static void copy (Object from, Object to, CopyStrategy strategy,
                             NoMatchingRule noMatchingRule, String... ignorePropeties) {
        Key key = getKey (notNull (from, "源对象不能为空"), notNull (to, "目标对象不能为空"),
                                 notNull (strategy, "拷贝策略不能为空"), ignorePropeties);
        Copy copy = COPIER_MAP.get (key);
        if (copy == null) {
            synchronized (Copier.class) {
                copy = COPIER_MAP.get (key);
                if (copy == null) {
                    Generator generator = new Generator ();
                    generator.setSource (from.getClass ());
                    generator.setTarget (to.getClass ());
                    generator.setIgnorePropeties (ignorePropeties);
                    generator.setNoMatchingRule (noMatchingRule);
                    generator.setStrategy (strategy);
                    try {
                        copy = generator.generate ().newInstance ();
                        COPIER_MAP.put (key, copy);
                    } catch (Exception e) {
                        throw new RuntimeException (e);
                    }
                }
            }
        }
        copy.copy (from, to);
    }

    private static Key getKey (Object from, Object to, CopyStrategy strategy,
                                      String[] ignoreProperties) {
        Class<?> fromClass = from.getClass ();
        Class<?> toClass = to.getClass ();
        return new Key (fromClass, toClass, ignoreProperties);
    }

    public static enum CopyStrategy {
        /**
         * 复制时忽略null对象，源对象属性为null，目标对象属性不会被设置为null
         */
        IGNORE_NULL,
        /**
         * 复制时包括null对象。源对象属性为null，目标对象属性会被设置为null
         */
        CONTAIN_NULL
    }

    /**
     * 当拷贝属性不兼容时的处理规则。
     *
     * @since 2.1
     */
    public static enum NoMatchingRule {

        /**
         * 忽略
         */
        IGNORE,

        /**
         * 抛出异常
         */
        EXCEPTION;

    }

    /**
     * 指定拷贝对象的字段的对应关系
     * <p>
     * <p>
     * <pre class="code">
     * public class BeanA {
     * private String userName = "Str1"；
     * <p>
     * //...getter setter...
     * }
     * </pre>
     * <p>
     * <p>
     * <pre class="code">
     * public class BeanB {
     * CopyFrom({"userName"})
     * private String username；
     * <p>
     * //...getter setter...
     * }
     * </pre>
     * <p>
     * 使用
     * <p>
     * <pre class="code">
     * Copier.copy(beanA, beanB);
     * </pre>
     * <p>
     * 这样就能从 beanA.userName 拷贝到 beanB.username 了
     * <p/>
     * 如果指定的属性不存在，则忽略该注解的指定的属性；<br/>
     * <p>
     * <p>
     * <pre class="code">
     * public class BeanB {
     * CopyFrom({"userName","name"})
     * private String username；
     * <p>
     * //...getter setter...
     * }
     * </pre>
     * <p>
     * 拷贝顺序：getter > userName > name > username <br/>
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface CopyFrom {
        /**
         * 指定对象的来源 属性
         *
         * @return
         */
        String[] value() default {};

        /**
         * 指定从一个getter中获取值
         *
         * @return
         */
        String getter() default "";

        /**
         * 指定字段的转换器
         * <p>
         * 转换器需要实现{@link BeanCopierConverter}接口
         *
         * @return
         */
        Class<? extends BeanCopierConverter> converter() default NoneConverter.class;
    }

    /**
     * 指定拷贝对象的字段的对应关系
     * <p>
     * <p>
     * <pre class="code">
     * public class BeanA {
     * CopyFrom({"username"})
     * private String userName = "Str1"；
     * <p>
     * //...getter setter...
     * }
     * </pre>
     * <p>
     * <p>
     * <pre class="code">
     * public class BeanB {
     * private String username；
     * <p>
     * //...getter setter...
     * }
     * </pre>
     * <p>
     * 使用
     * <p>
     * <pre class="code">
     * Copier.copy(beanA, beanB);
     * </pre>
     * <p>
     * 这样就能从 beanA.userName 拷贝到 beanB.username 了
     * <p/>
     * 如果指定的属性不存在，则忽略该注解的指定的属性；<br/>
     * <p>
     * <p>
     * <pre class="code">
     * public class BeanA {
     * CopyTo({"username","name"})
     * private String userName = "Str1"；
     * <p>
     * //...getter setter...
     * }
     * </pre>
     * <p>
     * 拷贝顺序：settter > 目标对象CopyFrom指定的顺序 > userName > username/name <br/>
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface CopyTo {
        /**
         * 指定对象的目标 属性
         *
         * @return
         */
        String[] value() default {};

        /**
         * 指定从一个getter中获取值
         *
         * @return
         */
        String setter() default "";

        /**
         * 指定字段的转换器
         * <p>
         * 转换器需要实现{@link BeanCopierConverter}接口
         *
         * @return
         */
        Class<? extends BeanCopierConverter> converter() default NoneConverter.class;
    }

    public static interface Copy {
        void copy(Object source, Object target);
    }

    private static class Generator extends AbstractGenerator {

        private final static String TARGET = "t";
        protected Map<String, Field> targetFieldMap = null;
        private Class<?> target;
        private NoMatchingRule noMatchingRule;
        private CopyStrategy strategy;

        /**
         * 是否为包装类
         */
        public static boolean isWrapClass (Class<?> clazz) {
            try {
                return ((Class<?>) clazz.getField ("TYPE").get (null)).isPrimitive ();
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * source对象类型是否是target对象类型的包装类
         */
        public static boolean isWrapClass (Class<?> source, Class<?> target) {
            if (!target.isPrimitive ()) {
                return false;
            }
            try {
                return source.getField ("TYPE").get (null) == target;
            } catch (Exception e) {
                return false;
            }
        }

        public void setStrategy (CopyStrategy strategy) {
            this.strategy = strategy;
        }

        public void setTarget (Class<?> target) {
            this.target = target;
        }

        public void setNoMatchingRule (NoMatchingRule noMatchingRule) {
            this.noMatchingRule = noMatchingRule;
        }

        @Override
        protected boolean isLogSource () {
            return logSource;
        }

        @Override
        protected Logger getLogger () {
            return logger;
        }

        @Override
        protected String getDumpClass () {
            return dumpClass;
        }

        @Override
        protected String getPackageName () {
            return PACKAGE_NAME + ".CopierImpl";
        }

        /**
         * 获取 CopyFrom 指定的字段 <br/>
         * <p>
         * <pre class="code">
         * public class BeanB {
         * CopyFrom({"userName","name"})
         * private String username = ；
         * <p>
         * //...getter setter...
         * }
         * </pre>
         * <p>
         * result={username->{userName,name}}
         *
         * @param clazz
         *
         * @return
         */

        private Map<String, List<CopyWrapper>> findCopyFrom (Class<?> clazz) {
            Map<String, List<CopyWrapper>> fieldMap = Maps.newHashMap ();

            Class<?> superClass = clazz.getSuperclass ();
            if (superClass != null && !Object.class.equals (superClass)) {
                fieldMap.putAll (findCopyFrom (superClass));
            }

            Field[] fields = clazz.getDeclaredFields ();
            for (Field field : fields) {
                CopyFrom copyFrom = field.getAnnotation (CopyFrom.class);
                if (copyFrom != null) {
                    String[] fromValues = copyFrom.value ().length == 0
                            ? new String[]{field.getName ()}
                            : copyFrom.value ();
                    for (String value : fromValues) {
                        List<CopyWrapper> wrapperList = fieldMap.get (field.getName ());
                        if (wrapperList == null) {
                            wrapperList = Lists.newArrayList ();
                            fieldMap.put (field.getName (), wrapperList);
                        }
                        CopyWrapper wrapper = new CopyWrapper (value, field.getName ());
                        if (!NoneConverter.class.equals (copyFrom.converter ())) {
                            wrapper.setConverter (copyFrom.converter ());
                        }
                        if (StringUtils.isNotBlank (copyFrom.getter ())) {
                            wrapper.setGetter (copyFrom.getter ());
                        }
                        wrapperList.add (wrapper);
                    }
                }
            }
            return fieldMap;
        }

        /**
         * 获取 CopyTo 指定的字段 <br/>
         * <p>
         * <pre class="code">
         * public class BeanA {
         * CopyTo({"username","name"})
         * private String userName = "Str1"；
         * <p>
         * //...getter setter...
         * }
         * </pre>
         * <p>
         * result={username->userName,name->userName}
         *
         * @param clazz
         *
         * @return
         */
        private Map<String, CopyWrapper> findCopyTo (Class<?> clazz) {
            Map<String, CopyWrapper> fieldMap = Maps.newHashMap ();

            Class<?> superClass = clazz.getSuperclass ();
            if (superClass != null && !Object.class.equals (superClass)) {
                fieldMap.putAll (findCopyTo (superClass));
            }

            Field[] fields = clazz.getDeclaredFields ();
            for (Field field : fields) {
                CopyTo copyTo = field.getAnnotation (CopyTo.class);
                if (copyTo != null) {
                    String[] toValues = copyTo.value ().length == 0
                            ? new String[]{field.getName ()}
                            : copyTo.value ();
                    for (String value : toValues) {

                        CopyWrapper wrapper = new CopyWrapper (field.getName (), value);
                        if (!NoneConverter.class.equals (copyTo.converter ())) {
                            wrapper.setConverter (copyTo.converter ());
                        }
                        if (StringUtils.isNotBlank (copyTo.setter ())) {
                            wrapper.setGetter (copyTo.setter ());
                        }

                        fieldMap.put (value, wrapper);
                    }
                }
            }
            return fieldMap;
        }

        @Override
        protected void parseFields () {
            super.parseFields ();
            targetFieldMap = getDeclaredFieldMap (target);
        }

        @Override
        protected void generateBegin () {
            // 生成方法签名public void copy(TestBean s, TestBean1 t) {
            beginSource = "public void copy(Object " + SOURCE + "1 ,Object " + TARGET + "1){\n";
            // 强制转换源对象
            String convertSource = source.getName () + " " + SOURCE + " =" + "(" + source.getName ()
                    + ")" + SOURCE + "1;\n";
            // 强制转换目标对象
            String convertTarget = target.getName () + " " + TARGET + " =" + "(" + target.getName ()
                    + ")" + TARGET + "1;\n";
            beginSource += convertSource + convertTarget;

        }

        @Override
        protected void generateEnd () {
            endSources = "\n}";
        }

        @Override
        protected void generateBody () {

            ClassInfo sourceInfo = ClassInfoUtils.getClassInfo (source);
            ClassInfo targetInfo = ClassInfoUtils.getClassInfo (target);

            // 源
            Map<String, CopyWrapper> fromGetterMap = findCopyTo (this.source);
            // 目标
            Map<String, List<CopyWrapper>> toSetterMap = findCopyFrom (this.target);

            for (Map.Entry<String, PropertyDescriptor> targetEntry : targetInfo.getPropertyMap ()
                    .entrySet ()) {
                PropertyDescriptor targetProperty = targetEntry.getValue ();

                PropertyDescriptor sourceProperty = null;

                Method readMethod = null;
                Method writeMethod = null;
                Class<? extends BeanCopierConverter> converter = null;

                if (sourceProperty == null) {
                    // find from
                    // CopyFrom({"userName","name"})
                    // Map = {username->{userName,name}}
                    List<CopyWrapper> sourceFields = toSetterMap.get (targetProperty.getName ());
                    if (CollectionUtils.isNotEmpty (sourceFields)) {
                        for (CopyWrapper fromField : sourceFields) {
                            sourceProperty = sourceInfo.getPropertyMap ().get (fromField.from);
                            converter = fromField.getConverter ();
                            if (StringUtils.isNotBlank (fromField.getGetter ())) {
                                readMethod = sourceInfo.getGetterMap ().get (fromField.getGetter ());
                                if (readMethod == null) {
                                    MethodDescriptor methodDescriptor = sourceInfo.getMethodMap ()
                                            .get (fromField.getGetter ());
                                    if (methodDescriptor != null) {
                                        readMethod = methodDescriptor.getMethod ();
                                        try {
                                            sourceProperty = new PropertyDescriptor (fromField.from, source,
                                                                                     readMethod.getName (), null);
                                        } catch (Exception e) {
                                            e.printStackTrace ();
                                        }
                                    }
                                } else {
                                    try {
                                        sourceProperty = new PropertyDescriptor (fromField.from, source,
                                                                                 readMethod.getName (), null);
                                    } catch (Exception e) {
                                        e.printStackTrace ();
                                    }
                                }
                            }
                            if (sourceProperty != null) {
                                break;
                            }
                        }
                    }
                }
                if (sourceProperty == null && readMethod == null) {
                    // CopyTo({"username","name"})
                    // {username->userName,name->userName}
                    CopyWrapper toField = fromGetterMap.get (targetProperty.getName ());
                    if (toField != null) {
                        if (converter == null) {
                            converter = toField.getConverter ();
                        }
                        if (StringUtils.isNotBlank (toField.getSetter ())) {
                            writeMethod = targetInfo.getSetterMap ().get (toField.getSetter ());
                        }
                        sourceProperty = sourceInfo.getPropertyMap ().get (toField.from);
                    }
                }

                if (sourceProperty == null) {
                    sourceProperty = sourceInfo.getPropertyMap ().get (targetProperty.getName ());
                }

                if (!checkCanGenSource (targetProperty, sourceProperty)) {
                    continue;
                }

                if (readMethod == null) {
                    readMethod = sourceProperty.getReadMethod ();
                }
                if (writeMethod == null) {
                    writeMethod = targetProperty.getWriteMethod ();
                }

                if (Collection.class.isAssignableFrom (sourceProperty.getPropertyType ()) &&
                        (Collection.class.isAssignableFrom (targetProperty.getPropertyType ()) ||
                                targetProperty.getPropertyType ().isArray ())) {
                    buildCollectionCopyCode (sourceProperty, targetProperty, readMethod, writeMethod, converter);
                } else if (Map.class.isAssignableFrom (sourceProperty.getPropertyType ()) &&
                        Map.class.isAssignableFrom (targetProperty.getPropertyType ())) {
                    buildMapCopyCode (sourceProperty, targetProperty, readMethod, writeMethod, converter);
                } else {
                    buildCopyCode (sourceProperty, targetProperty, readMethod, writeMethod, converter);
                }
            }
        }

        private void buildCopyCode (PropertyDescriptor getter, PropertyDescriptor setter,
                                    Method readMethod, Method writeMethod,
                                    Class<? extends BeanCopierConverter> converter) {
            String readMethodName = readMethod.getName ();
            String writerMethodName = writeMethod.getName ();
            Class<?> getterPropertyType = getter.getPropertyType ();
            if (compatible (getter, setter)) {
                String getterSource;
                if (converter != null) {
                    // 自定义转换器
                    getterSource = String.format (
                            "(%s)%s.class.cast(%s.getConverter(%s.class).convert(%s,%s.%s()))",
                            setter.getPropertyType ().getName (), setter.getPropertyType ().getName (),
                            BeanCopierConverterFactory.class.getName (), converter.getName (), SOURCE,
                            SOURCE, readMethodName);
                } else {
                    getterSource = String.format ("%s.%s()", SOURCE, readMethodName);
                }
                if (strategy == CopyStrategy.IGNORE_NULL && !getterPropertyType.isPrimitive ()) {
                    String source = genCheckWrapperIsNotNullSource (readMethod.getName ());
                    source += "\t" + genPropertySource (writerMethodName, getterSource);
                    bodySources.add (source);
                } else {
                    bodySources.add (genPropertySource (writerMethodName, getterSource));
                }
            } else {
                // 是否是包装类转换
                Class<?> setterPropertyType = setter.getPropertyType ();
                if (compatibleWrapper (getter, setter)) {
                    WrapperConverter convert = new WrapperConverter (setterPropertyType, SOURCE,
                                                                                   readMethod.getName ());
                    String f = convert.convert ();

                    if (f != null) {
                        String getterSource;
                        if (converter != null) {
                            // setXXX(int)
                            if (setterPropertyType.isPrimitive ()) {
                                getterSource = String.format (
                                        "(%s)((%s)%s.class.cast(%s.getConverter(%s.class).convert(%s,",
                                        setterPropertyType.getName (),
                                        PrimitiveUtils.getWrapperClass (setterPropertyType)
                                                .getName (),
                                        PrimitiveUtils.getWrapperClass (setterPropertyType)
                                                .getName (),
                                        BeanCopierConverterFactory.class.getName (),
                                        converter.getName (), SOURCE);
                                if (getterPropertyType.isPrimitive ()) {
                                    getterSource += String.format ("%s.valueOf(%s.%s())))",
                                                                   PrimitiveUtils.getWrapperClass (getterPropertyType),
                                                                   SOURCE, readMethodName);
                                } else {
                                    getterSource += String.format ("%s.%s()))", SOURCE,
                                                                   readMethodName);
                                }
                                getterSource += String.format (").%sValue()",
                                                               setterPropertyType.getName ());
                            } else {
                                // 自定义转换器
                                getterSource = String.format (
                                        "(%s)%s.class.cast(%s.getConverter(%s.class).convert(%s,",
                                        setter.getPropertyType ().getName (),
                                        setter.getPropertyType ().getName (),
                                        BeanCopierConverterFactory.class.getName (),
                                        converter.getName (), SOURCE);

                                if (getterPropertyType.isPrimitive ()) {
                                    getterSource += String.format (
                                            "%s.valueOf(%s.%s())))", PrimitiveUtils
                                                    .getWrapperClass (getterPropertyType).getName (),
                                            SOURCE, readMethodName);
                                } else {
                                    getterSource += String.format ("%s.%s()))", SOURCE,
                                                                   readMethodName);
                                }
                            }
                        } else {
                            getterSource = f;
                        }

                        if (isWrapClass (getterPropertyType)) {
                            String source = genCheckWrapperIsNotNullSource (readMethod.getName ());
                            source += "\t" + genPropertySource (writerMethodName, getterSource);
                            bodySources.add (source);
                        } else {
                            bodySources.add (genPropertySource (writerMethodName, getterSource));
                        }
                        return;
                    }
                } else {
                    String typeName = buildTypeName (setter, false);

                    StringBuilder copySource = new StringBuilder (1024);
                    copySource.append (TARGET).append (".").append (writerMethodName).append ("(");

                    if (converter != null) {
                        // 自定义转换器
                        if (setterPropertyType.isPrimitive ()) {
                            copySource.append (String.format ("(%s)((%s)%s.class.cast(",
                                                              setterPropertyType.getName (),
                                                              PrimitiveUtils.getWrapperClass (setterPropertyType)
                                                                      .getName (),
                                                              PrimitiveUtils.getWrapperClass (setterPropertyType)
                                                                      .getName ()));
                        } else {
                            copySource.append ("(").append (typeName).append (") ").append (typeName)
                                    .append (".class.cast( ");
                        }

                        copySource.append (String.format ("%s.getConverter(%s.class).convert(%s,",
                                                          BeanCopierConverterFactory.class.getName (),
                                                          converter.getName (),
                                                          SOURCE));

                        if (getterPropertyType.isPrimitive ()) {
                            copySource.append (String.format ("%s.valueOf(%s.%s())));\n",
                                                              PrimitiveUtils.getWrapperClass (getterPropertyType)
                                                                      .getName (),
                                                              SOURCE, readMethodName));
                        } else {
                            copySource
                                    .append (String.format ("%s.%s())));\n", SOURCE, readMethodName));
                        }
                    } else {
                        if (setterPropertyType.isPrimitive ()) {
                            copySource.append (PrimitiveUtils.class.getName ()).append (".value((")
                                    .append (PrimitiveUtils
                                                     .getWrapperClass (setterPropertyType.getName ())
                                                     .getName ())
                                    .append (") ").append (TypeConverterUtils.class.getName ())
                                    .append (".convertValue(");
                            if (getterPropertyType.isPrimitive ()) {
                                copySource.append (PrimitiveUtils.class.getName ()).append (".value(")
                                        .append (SOURCE).append (".").append (readMethodName)
                                        .append ("())");
                            } else {
                                copySource.append (buildTypeName (getter, false))
                                        .append (".class.cast(").append (SOURCE).append (".")
                                        .append (readMethodName).append ("())");
                            }
                            copySource.append (", ").append (typeName).append (".class)));\n");
                        } else {
                            copySource.append ("(").append (typeName).append (") ").append (typeName)
                                    .append (".class.cast( ")
                                    .append (TypeConverterUtils.class.getName ())
                                    .append (".convertValue(");
                            if (getterPropertyType.isPrimitive ()) {
                                copySource.append (PrimitiveUtils.class.getName ()).append (".value(")
                                        .append (SOURCE).append (".").append (readMethodName)
                                        .append ("())");
                            } else {
                                copySource.append (buildTypeName (getter, false))
                                        .append (".class.cast(").append (SOURCE).append (".")
                                        .append (readMethodName).append ("())");
                            }
                            copySource.append (", ").append (typeName).append (".class)));\n");
                        }
                    }
                    String s = copySource.toString ();

                    StringBuilder sb = new StringBuilder (400);
                    switch (this.noMatchingRule) {
                        case IGNORE:
                            sb.append ("try {\n");
                            sb.append (s);
                            sb.append ("} catch (java.lang.ClassCastException e) {\n");
                            sb.append ("\n");
                            sb.append ("} catch (java.lang.Exception e) {\n");
                            sb.append ("\n");
                            sb.append ("}\n");
                            break;
                        case EXCEPTION:
                            sb.append (s);
                            break;
                        default:
                            throw new InternalError ();
                    }
                    this.bodySources.add (sb.toString ());
                    return;
                }
                warnCantConvert (setter, getter);
            }
        }

        private void buildCollectionCopyCode (PropertyDescriptor getter, PropertyDescriptor setter,
                                              Method readMethod, Method writeMethod,
                                              Class<? extends BeanCopierConverter> converter) {
            String sourceFieldName = getter.getName ();
            Field sourceField = sourceFieldMap.get (sourceFieldName);

            Class sourceGenericType = getGenericType (sourceField, 0);

            String targetFieldName = setter.getName ();
            Field targetField = targetFieldMap.get (targetFieldName);
            Class targetGenericType = getGenericType (targetField, 0);

            StringBuilder code = new StringBuilder ();
            code.append (String.format ("if (%s.%s() != null ){\n", SOURCE, readMethod.getName ()));

            code.append (String.format ("\tif(%s.%s() == null ){\n", TARGET, getter.getReadMethod ().getName ()));
            code.append (String.format ("\t\t%s.%s(new %s%s);\n", TARGET, writeMethod.getName (),
                                        getImplementationClass (setter.getPropertyType ()).getName (),
                                        (setter.getPropertyType ().isArray () ? (String
                                                .format ("[%s.%s().size()]", SOURCE,
                                                         readMethod.getName ())) : "()")));
            code.append ("\t}\n");

            String valueCode;
            if (compatible (sourceGenericType, targetGenericType)) {
                valueCode = "iterator.next()";
            } else if (ClassUtils.isBaseType (sourceGenericType) || ClassUtils.isBaseType (targetGenericType)) {
                //TODO 需要使用转换器
                valueCode = "iterator.next()";
            } else if (Collection.class.isAssignableFrom (sourceGenericType) ||
                    Collection.class.isAssignableFrom (targetGenericType)) {
                //TODO 需要使用转换器
                valueCode = "iterator.next()";
            } else if (Map.class.isAssignableFrom (sourceGenericType) ||
                    Map.class.isAssignableFrom (targetGenericType)) {
                //TODO 需要使用转换器
                valueCode = "iterator.next()";
            } else {
                valueCode = String.format ("%s.copy(iterator.next(),Class.forName(\"%s\"),new String[]{\"_\"})",
                                           Copier.class.getName (), targetGenericType.getName ());
            }


            code.append (String.format ("\tjava.util.Iterator iterator = %s.%s().iterator ();\n", SOURCE,
                                        readMethod.getName ()));

            if (setter.getPropertyType ().isArray ()) {
                code.append ("\tint i = 0;\n");
            }
            code.append ("\twhile ( iterator.hasNext ()){\n");
            if (setter.getPropertyType ().isArray ()) {
                code.append (
                        String.format (
                                "\t\t%s.%s()[i++] = %s;\n",
                                TARGET, readMethod.getName (), valueCode));
            } else {
                code.append (
                        String.format (
                                "\t\t%s.%s().add (%s);\n",
                                TARGET, readMethod.getName (), valueCode));
            }
            code.append ("\t").append ("}\n");
            code.append ("}\n");
            bodySources.add (code.toString ());

        }

        /**
         * Map 对象的复制
         *
         * @param getter
         * @param setter
         * @param sourceReadMethod
         * @param targetWriteMethod
         * @param converter
         */
        private void buildMapCopyCode (PropertyDescriptor getter, PropertyDescriptor setter,
                                       Method sourceReadMethod, Method targetWriteMethod,
                                       Class<? extends BeanCopierConverter> converter) {
            String sourceFieldName = getter.getName ();
            Field sourceField = sourceFieldMap.get (sourceFieldName);

            Class sourceGenericType0 = getGenericType (sourceField, 0);
            Class sourceGenericType1 = getGenericType (sourceField, 1);

            String targetFieldName = setter.getName ();
            Field targetField = targetFieldMap.get (targetFieldName);
            Class targetGenericType0 = getGenericType (targetField, 0);
            Class targetGenericType1 = getGenericType (targetField, 1);

            StringBuilder code = new StringBuilder ();
            code.append (String.format ("if (%s.%s() != null ){\n", SOURCE, sourceReadMethod.getName ()));

            code.append (String.format ("\tif(%s.%s() == null ){\n", TARGET, getter.getReadMethod ().getName ()));
            code.append (String.format ("\t\t%s.%s(new %s());\n", TARGET, targetWriteMethod.getName (),
                                        getImplementationClass (setter.getPropertyType ()).getName ()));
            code.append ("\t}\n");


            code.append (String.format ("\tjava.util.Iterator iterator = %s.%s().entrySet ().iterator ();\n", SOURCE,
                                        sourceReadMethod.getName ()));
            code.append ("\twhile ( iterator.hasNext ()){\n");
            code.append ("\t\tjava.util.Map.Entry entry = iterator.next();\n");

            String keyCode;
            if (compatible (sourceGenericType0, targetGenericType0)) {
                keyCode = "entry.getKey()";
            } else if (ClassUtils.isBaseType (sourceGenericType0) || ClassUtils.isBaseType (targetGenericType0)) {
                //TODO 需要使用转换器
                keyCode = "entry.getKey()";
            } else if (Collection.class.isAssignableFrom (sourceGenericType0) ||
                    Collection.class.isAssignableFrom (targetGenericType0)) {
                //TODO 需要使用转换器
                keyCode = "entry.getKey()";
            } else if (Map.class.isAssignableFrom (sourceGenericType0) ||
                    Map.class.isAssignableFrom (targetGenericType0)) {
                //TODO 需要使用转换器
                keyCode = "entry.getKey()";
            } else {
                keyCode = String.format ("%s.copy(entry.getKey(),Class.forName(\"%s\"),new String[]{\"_\"})",
                                         Copier.class.getName (), targetGenericType0.getName ());
            }

            String valueCode;
            if (compatible (sourceGenericType1, targetGenericType1)) {
                valueCode = "entry.getValue()";
            } else if (ClassUtils.isBaseType (sourceGenericType1) || ClassUtils.isBaseType (targetGenericType1)) {
                //TODO 需要使用转换器
                valueCode = "entry.getValue()";
            } else if (Collection.class.isAssignableFrom (sourceGenericType1) ||
                    Collection.class.isAssignableFrom (targetGenericType1)) {
                //TODO 需要使用转换器
                valueCode = "entry.getValue()";
            } else if (Map.class.isAssignableFrom (sourceGenericType1) ||
                    Map.class.isAssignableFrom (targetGenericType1)) {
                //TODO 需要使用转换器
                valueCode = "entry.getValue()";
            } else {
                valueCode = String.format ("%s.copy(entry.getValue(),Class.forName(\"%s\"),new String[]{\"_\"})",
                                           Copier.class.getName (), targetGenericType1.getName ());
            }


            code.append (
                    String.format (
                            "\t\t%s.%s().put (%s,%s);\n",
                            TARGET, sourceReadMethod.getName (), keyCode, valueCode));

            code.append ("\t").append ("}\n");
            code.append ("}\n");
            bodySources.add (code.toString ());

        }

        private Class getImplementationClass (Class<?> propertyType) {
            int mod = propertyType.getModifiers ();
            if (Modifier.isAbstract (mod) || Modifier.isInterface (mod)) {
                //需要找实现类
                if (List.class.equals (propertyType)) {
                    return ArrayList.class;
                } else if (Collection.class.equals (propertyType)) {
                    // 声明为 private Collection param;
                    // 为了保证排序，返回ArrayList
                    return ArrayList.class;
                } else if (Set.class.equals (propertyType)) {
                    return HashSet.class;
                } else if (Queue.class.equals (propertyType) || Deque.class.equals (propertyType)) {
                    return LinkedList.class;
                } else if (BlockingQueue.class.equals (propertyType)) {
                    return LinkedBlockingQueue.class;
                } else if (BlockingDeque.class.equals (propertyType)) {
                    return LinkedBlockingDeque.class;
                } else if (Map.class.equals (propertyType)) {
                    return HashMap.class;
                } else if (ConcurrentMap.class.equals (propertyType)) {
                    return ConcurrentHashMap.class;
                } else if (MultiValueMap.class.equals (propertyType)) {
                    return LinkedMultiValueMap.class;
                } else if (BiMap.class.equals (propertyType)) {
                    return HashBiMap.class;
                }
            }
            if (propertyType.isArray ()) {
                return propertyType.getComponentType ();
            }
            //是具体的实现类
            return propertyType;
        }

        protected Class<?> getGenericType (Field field, int idx) {
            Type type = field.getGenericType ();
            if (type instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) type;
                Type genericType = t.getActualTypeArguments ()[idx];
                return genericType instanceof Class ? (Class) genericType : Object.class;
            }
            return Object.class;
        }

        private String genCheckWrapperIsNotNullSource (String readName) {
            return "if(" + SOURCE + "." + readName + "()!=null)\n";
        }

        private String genPropertySource (String writerMethodName, String getterSource) {
            return TARGET + "." + writerMethodName + "(" + getterSource + ");\n";
        }

        private void warnCantConvert (PropertyDescriptor setter, PropertyDescriptor getter) {
            logger.debug ("[对象属性复制]属性类型转换失败{}.{}({})->{}.{}({})",
                          getter.getReadMethod ().getDeclaringClass ().getSimpleName (), getter.getName (),
                          getter.getPropertyType (),
                          setter.getWriteMethod ().getDeclaringClass ().getSimpleName (), setter.getName (),
                          setter.getPropertyType ());
        }

        /**
         * 检查是否可以生成源代码
         */
        private boolean checkCanGenSource (PropertyDescriptor targetProperty, PropertyDescriptor sourceProperty) {
            if ("class".equals (targetProperty.getName ())) {
                return false;
            }
            // 是否被忽略
            if (ignorePropeties != null && isIgnoredProperty (targetProperty)) {
                return false;
            }
            // 检查getter是否存在
            if (sourceProperty == null) {
                logger.debug ("[对象属性复制]原对象[{}.{}]getter方法不存在", source.getCanonicalName (),
                              targetProperty.getName ());
                return false;
            }
            // 检查getter的读方法是否存在
            if (sourceProperty.getReadMethod () == null) {
                logger.debug ("[对象属性复制]原对象[{}.{}]getter方法不存在", source.getCanonicalName (),
                              sourceProperty.getName ());
                return false;
            }
            // 检查setter的写方法是否存在
            if (targetProperty.getWriteMethod () == null) {
                logger.debug ("[对象属性复制]目标对象[{}.{}]setter方法不存在", this.target.getCanonicalName (),
                              targetProperty.getName ());
                return false;
            }
            return true;
        }

        private boolean compatibleWrapper (PropertyDescriptor getter, PropertyDescriptor setter) {

            return isWrapClass (getter.getPropertyType (), setter.getPropertyType ())
                    || isWrapClass (setter.getPropertyType (), getter.getPropertyType ());
        }

        @SuppressWarnings({"unchecked"})
        public Class<Copy> generate () {
            return super.generate (Copy.class);
        }

        private boolean compatible (PropertyDescriptor getter, PropertyDescriptor setter) {
            return setter.getPropertyType ().isAssignableFrom (getter.getPropertyType ());
        }

        private boolean compatible (Class source, Class target) {
            return source.isAssignableFrom (target);
        }

        public PropertyDescriptor[] getPropertyDescriptors (Class<?> clazz) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo (clazz);
                return beanInfo.getPropertyDescriptors ();
            } catch (IntrospectionException e) {
                throw new RuntimeException (e);
            }
        }

        public MethodDescriptor[] getMethodDescriptors (Class<?> clazz) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo (clazz);
                return beanInfo.getMethodDescriptors ();
            } catch (IntrospectionException e) {
                throw new RuntimeException (e);
            }
        }

    }

    private static class CopyWrapper {
        private String from;
        private String to;
        private String getter;
        private String setter;
        private Class<? extends BeanCopierConverter> converter;

        public CopyWrapper (String from, String to) {
            this.from = from;
            this.to = to;
        }

        public CopyWrapper (String from, String to, Class<? extends BeanCopierConverter> converter) {
            this (from, to);
            this.converter = converter;
        }

        public String getFrom () {
            return from;
        }

        public void setFrom (String from) {
            this.from = from;
        }

        public String getTo () {
            return to;
        }

        public void setTo (String to) {
            this.to = to;
        }

        public String getGetter () {
            return getter;
        }

        public void setGetter (String getter) {
            this.getter = getter;
        }

        public String getSetter () {
            return setter;
        }

        public void setSetter (String setter) {
            this.setter = setter;
        }

        public Class<? extends BeanCopierConverter> getConverter () {
            return converter;
        }

        public void setConverter (Class<? extends BeanCopierConverter> converter) {
            this.converter = converter;
        }
    }

    /**
     * 把source转换为target需要的包装器类型或者原始类型
     */
    private static class WrapperConverter {
        private String sourceName;
        private String readMethodName;
        private Class<?> targetType;

        private WrapperConverter (Class<?> targetType, String sourceName, String readMethodName) {
            this.targetType = notNull (targetType);
            this.sourceName = notNull (sourceName);
            this.readMethodName = notNull (readMethodName);
        }

        public String convert () {
            if (targetType.isPrimitive ()) {
                String f = getPrimitiveFormat ();
                return getterSource () + "." + f + "()";
            } else if (Generator.isWrapClass (targetType)) {
                String f = getWrapperFormat ();
                return f + "(" + getterSource () + ")";
            } else {
                return null;
            }
        }

        private String getterSource () {
            return sourceName + "." + readMethodName + "()";
        }

        private String getPrimitiveFormat () {
            return targetType.getName () + "Value";
        }

        private String getWrapperFormat () {
            return targetType.getSimpleName () + ".valueOf";
        }

    }

    private static class Key {
        private Class<?> fromClass;
        private Class<?> toClass;
        private String[] ignoreProperties;
        private CopyStrategy strategy;

        @SuppressWarnings("unused")
        public Key (Class<?> fromClass, Class<?> toClass) {
            this.fromClass = fromClass;
            this.toClass = toClass;
        }

        public Key (Class<?> fromClass, Class<?> toClass, String[] ignoreProperties) {
            super ();
            this.fromClass = fromClass;
            this.toClass = toClass;
            this.ignoreProperties = ignoreProperties;
        }

        @SuppressWarnings("unused")
        public Key (Class<?> fromClass, Class<?> toClass, CopyStrategy strategy,
                    String[] ignoreProperties) {
            super ();
            this.fromClass = fromClass;
            this.toClass = toClass;
            this.ignoreProperties = ignoreProperties;
            this.strategy = strategy;
        }

        @Override
        public boolean equals (Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass () != o.getClass ()) {
                return false;
            }

            Key key = (Key) o;

            if (fromClass != null ? !fromClass.equals (key.fromClass) : key.fromClass != null) {
                return false;
            }
            if (!Arrays.equals (ignoreProperties, key.ignoreProperties)) {
                return false;
            }
            if (strategy != key.strategy) {
                return false;
            }
            if (toClass != null ? !toClass.equals (key.toClass) : key.toClass != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode () {
            int result = fromClass != null ? fromClass.hashCode () : 0;
            result = 31 * result + (toClass != null ? toClass.hashCode () : 0);
            result = 31 * result
                    + (ignoreProperties != null ? Arrays.hashCode (ignoreProperties) : 0);
            result = 31 * result + (strategy != null ? strategy.hashCode () : 0);
            return result;
        }

        @Override
        public String toString () {
            return "Key[" + (fromClass == null ? "NULL" : fromClass.getSimpleName ()) + "==>>" +
                    (toClass == null ? "NULL" : toClass.getSimpleName ()) +
                    (strategy == null ? "" : (":" + strategy.name ())) + "]";
        }
    }

    public static class NoneConverter implements BeanCopierConverter<Object, Object, Object> {
        @Override
        public Object convert (Object obj, Object src) {
            return src;
        }
    }
}