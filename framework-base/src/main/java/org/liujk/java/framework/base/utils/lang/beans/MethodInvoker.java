
package org.liujk.java.framework.base.utils.lang.beans;



import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.liujk.java.framework.base.exceptions.Exceptions;
import org.liujk.java.framework.base.utils.lang.ArrayUtils;
import org.liujk.java.framework.base.utils.lang.PrimitiveUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 说明：
 * <p>
 * 方法执行器，动态生成代码，比直接使用反射效率高
 *
 */
public class MethodInvoker extends AbstractBeanUtils {
    protected static final Map<Key, Invoker> CACHE = Maps.newConcurrentMap ();
    private static final String PACKAGE_NAME = getPackageName (MethodInvoker.class);
    private static final Logger logger = LoggerFactory
            .getLogger (MethodInvoker.class);
    /**
     * 打印生成源代码
     */
    public static boolean logSource = false;
    public static String dumpClass = null;
    private static ThreadLocal<Set<ListClassKey>> threadLocal = new ThreadLocal<> ();
    private static List<String> EMPTY_KEY = Lists.newArrayList ();

    /**
     * 执行静态方法
     * <p/>
     * 相当于执行 method.invoke(null,args);
     *
     * @param clazz      class
     * @param methodName 执行的静态方法名称，如果方法有重载，根据参数来确定方法
     * @param args       方法的参数，是个 Object[];<br/>
     *                   注意:如果方法的参数就是一个Object[] 或者 是 Object[]的子类， 需要用 new Object[]{args}
     *                   来封装，方法内部对没有重载的方法做了简单的判断
     * @param clazz
     * @param methodName
     * @param args
     *
     * @return
     */
    public static Object invoke (Class<?> clazz, String methodName, Object... args) {
        return invoke0 (clazz, methodName, args);
    }

    /**
     * 动态执行对象的方法
     * <p/>
     * 相当于执行 method.invoke(obj,args);
     *
     * @param obj        对象实例，如果是执行静态方法的话，obj可以是 Class
     * @param methodName 执行的方法名称，如果方法有重载，根据参数来确定方法
     * @param args       方法的参数，是个 Object[];<br/>
     *                   注意:如果方法的参数就是一个Object[] 或者 是 Object[]的子类， 需要用 new Object[]{args}
     *                   来封装，方法内部对没有重载的方法做了简单的判断
     *
     * @return 如果为void方法，返回null
     */
    public static Object invoke (Object obj, String methodName, Object... args) {
        return invoke0 (obj, methodName, args);
    }


    /**
     * 获取反射接口
     * <p>
     * 在获取反射接口时
     * 耗费的时间往往比正在执行调用的时候多得多
     * 因此提供获取接口方法，自己缓存，然后调用 invoke(obj, args)
     *
     * @param clazz
     * @param methodName
     * @param args
     *
     * @return
     */
    public static Invoker getInvoker (Class<?> clazz, String methodName, Object... args) {
        return getInvoker0 (clazz, methodName, args);
    }

    /**
     * 获取反射接口
     * <p>
     * 在获取反射接口时
     * 耗费的时间往往比正在执行调用的时候多得多
     * 因此提供获取接口方法，自己缓存，然后调用 invoke(obj, args)
     *
     * @param obj
     * @param methodName
     * @param args
     *
     * @return
     */
    public static Invoker getInvoker (Object obj, String methodName, Object... args) {
        return getInvoker0 (obj, methodName, args);
    }


    /**
     * 获取反射接口
     * <p>
     * 在获取反射接口时
     * 耗费的时间往往比正在执行调用的时候多得多
     * 因此提供获取接口方法，自己缓存，然后调用 invoke(obj, args)
     *
     * @param clazz
     * @param methodName
     * @param parameterTypes
     *
     * @return
     */
    public static Invoker getInvoker (Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return getInvoker0 (clazz, methodName, parameterTypes);
    }


    private static Object invoke0 (Object obj, String methodName, Object... args) {
        Invoker i = getInvoker0 (obj, methodName, args);
        return i.invoke (obj, args);
    }


    private static Invoker getInvoker0 (Object obj, String methodName, Object... args) {
        if (obj == null) {
            // 如果传入的对象都为null，返回null
            return null;
        }
        Class<?> clazz = (obj instanceof Class) ? (Class) obj : obj.getClass ();
        Class<?>[] parameterTypes = buildParameterTypes (args);
        Key key = new Key (clazz, methodName, parameterTypes);
        Invoker i = CACHE.get (key);
        if (i != null) {
            return i;
        }
        //这一步需要通过反射进行查找比较耗时
        args = checkArgs (clazz, methodName, args);
        parameterTypes = buildParameterTypes (args);
        return getInvoker0 (clazz, methodName, parameterTypes);
    }

    /**
     * 通过反射构建执行器
     *
     * @param clazz
     * @param methodName
     * @param parameterTypes
     *
     * @return
     */
    private static Invoker getInvoker0 (Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Key key = new Key (clazz, methodName, parameterTypes);
        Invoker i = CACHE.get (key);
        if (i == null) {
            synchronized (MethodInvoker.class) {
                i = CACHE.get (key);
                if (i == null) {
                    Method method = findMethod (clazz, methodName, parameterTypes);
                    if (method == null) {
                        throw new RuntimeException (
                                String.format ("No found method[%s] for parameter types %s",
                                               methodName, ArrayUtils.toString (parameterTypes)));
                    }
                    Generator generator = new Generator ();
                    generator.setSource (clazz);
                    generator.setMethod (method);

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

    private static Object[] checkArgs (Class<?> clazz, String methodName, Object[] args) {
        List<Method> methodList = Lists.newArrayList ();

        // 只能拿到public方法（包括继承的类或接口的方法）
        Method[] methods = clazz.getMethods ();
        for (Method method : methods) {
            // 方法名相同
            if (method.getName ().equals (methodName)) {
                methodList.add (method);
            }
        }

        if (methodList.size () == 1 && methodList.get (0).getParameterTypes ().length == 1
                && args.length > 1) {
            Class<?>[] parameterTypes = methodList.get (0).getParameterTypes ();
            if (parameterTypes.length == 1 && parameterTypes[0].isArray ()) {
                return new Object[]{args};
            }
        }

        return args;
    }

    private static Class<?>[] buildParameterTypes (Object... args) {
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

    private static Method findMethod (Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        Map<String, Method> methodMap = Maps.newHashMap ();
        // 只能拿到public方法（包括继承的类或接口的方法）
        Method[] methods = clazz.getMethods ();
        for (Method method : methods) {
            // 方法名相同
            if (method.getName ().equals (methodName)) {
                // 参数个数相同
                if (method.getParameterTypes ().length == parameterTypes.length) {
                    String key = buildKey (method.getParameterTypes (), false).get (0);
                    methodMap.put (key, method);
                }
            }
        }
        // 根据传入参数，查找方法
        threadLocal.set (new HashSet<ListClassKey> ());
        try {
            return findMethod (parameterTypes, methodMap);
        } finally {
            threadLocal.remove ();
        }

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

    private static Method findMethod (Class<?>[] parameterTypes, Map<String, Method> methodMap) {
        ListClassKey paramKey = new ListClassKey (parameterTypes);
        if (threadLocal.get ().contains (paramKey)) {
            return null;
        } else {
            threadLocal.get ().add (paramKey);
        }

        List<String> keys = buildKey (parameterTypes, true);
        for (String key : keys) {
            if (methodMap.containsKey (key)) {
                return methodMap.get (key);
            }
        }

        List<Class<?>> supperClasses = Lists.newArrayList ();

        Method method = null;
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

                method = findMethod (types, methodMap);
                if (method != null) {
                    return method;
                }
            }
        }
        return null;
    }

    public static interface Invoker {
        /**
         * 反射执行器，反射调用方法
         *
         * @param obj
         * @param args
         *
         * @return
         */
        Object invoke(Object obj, Object... args);
    }

    public static class Generator extends AbstractGenerator {

        private Method method;

        @SuppressWarnings({"unchecked"})
        public Class<Invoker> generate () {
            return super.generate (Invoker.class);
        }

        @Override
        protected void generateBegin () {
            beginSource = "public Object invoke(Object obj,Object[] args){\n";
            if (!Modifier.isStatic (method.getModifiers ())) {
                // 强制转换源对象
                String convertSource = String.format ("  %s %s=(%s)obj;\n", source.getName (), SOURCE,
                                                      source.getName (), SOURCE);
                beginSource += convertSource;
            }

        }

        @Override
        protected void generateEnd () {
            endSources = "\n}";
        }

        @Override
        protected void generateBody () {
            if (!Modifier.isStatic (method.getModifiers ())) {
                bodySources.add (String.format (
                        "  if(%s==null){\n    throw new java.lang.NullPointerException();\n  }\n\n",
                        SOURCE));
            }
            boolean isVoid = method.getReturnType ().equals (Void.TYPE);

            if (!isVoid) {
                bodySources.add ("  return ");
            }
            if (Modifier.isStatic (method.getModifiers ())) {
                bodySources.add (String.format (" %s.%s( ", source.getName (), method.getName ()));
            } else {
                bodySources.add (String.format (" %s.%s( ", SOURCE, method.getName ()));
            }
            int i = 0;
            for (Class<?> type : method.getParameterTypes ()) {
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
            if (isVoid) {
                bodySources.add ("  return null;");
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
            return PACKAGE_NAME + ".MethodInvokerImpl";
        }

        public void setMethod (Method method) {
            this.method = method;
        }

    }

    protected static class Key {
        private Class<?> objectClass;
        private String method;
        private Class<?>[] parameterTypes;

        public Key (Class<?> objectClass, String method, Class<?>[] parameterTypes) {
            this.objectClass = objectClass;
            this.method = method;
            this.parameterTypes = parameterTypes;
        }

        public Class<?> getObjectClass () {
            return objectClass;
        }

        public void setObjectClass (Class<?> objectClass) {
            this.objectClass = objectClass;
        }

        public String getMethod () {
            return method;
        }

        public void setMethod (String method) {
            this.method = method;
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
            if (method != null ? !method.equals (key.method) : key.method != null) {
                return false;
            }
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals (parameterTypes, key.parameterTypes);

        }

        @Override
        public int hashCode () {
            int result = objectClass != null ? objectClass.hashCode () : 0;
            result = 31 * result + (method != null ? method.hashCode () : 0);
            result = 31 * result + Arrays.hashCode (parameterTypes);
            return result;
        }

        @Override
        public String toString () {
            StringBuilder infoBuilder = new StringBuilder ();
            infoBuilder.append (objectClass.getName ()).append ("#").append (method);
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
