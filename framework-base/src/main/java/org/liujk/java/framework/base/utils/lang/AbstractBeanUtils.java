package org.liujk.java.framework.base.utils.lang;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AbstractBeanUtils {

    protected static final String NULL = "null";
    protected static final Map<ClassLoader, ClassPool> CLASS_POOL_CACHE = Maps.newConcurrentMap();

    protected static String getPackageName(Class<?> clazz) {
        String className = clazz.getName();
        int lastDotIndex = className.lastIndexOf(".");
        return lastDotIndex != -1 ? className.substring(0, lastDotIndex) : "";
    }

    protected static <T> T notNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }

    protected static <T> T notNull(T obj) {
        return notNull(obj, null);
    }

    public static enum ConvertStrategy {

        //复制时忽略null对象，源对象属性为null，目标对象属性不会被设置为null
        IGNORE_NULL,
        //忽略null对象，并且忽略大小写
        IGNORE_NULL_AND_IGNORE_CASE,
        //复制时包含null对象。源对象属性为null，目标对象属性会被设置成null
        CONTAIN_NULL,
        //复制时包含null，并且忽略大小写
        CONTAIN_NULL_AND_IGNORE_CASE;

        /**
         * 判断给定的枚举是否在列表中
         *
         * @param values
         * @return
         */
        public boolean isInList(ConvertStrategy... values) {
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

        public Key(Class<?> objectClass, ConvertStrategy strategy, String[] ignoreProperties) {
            this.objectClass = objectClass;
            this.strategy = strategy;
            this.ignoreProperties = ignoreProperties;
        }

        @Override
        public int hashCode() {
            int result = objectClass != null ? objectClass.hashCode() : 0;
            result = 31 * result + Arrays.hashCode(ignoreProperties);
            result = 31 * result + (strategy != null ? strategy.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Key key = (Key) obj;
            if (objectClass != null ? !objectClass.equals(key.objectClass) : key.objectClass != null) {
                return false;
            }

            if (Arrays.equals(ignoreProperties, key.ignoreProperties)) {
                return false;
            }
            return strategy == key.strategy;
        }
    }


    /**
     * 根据classloader创建classpool
     */
    public static abstract class AbstarctGenerator {

        protected static final String SOURCE = "s";
        protected static AtomicInteger classNameIndex = new AtomicInteger(100000);
        protected List<String> bodySources = Lists.newArrayList();
        protected Map<String, Field> sourceFieldMap = null;
        protected Set<String> ignoreProperties = null;
        protected Class<?> source;
        protected String beginSource;
        protected String endSource;
        protected ConvertStrategy strategy = ConvertStrategy.IGNORE_NULL_AND_IGNORE_CASE;

        public static ClassPool getClassPool(ClassLoader loader) {
            if (loader == null) {
                return ClassPool.getDefault();
            }
            ClassPool pool = CLASS_POOL_CACHE.get(loader);
            if (pool == null) {
                pool = new ClassPool(true);
                pool.appendClassPath(new LoaderClassPath(loader));
                CLASS_POOL_CACHE.putIfAbsent(loader, pool);
                pool = CLASS_POOL_CACHE.get(loader);
            }
            return pool;
        }

        public static boolean isWrapClass(Class<?> clazz) {
            try {
                return ((Class<?>) clazz.getField("TYPE").get(null)).isPrimitive();
            } catch (Exception e) {
                return false;
            }
        }

        public void setIgnoreProperties(Set<String> ignoreProperties) {
            this.ignoreProperties = ignoreProperties;
        }

        public void setSource(Class<?> source) {
            this.source = source;
        }

        public void setStrategy(ConvertStrategy strategy) {
            if (strategy != null) {
                this.strategy = strategy;
            }
        }

        public void setIgnoreProperties(String[] ignoreProperties) {
            if (ignoreProperties != null) {
                this.ignoreProperties = Sets.newHashSet(ignoreProperties);
            } else {
                this.ignoreProperties = Sets.newHashSet();
            }
        }

        protected abstract boolean isLogSource();

        protected abstract Logger getLogger();

        protected abstract String getDumpClass();

        protected abstract String getPackageName();

        protected abstract void generateBegin();

        protected abstract void genrateBody();

        protected abstract void generateEnd();

        public <T> Class<T> generate(Class<T> clazz) {

            parseFields();

            generateBegin();
            genrateBody();
            generateEnd();

            StringBuilder sb = new StringBuilder();
            sb.append(beginSource);
            for (String propSource : bodySources) {
                sb.append(propSource);
            }
            sb.append(endSource);

            String source = sb.toString();
            if (isLogSource()) {
                getLogger().info("\n\n\n{}\n\n\n", source);
            }

            //todo overwrite classutils
            ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
            ClassPool pool = getClassPool(classLoader);
            ClassClassPath classPath = new ClassClassPath(this.getClass());
            pool.insertClassPath(classPath);
            CtClass cc = pool.makeClass(getPackageName() + classNameIndex.incrementAndGet());

            Class<T> copyClass = null;
            try {
                cc.addInterface(pool.get(clazz.getName()));

                String loggerField = String.format(" protected static final %s logger = %s.getLogger(\"%s\");"
                        , Logger.class.getName(), LoggerFactory.class.getName(), clazz.getName());

                CtField field = CtField.make(source, cc);
                cc.addField(field);

                CtMethod m = CtNewMethod.make(source, cc);
                cc.addMethod(m);
                if (getDumpClass() != null) {
                    CtClass.debugDump = getDumpClass();
                }
                getLogger().debug("classloader:{}", classLoader);
                copyClass = cc.toClass(classLoader, null);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return copyClass;
        }

        protected void parseFields() {
            sourceFieldMap = getDeclaredFieldMap(source);
        }

        protected Map<String, Field> getDeclaredFieldMap(Class<?> source) {
            Map<String, Field> declaredFieldMap = Maps.newHashMap();
            for (Class acls = source; acls != Object.class; acls = acls.getSuperclass()) {
                Field[] fields = acls.getDeclaredFields();
                for (Field field : fields) {
                    if (!declaredFieldMap.containsKey(field.getName())) {
                        declaredFieldMap.put(field.getName(), field);
                    }
                }
            }
            return declaredFieldMap;
        }

        public String buildReadMethod(String objName, PropertyDescriptor pd) {
            String readMethod = String.format("%s.%s()", objName, pd.getReadMethod().getName());
            return readMethod;
        }

        public String buildClassName(PropertyDescriptor pd) {
            String className;
            if (pd.getPropertyType().isArray()) {
                className = String.format("%s[].class", pd.getPropertyType().getComponentType().getName());
            } else {
                className = String.format("%s.class", pd.getPropertyType().getName());
            }
            return className;
        }

        public Map<String, PropertyDescriptor> getPropertyDescriptor() {
            Class clazz = this.source;
            PropertyDescriptor[] propertyDescriptors = null;
//                    todo  overwrite ReflectionUtils
//                    ReflectionUtils.
            Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                Field field = sourceFieldMap.get(propertyDescriptor.getName());
                if (field == null) {
                    field = sourceFieldMap.get(uppercaseFirstChar(propertyDescriptor.getName()));
                }
                if (field == null) {
                    continue;
                }
                propertyDescriptorMap.put(field.getName(), propertyDescriptor);
            }
            return propertyDescriptorMap;

        }

        private String uppercaseFirstChar(String str) {
            char[] chars = str.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }

        protected String buildTypeName(PropertyDescriptor setter, boolean isPrimitive) {
            return buildTypeName(setter.getPropertyType(), isPrimitive);
        }

        protected String buildTypeName(Class<?> type, boolean isPrimitive) {
            String typeName = null;
            if (type.isArray()) {
//                todo overwrite ArrayUtils
//                int dn = ArrayUtils.coun
                int dn = 0;
                typeName = type.getComponentType().getName();
                for (int i = 0; i < dn; i++) {
                    typeName += "[]";
                }
            } else {
                typeName = type.getName();
            }
            if (typeName.indexOf('$') > 0) {
                typeName = typeName.replace('$', '.');
            }
            return typeName;
        }

    }

}
