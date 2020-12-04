
package org.liujk.java.framework.base.utils.lang.beans;



import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.liujk.java.framework.base.exceptions.Exceptions;
import org.liujk.java.framework.base.utils.lang.ArrayUtils;
import org.liujk.java.framework.base.utils.lang.ObjectUtils;
import org.liujk.java.framework.base.utils.lang.PrimitiveUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * 说明：
 * <p>
 * 方法执行器，动态生成代码，比直接使用反射效率高
 *
 */
public class BeanInstance extends AbstractBeanUtils {
    protected static final Map<Key, I> CACHE = Maps.newConcurrentMap ();
    private static final String PACKAGE_NAME = getPackageName (BeanInstance.class);
    private static final Logger LOGGER = LoggerFactory
            .getLogger (BeanInstance.class);
    /**
     * 打印生成源代码
     */
    public static boolean logSource = false;
    public static String dumpClass = null;
    private static ThreadLocal<Set<ListClassKey>> threadLocal = new ThreadLocal<> ();
    private static List<String> EMPTY_KEY = Lists.newArrayList ();

    /**
     * 相当于执行 class.newInstance(args);
     *
     * @param clazz
     * @param args
     * @param <T>
     *
     * @return
     */
    public static <T> T newInstance (Class<T> clazz, Object... args) {
        return newInstance0 (clazz, args);
    }

    public static <T> I getInterface (Class<T> clazz, Object... args) {
        return getInterface0 (clazz, args);
    }

    private static <T> T newInstance0 (Class<T> clazz, Object... args) {
        I<T> i = getInterface0 (clazz, args);
        return i.newInstance (args);
    }

    private static <T> I<T> getInterface0 (Class<T> clazz, Object... args) {
        ObjectUtils.notNull (clazz, "class不能为空");
        Class<?>[] parameterTypes = new Class[]{};
        if (ArrayUtils.isNotEmpty (args)) {
            parameterTypes = buildParameterTypes (args);
            args = checkArgs (clazz, args);
        }
        Key key = new Key (clazz, parameterTypes);
        I i = CACHE.get (key);
        if (i == null) {
            synchronized (BeanInstance.class) {
                i = CACHE.get (key);
                if (i == null) {
                    Constructor constructor = findConstructor (clazz, parameterTypes);
                    if (constructor == null) {
                        throw new RuntimeException (
                                String.format ("No found Constructorfor parameter types %s",
                                               ArrayUtils.toString (args)));
                    }

                    Generator generator = new Generator ();
                    generator.setSource (clazz);
                    generator.setConstructor (constructor);

                    try {
                        i = generator.generate ().newInstance ();
                        CACHE.put (key, i);
                    } catch (Exception e) {
                        throw Exceptions.newRuntimeExceptionWithoutStackTrace (e);
                    }
                }
            }
        }
        return i;
    }

    /**
     * 检查构造方法的参数
     *
     * @param clazz
     * @param args
     *
     * @return
     */
    protected static Object[] checkArgs (Class<?> clazz, Object[] args) {
        List<Constructor> constructorList = Lists.newArrayList ();

        // 只能拿到public方法（包括继承的类或接口的方法）
        Constructor[] constructors = clazz.getConstructors ();
        for (Constructor constructor : constructors) {
            if (constructor.getParameters ().length == args.length) {
                constructorList.add (constructor);
            }
        }
        if (constructorList.size () == 1 && constructorList.get (0).getParameterTypes ().length == 1
                && args.length > 1) {
            Class<?>[] parameterTypes = constructorList.get (0).getParameterTypes ();
            if (parameterTypes.length == 1 && parameterTypes[0].isArray ()) {
                return new Object[]{args};
            }
        }

        return args;
    }

    protected static Class<?>[] buildParameterTypes (Object... args) {
        Class<?>[] parameterTypes;
        if (args != null) {
            parameterTypes = new Class<?>[args.length];
            for (int i = 0, len = args.length; i < len; i++) {
                Object param = args[i];
                parameterTypes[i] = param == null ? Object.class : param.getClass ();
            }
        } else {
            parameterTypes = new Class<?>[0];
        }

        return parameterTypes;
    }

    private static List<String> buildKey (Class<?>[] parameterTypes, boolean wrap) {
        List<String> keys = Lists.newArrayList ();
        StringBuilder keyBuilder = new StringBuilder ();
        for (int i = 0, len = parameterTypes.length; i < len; i++) {
            keyBuilder.append ("#").append (parameterTypes[i].getName ());

            if (wrap) {
                if (PrimitiveUtils.isWrapperClass (parameterTypes[i])) {
                    Class<?>[] types = new Class<?>[parameterTypes.length];
                    System.arraycopy (parameterTypes, 0, types, 0, parameterTypes.length);
                    types[i] = PrimitiveUtils.getPrimitiveClass (parameterTypes[i]);

                    keys.addAll (buildKey (types, wrap));
                }
            }
        }
        // 开头的 “#” 不影响结果，不用去掉
        keys.add (keyBuilder.toString ());
        return keys;
    }

    private static Constructor findConstructor (Class<?> clazz, Class<?>[] parameterTypes) {
        Map<String, Constructor> constructorMap = Maps.newHashMap ();
        // 只能拿到public构造方法
        Constructor[] constructors = clazz.getConstructors ();
        for (Constructor constructor : constructors) {
            // 参数个数相同
            if (constructor.getParameterTypes ().length == parameterTypes.length) {
                String key = buildKey (constructor.getParameterTypes (), false).get (0);
                constructorMap.put (key, constructor);
            }
        }
        // 根据传入参数，查找方法
        threadLocal.set (new HashSet<ListClassKey> ());
        try {
            return findConstructor (parameterTypes, constructorMap);
        } finally {
            threadLocal.remove ();
        }

    }

    private static Constructor findConstructor (Class<?>[] parameterTypes,
                                                Map<String, Constructor> constructorMap) {
        ListClassKey paramKey = new ListClassKey (parameterTypes);
        if (threadLocal.get ().contains (paramKey)) {
            return null;
        } else {
            threadLocal.get ().add (paramKey);
        }

        List<String> keys = buildKey (parameterTypes, true);
        for (String key : keys) {
            if (constructorMap.containsKey (key)) {
                return constructorMap.get (key);
            }
        }

        List<Class<?>> supperClasses = Lists.newArrayList ();

        Constructor constructor = null;
        for (int i = 0, len = parameterTypes.length; i < len; i++) {
            if (Object.class.equals (parameterTypes[i])) {
                continue;
            }

            Class<?> supperClass = parameterTypes[i].getSuperclass ();
            if (supperClass != null) {
                supperClasses.add (supperClass);
            }
            Class<?>[] interfaces = parameterTypes[i].getInterfaces ();
            if (ArrayUtils.isNotEmpty (interfaces)) {
                supperClasses.addAll (Arrays.asList (interfaces));
            }

            for (Class<?> superClass : supperClasses) {
                Class<?>[] types = new Class<?>[parameterTypes.length];
                System.arraycopy (parameterTypes, 0, types, 0, parameterTypes.length);
                types[i] = superClass;

                constructor = findConstructor (types, constructorMap);
                if (constructor != null) {
                    return constructor;
                }
            }
        }
        return null;
    }

    public static interface I<T> {
        T newInstance(Object... args);
    }

    public static class Generator extends AbstractGenerator {

        private Constructor constructor;

        @SuppressWarnings({"unchecked"})
        public Class<I> generate () {
            return super.generate (I.class);
        }

        @Override
        protected void generateBegin () {
            beginSource = "public Object newInstance(Object[] args){\n";
        }

        @Override
        protected void generateEnd () {
            endSources = "\n}";
        }

        @Override
        protected void generateBody () {

            bodySources.add ("  return ");
            bodySources.add (String.format (" new %s( ", source.getName (), constructor.getName ()));
            int i = 0;
            for (Class<?> type : constructor.getParameterTypes ()) {
                if (i > 0) {
                    bodySources.add (", ");
                }
                if (type.isPrimitive ()) {
                    Class<?> wrapper = PrimitiveUtils.getWrapperClass (type.getName ());
                    bodySources.add (String.format ("%s.value((%s)args[%d])",
                                                    PrimitiveUtils.class.getName (), wrapper.getName (), i++));
                } else if (type.isArray ()) {
                    bodySources.add (String.format ("(%s[])args[%d]",
                                                    type.getComponentType ().getName (), i++));
                } else {
                    bodySources.add (String.format ("(%s)args[%d]", type.getName (), i++));
                }
            }
            bodySources.add (String.format (" );"));
        }

        @Override
        protected boolean isLogSource () {
            return logSource;
        }

        @Override
        protected Logger getLogger () {
            return LOGGER;
        }

        @Override
        protected String getDumpClass () {
            return dumpClass;
        }

        @Override
        protected String getPackageName () {
            return PACKAGE_NAME + ".BeanInstanceImpl";
        }

        public void setConstructor (Constructor constructor) {
            this.constructor = constructor;
        }

    }

    protected static class Key {
        private Class<?> objectClass;
        private Class<?>[] parameterTypes;

        public Key (Class<?> objectClass, Class<?>[] parameterTypes) {
            this.objectClass = objectClass;
            this.parameterTypes = parameterTypes;
        }

        public Class<?> getObjectClass () {
            return objectClass;
        }

        public void setObjectClass (Class<?> objectClass) {
            this.objectClass = objectClass;
        }

        public Class<?>[] getParameterTypes () {
            return parameterTypes;
        }

        public void setParameterTypes (Class<?>[] parameterTypes) {
            this.parameterTypes = parameterTypes;
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
            return Arrays.equals (parameterTypes, key.parameterTypes);

        }

        @Override
        public int hashCode () {
            int result = objectClass != null ? objectClass.hashCode () : 0;
            result = 31 * result + Arrays.hashCode (parameterTypes);
            return result;
        }

        @Override
        public String toString () {
            StringBuilder infoBuilder = new StringBuilder ();
            infoBuilder.append (objectClass.getName ());
            infoBuilder.append ("(");
            for (Class<?> type : parameterTypes) {
                if (type.isArray ()) {
                    infoBuilder.append (type.getComponentType ().getName ()).append ("[], ");
                } else {
                    infoBuilder.append (type.getName ()).append (", ");
                }
            }
            infoBuilder.append (")");
            return infoBuilder.toString ();
        }
    }

    protected static class ListClassKey {
        Class<?>[] types;

        public ListClassKey (Class<?>[] types) {
            this.types = types;
        }

        public Class<?>[] getTypes () {
            return types;
        }

        public void setTypes (Class<?>[] types) {
            this.types = types;
        }

        @Override
        public boolean equals (Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass () != o.getClass ()) {
                return false;
            }

            ListClassKey that = (ListClassKey) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals (types, that.types);

        }

        @Override
        public int hashCode () {
            return Arrays.hashCode (types);
        }

    }
}
