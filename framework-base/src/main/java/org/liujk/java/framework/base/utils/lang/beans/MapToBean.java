
package org.liujk.java.framework.base.utils.lang.beans;



import com.google.common.collect.Maps;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.liujk.java.framework.base.exceptions.Exceptions;
import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.lang.PrimitiveUtils;
import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.base.utils.lang.ThrowableUtils;
import org.liujk.java.framework.base.utils.lang.beans.converter.TypeConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 说明：
 * <p>
 * 将Map对象转换为Bean
 *
 */
public class MapToBean extends AbstractBeanUtils {
    private static final String PACKAGE_NAME = AbstractBeanUtils
            .getPackageName (MapToBean.class);
    private static final Map<Key, I> CACHE = Maps.newConcurrentMap ();
    private static final Logger logger = LoggerFactory.getLogger (MapToBean.class);
    /**
     * 打印生成源代码
     */
    public static boolean logSource = false;
    public static String dumpClass = null;

    /**
     * 将Map格式的对象转换为Bean<br/>
     *
     * @param map             Map形式的对象
     * @param clazz           被转换的对象类型
     * @param ignorePropeties 忽略比对的属性，如果没有则不传。
     *
     * @return
     */
    public static <T> T convert (Map<String, Object> map, Class<T> clazz,
                                 String... ignorePropeties) {
        return convert (map, clazz, ConvertStrategy.IGNORE_NULL_AND_IGNORE_CASE, ignorePropeties);
    }

    /**
     * 将Map格式的对象转换为Bean<br/>
     *
     * @param map             属性Map
     * @param obj             目标对象
     * @param ignorePropeties 忽略的属性
     * @param <T>
     *
     * @return
     */
    public static <T> T convert (Map<String, Object> map, T obj, String... ignorePropeties) {
        return convert (map, obj, ConvertStrategy.IGNORE_NULL_AND_IGNORE_CASE, ignorePropeties);
    }

    /**
     * 将Map格式的对象转换为Bean<br/>
     *
     * @param map             属性Map
     * @param clazz           目标对象class
     * @param strategy        转换方式
     * @param ignorePropeties 忽略的属性
     * @param <T>
     *
     * @return
     */
    public static <T> T convert (Map<String, Object> map, Class<T> clazz, ConvertStrategy strategy,
                                 String... ignorePropeties) {
        notNull (clazz, "clazz不能为空");
        try {
            T target = clazz.newInstance ();
            convert (map, target, strategy, ignorePropeties);
            return target;
        } catch (Exception e) {
            throw Exceptions.newRuntimeException (e);
        }
    }

    /**
     * 将Map格式的对象转换为Bean<br/>
     *
     * @param map             属性Map
     * @param obj             目标对象
     * @param strategy        转换方式
     * @param ignorePropeties 忽略的属性
     * @param <T>
     *
     * @return
     */
    public static <T> T convert (Map<String, Object> map, Object obj, ConvertStrategy strategy,
                                 String... ignorePropeties) {
        notNull (obj, "目标对象不能为空");
        if (strategy == null) {
            strategy = ConvertStrategy.IGNORE_NULL_AND_IGNORE_CASE;
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

        return (T) i.convert (initMap (map, strategy), obj);
    }

    /**
     * 初始化Map对象，如果Map对象的Key值是"."分割的形式，将他们转化为子Map
     *
     * @param map
     *
     * @return
     */
    private static Map<String, Object> initMap (Map<String, Object> map, ConvertStrategy strategy) {
        if (CollectionUtils.isEmpty (map)) {
            return map;
        }
        Map<String, Object> newMap;

        if (strategy == ConvertStrategy.IGNORE_NULL_AND_IGNORE_CASE ||
                strategy == ConvertStrategy.CONTAIN_NULL_AND_IGNORE_CASE) {
            newMap = new CaseInsensitiveMap ();
        } else {
            newMap = Maps.newHashMap ();
        }
        for (Map.Entry<String, Object> entry : map.entrySet ()) {
            String key = entry.getKey ();
            if (key.indexOf ('.') > 0) {
                String[] keys = key.split ("\\.");

                Map<String, Object> subMap = newMap;
                for (int idx = 0, len = keys.length; idx < len; idx++) {
                    String subKey = keys[idx];
                    if (idx < len - 1) {
                        if (!newMap.containsKey (subKey)) {
                            subMap.put (subKey, Maps.newHashMap ());
                        }
                        subMap = (Map<String, Object>) subMap.get (subKey);
                    } else {
                        subMap.put (subKey, entry.getValue ());
                    }
                }
            } else {
                newMap.put (key, entry.getValue ());
            }
        }
        return newMap;
    }

    public static interface I {
        <T> T convert(Map<String, Object> map, T obj);
    }

    public static class Generator extends AbstractGenerator {
        @SuppressWarnings({"unchecked"})
        public Class<I> generate () {
            return super.generate (I.class);
        }

        @Override
        protected void generateBegin () {
            beginSource = "public Object convert(java.util.Map map,Object obj){\n";
            // 强制转换源对象
            String convertSource = String.format ("  %s %s=(%s)obj;\n", source.getName (), SOURCE,
                                                  source.getName (), SOURCE);
            beginSource += convertSource;
        }

        @Override
        protected void generateEnd () {
            endSources = "\n  return obj;\n}";
        }

        @Override
        protected void generateBody () {
            String sourceStr;
            Map<String, PropertyDescriptor> pdsMap = getPropertyDescriptors ();

            // if(s==null){return null}
            bodySources.add (String.format ("  if(%s==null){\n    return null;\n  }\n\n", SOURCE));
            // if(map==null || map.size()==0 ){return s}
            bodySources.add (String
                                     .format ("  if(map==null || map.size()==0){\n    return %s;\n  }\n\n", SOURCE));
            bodySources.add ("  Object value;\n");

            for (Map.Entry<String, PropertyDescriptor> entry : pdsMap.entrySet ()) {
                PropertyDescriptor pd = entry.getValue ();
                Method writeMethod = pd.getWriteMethod ();
                if (writeMethod == null) {
                    continue;
                }
                if (isIgnoredProperty (pd)) {
                    continue;
                }
                // value=map.get("xxx");
                bodySources.add (String.format ("\n  value = map.get(\"%s\");\n", pd.getName ()));

                // 是否是包装类转换
                Class<?> propertyType = pd.getPropertyType ();
                String writerMethodName = writeMethod.getName ();

                if (strategy == ConvertStrategy.IGNORE_NULL ||
                        strategy == ConvertStrategy.IGNORE_NULL_AND_IGNORE_CASE) {
                    sourceStr = "  if(value != null "
                            + "\n    || ((value instanceof java.lang.String) "
                            + String.format (
                            "\n       && %s.isNotBlank((java.lang.String) value))){\n",
                            StringUtils.class.getName ());
                    bodySources.add (sourceStr);
                }

                bodySources.add (String.format ("    try{\n"));

                // 如果原对象是Map，递归调用MapToBean
                bodySources.add (String.format ("      if( value instanceof java.util.Map ){\n"));
                if (Map.class.equals (propertyType)) {
                    // 如果对象属性是Map的话，直接设置值
                    bodySources.add (String.format ("          value =  value;\n"));
                } else {
                    bodySources.add (String.format (
                            "          value =  %s.convert((java.util.Map)value,%s.class,null);\n",
                            MapToBean.class.getName (), buildTypeName (propertyType, false)));
                }
                bodySources.add (String.format ("      }\n"));

                // s.setXXX(
                bodySources.add (String.format ("      %s.%s(", SOURCE, writerMethodName));

                bodySources.add (String.format ("(%s)", buildTypeName (propertyType, false)));
                if (propertyType.isPrimitive ()) {
                    bodySources.add (String.format ("%s.value((%s)", PrimitiveUtils.class.getName (),
                                                    PrimitiveUtils.getWrapperClass (propertyType.getName ())
                                                            .getName ()));
                } else {
                    bodySources.add (String.format ("%s.class.cast(", buildTypeName (pd, false)));
                }
                bodySources.add (String.format ("%s.convertValue( value , %s.class))",
                                                TypeConverterUtils.class.getName (),
                                                buildTypeName (propertyType, false)));
                bodySources.add (");\n");

                sourceStr = "    } catch (java.lang.Exception e){"
                        + "\n      logger.warn(\"%s\",value,%s.getCauseMessage(e));  "
                        + "\n    }\n";
                bodySources.add (String.format (sourceStr, "值[{}]转换异常,Cause by:{}",
                                                ThrowableUtils.class.getName ()));

                if (strategy == ConvertStrategy.IGNORE_NULL ||
                        strategy == ConvertStrategy.IGNORE_NULL_AND_IGNORE_CASE) {
                    bodySources.add ("  }\n");
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
            return PACKAGE_NAME + ".MapToBeanImpl";
        }
    }
}
