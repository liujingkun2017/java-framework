
package org.liujk.java.framework.base.utils.lang.beans;



import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.liujk.java.framework.base.exceptions.Exceptions;
import org.liujk.java.framework.base.utils.lang.PrimitiveUtils;
import org.liujk.java.framework.base.utils.lang.object.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 说明：
 * <p>
 * 将java对象转换为Map
 *
 */
public class BeanToMap extends AbstractBeanUtils {
    private static final String PACKAGE_NAME = getPackageName (BeanToMap.class);
    private static final Map<Key, I> CACHE = Maps.newConcurrentMap ();
    private static final Logger logger = LoggerFactory.getLogger (BeanToMap.class);
    /**
     * 打印生成源代码
     */
    public static boolean logSource = false;
    public static String dumpClass = null;
    /**
     * 不转换为Map的类
     */
    private static Set<Class<?>> ignoreToMapClasses = Sets.newHashSet ();

    /**
     * 设置不转换为Map的类，全局有效
     *
     * @param clazz
     */
    public static void addIgnoreToMapClasse (Class<?> clazz) {
        ignoreToMapClasses.add (clazz);
    }

    /**
     * 比较两个对象的属性值<br/>
     * 如果对象为null，等价于对象的所有属性值都为null；<br/>
     * 如果传入的对象都为null，返回null；<br/>
     * 默认返回不等的属性
     *
     * @param obj             被转换的对象
     * @param ignorePropeties 忽略比对的属性，如果没有则不传。
     *
     * @return
     */
    public static Map<String, Object> convert (Object obj, String... ignorePropeties) {
        return convert (obj, ConvertStrategy.IGNORE_NULL, ignorePropeties);
    }

    /**
     * 比较两个对象的属性值<br/>
     * 如果对象为null，等价于对象的所有属性值都为null；<br/>
     * 如果传入的对象都为null，返回null；<br/>
     * 默认返回不等的属性
     *
     * @param obj
     * @param strategy
     * @param ignorePropeties
     *
     * @return
     */
    public static Map<String, Object> convert (Object obj, ConvertStrategy strategy,
                                               String... ignorePropeties) {
        if (obj == null) {
            // 如果传入的对象都为null，返回null
            return null;
        }
        if (strategy == null) {
            strategy = ConvertStrategy.IGNORE_NULL;
        }

        if (obj instanceof Map) {
            return convert ((Map) obj);
        }

        Key key = new Key (obj.getClass (), strategy, ignorePropeties);
        I i = CACHE.get (key);
        if (i == null) {
            synchronized (BeanToMap.class) {
                i = CACHE.get (key);
                if (i == null) {
                    Generator generator = new Generator ();
                    generator.setSource (obj.getClass ());
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
        return i.convert (obj);
    }

    /**
     * 将List的内容转换类 List<Map>
     *
     * @param list
     *
     * @return
     */
    public static List<Object> convert (List<?> list) {
        if (list == null) {
            return null;
        }
        List<Object> mapList = Lists.newArrayList ();
        for (Object obj : list) {
            if (obj != null && needToMap (obj.getClass ())) {
                mapList.add (convert (obj));
            } else {
                mapList.add (obj);
            }
        }
        return mapList;
    }

    /**
     * 将Set的内容转换类 Set<Map>
     *
     * @param set
     *
     * @return
     */
    public static Set<Object> convert (Set<?> set) {
        if (set == null) {
            return null;
        }
        Set<Object> mapList = Sets.newHashSet ();
        for (Object obj : set) {
            if (obj != null && needToMap (obj.getClass ())) {
                mapList.add (convert (obj));
            } else {
                mapList.add (obj);
            }
        }
        return mapList;
    }

    /**
     * 将Map的内容转换类 Map<String,Map>
     *
     * @param map
     *
     * @return
     */
    public static Map<String, Object> convert (Map<?, ?> map) {
        if (map == null) {
            return null;
        }
        Map<String, Object> mapList = Maps.newLinkedHashMap ();
        for (Map.Entry<?, ?> entry : map.entrySet ()) {
            if (entry != null && needToMap (entry.getValue ().getClass ())) {
                mapList.put (entry.getKey () == null ? null : entry.getKey ().toString (), convert (entry.getValue ()));
            } else {
                mapList.put (entry.getKey () == null ? null : entry.getKey ().toString (), entry.getValue ());
            }
        }
        return mapList;
    }

    /**
     * 将数组的内容转换类 Map[]
     *
     * @param array
     *
     * @return
     */
    public static Object[] convert (Object[] array) {
        if (array == null) {
            return null;
        }
        Object[] mapArray = new Object[1];
        for (int i = 0, len = array.length; i < len; i++) {
            Object obj = array[i];
            if (obj == null) {
                mapArray[i] = null;
            } else {
                if (needToMap (obj.getClass ())) {
                    mapArray[i] = convert (obj);
                } else {
                    mapArray[i] = obj;
                }
            }
        }
        return mapArray;
    }

    /**
     * 判断 给定的类是否需要转为Map
     *
     * @param clazz
     *
     * @return
     */
    private static boolean needToMap (Class<?> clazz) {
        if (String.class.equals (clazz)
                // 类是不是公有类的话，无法访问内部，所以不转Map
                || (clazz.getModifiers () & Modifier.PUBLIC) < 0
                // 数组暂时不转Map
                || clazz.isArray ()
                // 枚举不转Map
                || clazz.isEnum ()
                // 基础对象不转Map
                || clazz.isPrimitive ()
                // 包装类不转Map
                || PrimitiveUtils.isWrapperClass (clazz)
                // Date类以及子类不转Map
                || Date.class.isAssignableFrom (clazz)
                // Money类以及子类不转Map
                || Money.class.isAssignableFrom (clazz)
                // 用户指定的类不转Map
                || ignoreToMapClasses.contains (clazz)) {
            return false;
        } else {
            return true;
        }
    }

    public static interface I {
        Map<String, Object> convert(Object obj);
    }

    public static class Generator extends AbstractGenerator {
        public Class<I> generate () {
            return super.generate (I.class);
        }

        @Override
        protected void generateBegin () {
            beginSource = "public java.util.Map convert( Object obj){\n";
            // 强制转换源对象
            String convertSource = String.format ("%s %s=(%s)obj;\n", source.getName (), SOURCE,
                                                  source.getName (), SOURCE);
            beginSource += convertSource;
        }

        @Override
        protected void generateEnd () {
            endSources = " return map;}";
        }

        @Override
        protected void generateBody () {
            Map<String, PropertyDescriptor> pdsMap = getPropertyDescriptors ();
            bodySources.add (String.format ("if(%s==null){\nreturn null;\n}\n\n", SOURCE));
            bodySources
                    .add (String.format ("java.util.Map map = new java.util.LinkedHashMap (%d);\n\n",
                                         pdsMap.size () + 1));
            bodySources.add ("Object value;\n");

            for (Map.Entry<String, PropertyDescriptor> entry : pdsMap.entrySet ()) {
                PropertyDescriptor pd = entry.getValue ();

                Method read = pd.getReadMethod ();
                if (read == null) {
                    continue;
                }
                // 处理不规范的命名 比如首字母大写
                if (ignorePropeties.contains (pd.getName ())
                        || ignorePropeties.contains (entry.getKey ())) {
                    continue;
                }
                // 是否是包装类转换
                Class<?> propertyType = pd.getPropertyType ();

                if (propertyType.isPrimitive ()) {
                    bodySources.add (String.format ("value = (Object)%s.value(%s);\n",
                                                    PrimitiveUtils.class.getName (), buildReadMethod (SOURCE, pd)));
                } else {
                    if (List.class.isAssignableFrom (propertyType)) {
                        bodySources.add (String.format ("value = %s.convert((java.util.List)%s);\n",
                                                        BeanToMap.class.getName (), buildReadMethod (SOURCE, pd)));
                    } else if (Set.class.isAssignableFrom (propertyType)) {
                        bodySources.add (String.format ("value = %s.convert((java.util.Set)%s);\n",
                                                        BeanToMap.class.getName (), buildReadMethod (SOURCE, pd)));
                    } else if (Map.class.isAssignableFrom (propertyType)) {
                        bodySources.add (String.format ("value = %s.convert((java.util.Map)%s);\n",
                                                        BeanToMap.class.getName (), buildReadMethod (SOURCE, pd)));
                    } else if (propertyType.isArray ()
                            && needToMap (propertyType.getComponentType ())) {
                        bodySources.add (String.format ("value = %s.convert(%s);\n",
                                                        BeanToMap.class.getName (), buildReadMethod (SOURCE, pd)));
                    } else if (needToMap (propertyType)) {
                        bodySources.add (String.format ("value = %s.convert(%s,null);\n",
                                                        BeanToMap.class.getName (), buildReadMethod (SOURCE, pd)));
                    } else {
                        bodySources
                                .add (String.format ("value = %s;\n", buildReadMethod (SOURCE, pd)));
                    }
                }

                if (strategy == ConvertStrategy.IGNORE_NULL) {
                    bodySources.add ("if(value != null ){\n");
                }
                bodySources.add (String.format ("map.put(\"%s\",value);\n", entry.getKey ()));
                if (strategy == ConvertStrategy.IGNORE_NULL) {
                    bodySources.add ("}\n");
                }
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
            return PACKAGE_NAME + ".BeanToMapImpl";
        }
    }
}
