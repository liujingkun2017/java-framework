
package org.liujk.java.framework.base.utils.lang.beans;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import javassist.*;
import org.liujk.java.framework.base.utils.lang.ArrayUtils;
import org.liujk.java.framework.base.utils.lang.ClassUtils;
import org.liujk.java.framework.base.utils.lang.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 说明：
 * <p>
 *
 */
public abstract class AbstractBeanUtils {
    protected static final String NULL = "null";
    protected static final Map<ClassLoader, ClassPool> CLASS_POOL_CACHE = Maps.newConcurrentMap ();


    protected static String getPackageName (Class<?> clazz) {
        String className = clazz.getName ();
        int lastDotIndex = className.lastIndexOf (".");
        return (lastDotIndex != -1 ? className.substring (0, lastDotIndex) : "");
    }

    protected static <T> T notNull (T obj, String message) {
        if (obj == null) {
            throw new NullPointerException (message);
        }
        return obj;
    }

    protected static <T> T notNull (T obj) {
        return notNull (obj, null);
    }

    public static enum ConvertStrategy {
        /**
         * 复制时忽略null对象，源对象属性为null，目标对象属性不会被设置为null
         */
        IGNORE_NULL,
        /**
         * 忽略null对象，并且忽略属性大小写
         */
        IGNORE_NULL_AND_IGNORE_CASE,
        /**
         * 复制时包括null对象。源对象属性为null，目标对象属性会被设置为null
         */
        CONTAIN_NULL,
        /**
         * 复制时包含null，但忽略大小写
         */
        CONTAIN_NULL_AND_IGNORE_CASE;

        /**
         * 判断给定的枚举，是否在列表中
         *
         * @param values 列表
         *
         * @return
         */
        public boolean isInList (ConvertStrategy... values) {
            for (ConvertStrategy e : values) {
                if (this == e) {
                    return true;
                }
            }
            return false;
        }
    }

    protected static class Key {
        private Class<?> objectClass;
        private String[] ignoreProperties;
        private ConvertStrategy strategy;

        public Key (Class<?> objectClass, ConvertStrategy strategy, String[] ignoreProperties) {
            this.objectClass = objectClass;
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

            if (objectClass != null ? !objectClass.equals (key.objectClass)
                    : key.objectClass != null) {
                return false;
            }
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            if (!Arrays.equals (ignoreProperties, key.ignoreProperties)) {
                return false;
            }
            return strategy == key.strategy;

        }

        @Override
        public int hashCode () {
            int result = objectClass != null ? objectClass.hashCode () : 0;
            result = 31 * result + Arrays.hashCode (ignoreProperties);
            result = 31 * result + (strategy != null ? strategy.hashCode () : 0);
            return result;
        }

        @Override
        public String toString () {
            return "Key[" + (objectClass == null ? "NULL" : objectClass.getSimpleName ()) +
                    (strategy == null ? "" : (":" + strategy.name ())) +
                    "]";
        }
    }

    public static abstract class AbstractGenerator {
        protected static final String SOURCE = "s";
        protected static AtomicInteger classNameIndex = new AtomicInteger (100000);
        protected List<String> bodySources = Lists.newArrayList ();
        protected Map<String, Field> sourceFieldMap = null;
        protected Set<String> ignorePropeties = null;
        protected Class<?> source;
        protected String beginSource;
        protected String endSources;

        protected ConvertStrategy strategy = ConvertStrategy.IGNORE_NULL_AND_IGNORE_CASE;

        /**
         * 根据ClassLoader创建 ClassPool
         *
         * @param loader
         *
         * @return
         */
        public static ClassPool getClassPool (ClassLoader loader) {
            if (loader == null) {
                return ClassPool.getDefault ();
            }
            ClassPool pool = CLASS_POOL_CACHE.get (loader);
            if (pool == null) {
                pool = new ClassPool (true);
                pool.appendClassPath (new LoaderClassPath (loader));
                // 如果缓存存在就不放入缓存
                ((ConcurrentHashMap) CLASS_POOL_CACHE).putIfAbsent (loader, pool);
                // 从缓存中获取pool，以非同步的方式实现同步
                pool = CLASS_POOL_CACHE.get (loader);
            }
            return pool;
        }

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

        public void setIgnorePropeties (Set<String> ignorePropeties) {
            this.ignorePropeties = ignorePropeties;
        }

        public void setSource (Class<?> source) {
            this.source = source;
        }

        public void setStrategy (ConvertStrategy strategy) {
            if (strategy != null) {
                this.strategy = strategy;
            }
        }

        public void setIgnorePropeties (String[] ignorePropeties) {
            if (ignorePropeties != null) {
                this.ignorePropeties = Sets.newHashSet (ignorePropeties);
            } else {
                this.ignorePropeties = Sets.newHashSet ();
            }
        }

        protected abstract boolean isLogSource ();

        protected abstract Logger getLogger ();

        protected abstract String getDumpClass ();

        protected abstract String getPackageName ();

        protected abstract void generateBegin ();

        protected abstract void generateBody ();

        protected abstract void generateEnd ();

        public <T> Class<T> generate (Class<T> clazz) {
            parseFields ();

            generateBegin ();
            generateBody ();
            generateEnd ();
            StringBuilder sb = new StringBuilder ();
            sb.append (beginSource);
            for (String propSource : bodySources) {
                sb.append (propSource);
            }
            sb.append (endSources);
            String source = sb.toString ();
            if (isLogSource ()) {
                getLogger ().info ("\n\n\n{}\n\n\n", source);
            }
            ClassLoader classLoader = ClassUtils.getDefaultClassLoader ();
            ClassPool pool = getClassPool (classLoader);
            ClassClassPath classPath = new ClassClassPath (this.getClass ());
            pool.insertClassPath (classPath);
            CtClass cc = pool.makeClass (getPackageName () + classNameIndex.incrementAndGet ());

            Class<T> copyClass = null;
            try {
                cc.addInterface (pool.get (clazz.getName ()));

                String loggerField = String.format (
                        " protected static final %s logger = %s.getLogger(\"%s\"); ",
                        Logger.class.getName (), LoggerFactory.class.getName (), clazz.getName ());
                CtField field = CtField.make (loggerField, cc);
                cc.addField (field);

                CtMethod m = CtNewMethod.make (source, cc);
                cc.addMethod (m);
                if (getDumpClass () != null) {
                    CtClass.debugDump = getDumpClass ();
                }
                getLogger ().debug ("classloader:{}", classLoader);
                copyClass = cc.toClass (classLoader, null);
            } catch (Exception e) {
                throw new RuntimeException (e);
            }
            return copyClass;

        }

        protected Map<String, Field> getDeclaredFieldMap (Class<?> source) {
            Map<String, Field> declaredFieldMap = Maps.newHashMap ();


            for (Class acls = source; acls != Object.class; acls = acls.getSuperclass ()) {
                Field[] fields = acls.getDeclaredFields ();
                for (Field field : fields) {
                    if (!declaredFieldMap.containsKey (field.getName ())) {
                        declaredFieldMap.put (field.getName (), field);
                    }
                }
            }

            return declaredFieldMap;
        }

        protected void parseFields () {
            sourceFieldMap = getDeclaredFieldMap (source);
        }

        public String buildReadMethod (String objName, PropertyDescriptor pd) {
            String readMethod = String.format ("%s.%s()", objName, pd.getReadMethod ().getName ());
            return readMethod;
        }

        public String buildClassName (PropertyDescriptor pd) {
            String className;
            if (pd.getPropertyType ().isArray ()) {
                className = String.format ("%s[].class",
                                           pd.getPropertyType ().getComponentType ().getName ());
            } else {
                className = String.format ("%s.class", pd.getPropertyType ().getName ());
            }
            return className;
        }

        public Map<String, PropertyDescriptor> getPropertyDescriptors () {
            Class clazz = this.source;

            PropertyDescriptor[] propertyDescriptors = ReflectionUtils
                    .getPropertyDescriptors (clazz);
            Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<String, PropertyDescriptor> ();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                Field field = sourceFieldMap.get (propertyDescriptor.getName ());
                if (field == null) {
                    field = sourceFieldMap.get (uppercaseFirstChar (propertyDescriptor.getName ()));
                }
                if (field == null) {
                    continue;
                }
                propertyDescriptorMap.put (field.getName (), propertyDescriptor);
            }
            return propertyDescriptorMap;
        }

        private String uppercaseFirstChar (String str) {
            char[] chars = str.toCharArray ();
            chars[0] = Character.toUpperCase (chars[0]);
            return new String (chars);
        }

        protected String buildTypeName (PropertyDescriptor setter, boolean isPrimitive) {
            return buildTypeName (setter.getPropertyType (), isPrimitive);
        }

        protected String buildTypeName (Class<?> type, boolean isPrimitive) {
            String typeName;
            if (type.isArray ()) {
                int dn = ArrayUtils.countDimension (type);
                typeName = type.getComponentType ().getName ();
                for (int i = 0; i < dn; i++) {
                    typeName += "[]";
                }
            } else {
                typeName = type.getName ();
            }
            if (typeName.indexOf ('$') > 0) {
                typeName = typeName.replace ('$', '.');
            }
            return typeName;
        }

        protected boolean isIgnoredProperty (PropertyDescriptor pd) {
            return ignorePropeties.contains (pd.getName ());
        }
    }

}
