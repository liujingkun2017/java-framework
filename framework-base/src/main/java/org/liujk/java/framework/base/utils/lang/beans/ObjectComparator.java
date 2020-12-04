
package org.liujk.java.framework.base.utils.lang.beans;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.liujk.java.framework.base.exceptions.Exceptions;
import org.liujk.java.framework.base.utils.lang.ClassUtils;
import org.liujk.java.framework.base.utils.lang.ObjectUtils;
import org.liujk.java.framework.base.utils.lang.PrimitiveUtils;
import org.liujk.java.framework.base.utils.lang.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 说明：
 * <p>
 * 对象比较工具
 *
 */
public class ObjectComparator extends AbstractBeanUtils {
    private static final String PACKAGE_NAME = getPackageName (ObjectComparator.class);
    private static final Map<Key, I> CACHE = Maps.newConcurrentMap ();
    private static final Logger logger = LoggerFactory.getLogger (ObjectComparator.class);
    /**
     * 打印生成源代码
     */
    public static boolean logSource = false;
    public static String dumpClass = null;

    /**
     * 比较两个对象的属性值<br/>
     * 如果对象为null，等价于对象的所有属性值都为null；<br/>
     * 如果传入的对象都为null，返回null；<br/>
     * 默认返回不等的属性
     *
     * @param objA            对象A
     * @param objB            对象B
     * @param ignorePropeties 忽略比对的属性，如果没有则不传。
     * @param <T>             对象类型
     *
     * @return
     */
    public static <T> Map<String, ComparedResult> compare (T objA, T objB,
                                                           String... ignorePropeties) {
        return compare (objA, objB, CompareStrategy.UNEQUAL, ignorePropeties);
    }

    public static Map<String, ComparedResult> compareMap (Map objA, Map objB,
                                                          CompareStrategy strategy, String... ignorePropeties) {
        if (objA == null && objB == null) {
            // 如果传入的对象都为null，返回null
            return null;
        }
        if (strategy == null) {
            strategy = CompareStrategy.UNEQUAL;
        }
        Set keySet = Sets.newHashSet ();
        if (objA != null) {
            keySet.addAll (objA.keySet ());
        }
        if (objB != null) {
            keySet.addAll (objB.keySet ());
        }
        Map<String, ComparedResult> resultMap = Maps.newHashMap ();

        Set<String> ignorePropetiesSet;
        if (ignorePropeties == null) {
            ignorePropetiesSet = Sets.newHashSet ();
        } else {
            ignorePropetiesSet = Sets.newHashSet (ignorePropeties);
        }
        Object valueA;
        Object valueB;
        ComparedResult result;
        for (Object key : keySet) {
            valueA = objA == null ? null : objA.get (key);
            valueB = objB == null ? null : objB.get (key);

            if (ignorePropetiesSet.contains (key)) {
                continue;
            }
            boolean add = false;
            if (ObjectUtils.equals (valueA, valueB)) {
                if (strategy.isInList (CompareStrategy.EQUALITY, CompareStrategy.ALL)) {
                    add = true;
                }
            } else {
                if (strategy.isInList (CompareStrategy.UNEQUAL, CompareStrategy.ALL)) {
                    add = true;
                }
            }
            if (add) {
                result = new ComparedResult ();
                result.setValueA (valueA);
                result.setValueB (valueB);
                result.setPropertyName (key.toString ());
                resultMap.put (key.toString (), result);
            }
        }

        return resultMap;
    }

    public static <T> Map<String, ComparedResult> compare (T objA, T objB, CompareStrategy strategy,
                                                           String... ignorePropeties) {
        if (objA == null && objB == null) {
            // 如果传入的对象都为null，返回null
            return null;
        }

        if (strategy == null) {
            strategy = CompareStrategy.UNEQUAL;
        }

        Class<?> clazz;
        if (objA != null) {
            clazz = objA.getClass ();
        } else {
            clazz = objB.getClass ();
        }

        // parent.isAssignableFrom(child);
        if (Map.class.isAssignableFrom (clazz)) {
            return compareMap ((Map) objA, (Map) objB, strategy, ignorePropeties);
        }

        Key key = new Key (clazz, strategy, ignorePropeties);
        I i = CACHE.get (key);
        if (i == null) {
            synchronized (ObjectComparator.class) {
                i = CACHE.get (key);
                if (i == null) {
                    Generator generator = new Generator ();
                    generator.setSource (clazz);
                    generator.setStrategy (strategy);
                    generator.setIgnorePropeties (ignorePropeties);
                    try {
                        i = generator.generate ().newInstance ();
                        CACHE.put (key, i);
                    } catch (Exception e) {
                        throw Exceptions.newRuntimeExceptionWithoutStackTrace (e);
                    }
                }
            }
        }

        return i.compare (objA, objB);
    }

    public static enum CompareStrategy {
        /**
         * 比较对象时，返回不相等的属性集合
         */
        UNEQUAL,
        /**
         * 比较对象时，返回相等对象的集合
         */
        EQUALITY,
        /**
         * 比较对象时，返回所有的属性集合
         */
        ALL;

        /**
         * 判断给定的枚举，是否在列表中
         *
         * @param values 列表
         *
         * @return
         */
        public boolean isInList (CompareStrategy... values) {
            for (CompareStrategy e : values) {
                if (this == e) {
                    return true;
                }
            }
            return false;
        }
    }

    public static interface I {
        <T> Map<String, ComparedResult> compare(T objA, T objB);
    }

    public static class Generator extends AbstractGenerator {

        private static final String SOURCE_A = "sa";
        private static final String SOURCE_B = "sb";

        CompareStrategy strategy = CompareStrategy.UNEQUAL;

        public void setStrategy (CompareStrategy strategy) {
            if (strategy != null) {
                this.strategy = strategy;
            }
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
            return PACKAGE_NAME + ".ObjectComparatorImpl";
        }

        @Override
        protected void generateBegin () {
            beginSource = "public java.util.Map compare( Object objA, Object objB ){\n";
            // 强制转换源对象
            String convertSourceA = String.format ("%s %s=(%s)objA;\n", source.getName (), SOURCE_A,
                                                   source.getName (), SOURCE_A);
            String convertSourceB = String.format ("%s %s=(%s)objB;\n", source.getName (), SOURCE_B,
                                                   source.getName (), SOURCE_B);
            beginSource += convertSourceA + convertSourceB;
        }

        @Override
        protected void generateEnd () {
            endSources = " return map;}";
        }

        @Override
        protected void generateBody () {
            Map<String, PropertyDescriptor> pdsMap = getPropertyDescriptors ();
            bodySources.add (String.format ("if(%s==null && %s==null ){\nreturn null;\n}\n\n",
                                            SOURCE_A, SOURCE_B));

            bodySources.add (String.format ("java.util.Map map = new java.util.HashMap (%d);\n\n",
                                            pdsMap.size () + 1));

            bodySources.add (
                    String.format ("%s result;\n", ClassUtils.getClassName (ComparedResult.class)));

            bodySources.add (String.format ("if(%s==null || %s==null ){\n", SOURCE_A, SOURCE_B));
            bodySources.add (String.format ("boolean isANull = %s==null;\n", SOURCE_A));
            bodySources.add ("Object value;\n");

            bodySources.add (String.format ("%s obj = isANull?%s:%s;\n", source.getName (), SOURCE_B,
                                            SOURCE_A));

            for (Map.Entry<String, PropertyDescriptor> entry : pdsMap.entrySet ()) {
                PropertyDescriptor pd = entry.getValue ();

                Method read = pd.getReadMethod ();
                if (read == null) {
                    continue;
                }
                if (ignorePropeties.contains (pd.getName ())) {
                    continue;
                }
                // 是否是包装类转换
                Class<?> propertyType = pd.getPropertyType ();

                bodySources.add (String.format ("result=new %s( %s ,\"%s\");\n",
                                                ClassUtils.getClassName (ComparedResult.class), buildClassName (pd),
                                                pd.getName ()));
                if (propertyType.isPrimitive ()) {
                    bodySources.add (String.format ("value = (Object)%s.value(%s);\n",
                                                    PrimitiveUtils.class.getName (), buildReadMethod ("obj", pd)));
                } else {
                    bodySources.add (String.format ("value = %s;\n", buildReadMethod ("obj", pd)));
                }

                bodySources.add ("if(isANull){\n");
                bodySources.add ("result.setValueB(value);\n");
                bodySources.add ("}else{\n");
                bodySources.add ("result.setValueA(value);\n");
                bodySources.add ("}\n");
                bodySources.add (String.format ("map.put(\"%s\",result);\n\n", pd.getName (),
                                                pd.getPropertyType (), pd.getName ()));
            }
            bodySources.add ("}\n");
            bodySources.add ("else{\n");

            for (Map.Entry<String, PropertyDescriptor> entry : pdsMap.entrySet ()) {
                PropertyDescriptor pd = entry.getValue ();

                Method read = pd.getReadMethod ();
                if (read == null) {
                    continue;
                }
                // if(fieldCount++>=0) break;
                // 是否是包装类转换
                Class<?> propertyType = pd.getPropertyType ();
                if (propertyType.isPrimitive ()) {
                    if (strategy == CompareStrategy.UNEQUAL) {
                        bodySources.add (String.format ("if(%s != %s){\n",
                                                        buildReadMethod (SOURCE_A, pd),
                                                        buildReadMethod (SOURCE_B, pd)));
                    } else if (strategy == CompareStrategy.EQUALITY) {
                        bodySources.add (String.format ("if(%s == %s){\n",
                                                        buildReadMethod (SOURCE_A, pd),
                                                        buildReadMethod (SOURCE_B, pd)));
                    }
                    bodySources.add (String.format ("result=new %s(%s,\"%s\");\n",
                                                    ClassUtils.getClassName (ComparedResult.class), buildClassName (pd),
                                                    pd.getName ()));
                    bodySources.add (String.format ("result.setValueA(%s.value(%s));\n",
                                                    PrimitiveUtils.class.getName (), buildReadMethod (SOURCE_A, pd)));
                    bodySources.add (String.format ("result.setValueB(%s.value(%s));\n",
                                                    PrimitiveUtils.class.getName (), buildReadMethod (SOURCE_B, pd)));

                } else {
                    if (strategy == CompareStrategy.UNEQUAL) {
                        bodySources.add (String.format ("if(!%s.equals(%s,%s)){\n",
                                                        ObjectUtils.class.getName (), buildReadMethod (SOURCE_A, pd),
                                                        buildReadMethod (SOURCE_B, pd)));
                    } else if (strategy == CompareStrategy.EQUALITY) {
                        bodySources.add (String.format ("if(%s.equals(%s,%s)){\n",
                                                        ObjectUtils.class.getName (), buildReadMethod (SOURCE_A, pd),
                                                        buildReadMethod (SOURCE_B, pd)));
                    }
                    bodySources.add (String.format ("result=new %s(%s ,\"%s\");\n",
                                                    ClassUtils.getClassName (ComparedResult.class), buildClassName (pd),
                                                    pd.getName ()));
                    bodySources.add (String.format ("result.setValueA(%s);\n",
                                                    buildReadMethod (SOURCE_A, pd)));
                    bodySources.add (String.format ("result.setValueB(%s);\n",
                                                    buildReadMethod (SOURCE_B, pd)));
                }
                bodySources.add (String.format ("map.put(\"%s\",result);\n", pd.getName (),
                                                pd.getPropertyType (), pd.getName ()));
                if (strategy.isInList (CompareStrategy.UNEQUAL, CompareStrategy.EQUALITY)) {
                    bodySources.add ("}\n");
                }
            }

            bodySources.add ("}\n");
        }

        public Class<I> generate () {
            return super.generate (I.class);
        }

    }

    public static class ComparedResult {
        private String propertyName;
        private Class type;
        private Object valueA = null;
        private Object valueB = null;

        public ComparedResult () {

        }

        public ComparedResult (Class type, String propertyName) {
            setType (type);
            setPropertyName (propertyName);
        }

        public String getPropertyName () {
            return propertyName;
        }

        public void setPropertyName (String propertyName) {
            this.propertyName = propertyName;
        }

        public Class getType () {
            return type;
        }

        public void setType (Class type) {
            this.type = type;
        }

        public Object getValueA () {
            return valueA;
        }

        public void setValueA (Object valueA) {
            this.valueA = valueA;
        }

        public Object getValueB () {
            return valueB;
        }

        public void setValueB (Object valueB) {
            this.valueB = valueB;
        }

        /**
         * 值是否相等；<br/>
         *
         * @return
         */
        public boolean isEqual () {
            if (this.valueA == null && this.valueB == null) {
                return true;
            } else if (this.valueA == null || this.valueB == null) {
                return false;
            } else {
                return this.valueA.equals (this.valueB);
            }
        }

        @Override
        public String toString () {
            return ToString.toString (this);
        }
    }

    private static class Key implements Serializable {
        private static final long serialVersionUID = 3042591728287275779L;

        private Class<?> source;
        private CompareStrategy strategy;
        private String[] ignoreProperties;

        public Key (Class<?> source, CompareStrategy strategy, String[] ignoreProperties) {
            this.source = source;
            this.strategy = strategy;
            this.ignoreProperties = ignoreProperties;
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

            if (source != null ? !source.equals (key.source) : key.source != null) {
                return false;
            }
            if (strategy != key.strategy) {
                return false;
            }
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals (ignoreProperties, key.ignoreProperties);

        }

        @Override
        public int hashCode () {
            int result = source != null ? source.hashCode () : 0;
            result = 31 * result + (strategy != null ? strategy.hashCode () : 0);
            result = 31 * result + Arrays.hashCode (ignoreProperties);
            return result;
        }

        @Override
        public String toString () {
            return "Key[" + (source == null ? "NULL" : source.getSimpleName ()) +
                    (strategy == null ? "" : (":" + strategy.name ())) +
                    "]";
        }
    }
}
