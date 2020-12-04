
package org.liujk.java.framework.base.utils.lang;



import org.liujk.java.framework.base.exceptions.FileOperateException;
import org.springframework.util.Assert;

import java.beans.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 说明：
 * <p>
 * {@link Class} 工具。 如果没有特别说明，向该类的方法传递 null 都会抛出 {@link NullPointerException}
 *
 */
public class ClassUtils {
    /**
     * The prefix String for all Windows OS.
     */
    private static final String OS_NAME_WINDOWS_PREFIX = "Windows";

    /**
     * 空的 {@link Class} 数组。
     */
    @SuppressWarnings("rawtypes")
    public static final Class[] EMPTY_CLASSES = new Class[0];
    /**
     * 资源文件的分隔符： <code>'/'</code>。
     */
    public static final char RESOURCE_SEPARATOR_CHAR = '/';
    /**
     * Java类名的分隔符： <code>'.'</code>。
     */
    public static final char PACKAGE_SEPARATOR_CHAR = '.';
    /**
     * Java类名的分隔符： <code>"."</code>。
     */
    public static final String PACKAGE_SEPARATOR = String
            .valueOf (PACKAGE_SEPARATOR_CHAR);
    /**
     * 内联类的分隔符： <code>'$'</code>。
     */
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
    /**
     * 内联类的分隔符： <code>"$"</code>。
     */
    public static final String INNER_CLASS_SEPARATOR = String
            .valueOf (INNER_CLASS_SEPARATOR_CHAR);
    /**
     * 所有类的信息表，包括父类, 接口, 数组的维数等信息。
     */
    @SuppressWarnings("rawtypes")
    private static Map<Class, TypeInfo> TYPE_MAP = Collections
            .synchronizedMap (new WeakHashMap<Class, TypeInfo> ());

    /**
     * 根据类的唯一限定名以及合适的类加载器创建{@link Class}对象。
     * <p>
     * 如果指定的类为基本类型，则返回的 {@link Class} 对象不一定是由 classLoader 参数指定的类加载器所加载的。
     *
     * @param className   className 类的唯一限定名。
     * @param classLoader 类加载器。
     *
     * @return 合适的{@link Class}对象。
     * @throws TypeNotPresentException 如果没找到限定名的类则抛出该异常。
     */
    public static Class<?> applicationClass (String className, ClassLoader classLoader)
            throws TypeNotPresentException {
        if (classLoader == null) {
            classLoader = getDefaultClassLoader ();
            if (classLoader == null) {
                classLoader = ClassUtils.class.getClassLoader ();
            }
        }
        Class<?> primitiveClass = PrimitiveUtils.getPrimitiveClass (className);
        if (primitiveClass != null) {
            return primitiveClass;
        }
        try {
            return (classLoader.loadClass (className));
        } catch (ClassNotFoundException e) {
            throw new TypeNotPresentException (className, e);
        }
    }

    /**
     * 根据类的唯一限定名创建{@link Class}对象。
     *
     * @param className 类的唯一限定名。
     *
     * @return 合适的{@link Class}对象。
     * @throws TypeNotPresentException 如果没找到限定名的类则抛出该异常。
     */
    public static Class<?> applicationClass (String className) throws TypeNotPresentException {
        return applicationClass (className, null);
    }

    /**
     * 得到当前默认的类加载器。
     * <p>
     * 步骤如下：
     * <ul>
     * <li>Thread.currentThread().getContextClassLoader()</li>
     * <li>ClassUtils.class.getClassLoader()</li>
     * </ul>
     *
     * @return 当前默认的类加载器。
     */
    public static ClassLoader getDefaultClassLoader () {
        return org.springframework.util.ClassUtils.getDefaultClassLoader ();
    }

    /**
     * 判定指定的 className 的类是否 ofClassName 相同,或者是否 ofClassName 的子 类/接口。该方法为在指定的类加载器中比较。
     *
     * @param className   检查类的 classNmae。
     * @param ofClassName 判定类的 className。
     * @param classLoader 在指定的类加载器中比较。
     *
     * @return 如果为子类或者自身，返回 true ，否则返回 false。
     * @throws TypeNotPresentException 如果任何一个类找不到。
     */
    public static boolean instanceofType (String className, String ofClassName,
                                          ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = getDefaultClassLoader ();
        }
        Class<?> clazz = applicationClass (className, classLoader);
        Class<?> ofClazz = applicationClass (ofClassName, classLoader);
        return instanceofType (clazz, ofClazz);
    }

    /**
     * 判定指定的 className 的类是否 ofClassName 相同,或者是否 ofClassName 的子 类/接口。该方法是在默认的类加载器中比较。
     *
     * @param className   检查类的 classNmae。
     * @param ofClassName 判定类的 className。
     *
     * @return 如果为子类或者自身，返回 true ，否则返回 false。
     * @throws TypeNotPresentException 如果任何一个类找不到。
     */
    public static boolean instanceofType (String className, String ofClassName) {
        return instanceofType (className, ofClassName, null);
    }

    /**
     * 判定指定的 clazz 是否与 ofClass 相同,或者是否 ofClass 的子 类/接口。
     *
     * @param clazz   检查的 CLASS。
     * @param ofClass 判定的 CLASS。
     *
     * @return 如果为子类或者自身，返回 true ，否则返回 false。
     */
    public static boolean instanceofType (Class<?> clazz, Class<?> ofClass) {
        // if(clazz == ofClass){
        // return true;
        // }
        // Class<?>[] superInterfaces = clazz.getInterfaces();
        // for(Class<?> superInterface : superInterfaces){
        // if(superInterface == ofClass){
        // return true;
        // } else if(instanceofType(superInterface, ofClass)){
        // return true;
        // }
        // }
        // Class<?> superClass = clazz.getSuperclass();
        // if(superClass == null){
        // return false;
        // }
        // return instanceofType(superClass, ofClass);
        if (clazz == null || ofClass == null) {
            return false;
        }
        return ofClass.isAssignableFrom (clazz);
    }

    /**
     * 将类型数组使用 {@link Class#getName()} 转换为字符串数组。
     *
     * @param classes 需要转换的类型的数组。
     *
     * @return classes 对应的字符串数组。
     */
    public static String[] toStrings (Class<?>... classes) {
        if (classes == null) {
            return null;
        }
        String[] classStrArray = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
            classStrArray[i] = classes[i].getName ();
        }
        return classStrArray;
    }

    /**
     * 使用文件路径构建一个该路径下（包含子路径）的所有java类的 {@link Class} 对象的集合。
     * <p>
     * 该方法会{@link ClassUtils#getDefaultClassLoader()}得到的类加载器加载 {@link Class} 。
     *
     * @param filePath
     *
     * @return filePath 下的所有java类的 {@link Class} 对象的集合。
     * @throws IllegalArgumentException 如果 filePath 不是指向的 classpath 的路径。
     * @throws TypeNotPresentException  如果有 .class 文件，但并不是 java 类文件时。
     */
    public static Set<Class<?>> getClassByFilePath (String filePath)
            throws IllegalArgumentException, TypeNotPresentException {
        return getClassByFilePath (filePath, ClassUtils.getDefaultClassLoader ());
    }

    /**
     * 使用文件路径构建一个该路径下（包含子路径）的所有java类的 {@link Class} 对象的集合。
     *
     * @param filePath    用于构建 {@link Class} 对象的集合的路径。
     * @param classLoader 用于加载 {@link Class} 的类加载器。
     *
     * @return filePath 下的所有java类的 {@link Class} 对象的集合。
     * @throws IllegalArgumentException 如果 filePath 不是指向的 classpath 的路径。
     * @throws TypeNotPresentException  如果有 .class 文件，但并不是 java 类文件时。
     */
    public static Set<Class<?>> getClassByFilePath (String filePath, ClassLoader classLoader) {
        Set<Class<?>> classSet = new HashSet<Class<?>> ();
        buildClassSetByFilePath (filePath, getClassPath (), classSet, classLoader);
        return classSet;
    }

    /**
     * 使用文件路径构建一个该路径下（包含子路径）的所有java类的 {@link Class} 对象的集合，该集合结果添加到 classSet 中
     * <p>
     * 该方法是用 {@link #getClassPath()} 获得 classpath ，使用默认的类加载器加载 {@link Class} 。
     *
     * @param filePath 用于构建 {@link Class} 对象的集合的路径。
     * @param classSet 用于存放结果的集合。
     *
     * @throws IllegalArgumentException 如果 filePath 不是指向的 classpath 的路径。
     * @throws TypeNotPresentException  如果有 .class 文件，但并不是 java 类文件时。
     */
    public static void buildClassSetByFilePath (String filePath, Set<Class<?>> classSet) {
        buildClassSetByFilePath (filePath, getClassPath (), classSet,
                                 ClassUtils.getDefaultClassLoader ());
    }

    /**
     * 使用文件路径构建一个该路径下（包含子路径）的所有java类的 {@link Class} 对象的集合，该集合结果添加到 classSet 中。
     * <p>
     * 该方法是用 {@link #getClassPath()} 获得 classpath 。
     *
     * @param filePath    用于构建 {@link Class} 对象的集合的路径。
     * @param classSet    用于存放结果的集合。
     * @param classLoader 用于加载 {@link Class} 的类加载器。
     *
     * @throws IllegalArgumentException 如果 filePath 不是指向的 classpath 的路径。
     * @throws TypeNotPresentException  如果有 .class 文件，但并不是 java 类文件时。
     */
    public static void buildClassSetByFilePath (String filePath, Set<Class<?>> classSet,
                                                ClassLoader classLoader) {
        buildClassSetByFilePath (filePath, getClassPath (), classSet, classLoader);
    }

    /**
     * getAppPath需要一个当前程序使用的Java类的class属性参数，它可以返回打包过的 Java可执行文件（jar，war）所处的系统目录名或非打包Java程序所处的目录
     *
     * @return 返回值为该类所在的Java程序运行的目录
     */
    public static String getAppPath () {
        return getAppPath (ClassUtils.class);
    }
    public static String getAppPath (Class<?> cls) {
        String clsClssPaht = cls.getName () + ".class";
        String strClassFile = getClassFilePath (cls);
        String realPath = strClassFile.substring (0, strClassFile.length () - clsClssPaht.length () - 1);

        realPath = realPath.replaceAll ("\\\\", "/");

        int pos = realPath.indexOf ('!');
        if (pos> 0) {
            realPath = realPath.substring (0, pos);
        }
        if(realPath.charAt (0) == '/' && System.getProperty ("os.name").startsWith (OS_NAME_WINDOWS_PREFIX)){
            realPath = realPath.substring (1);
        }

        return realPath;
    }

    /**
     * 得到 该类所在的classpath 的绝对路径。
     *
     * @return 该类所在的classpath 的绝对路径。
     */
    public static String getClassPath () {
        return getClassFilePath(ClassUtils.class);
    }

    /**
     * 获取Class文件路径
     *
     * @param cls
     *
     * @return
     */
    public static String getClassFilePath (Class<?> cls) {
        ClassLoader loader = cls.getClassLoader ();

        String clsClssPaht = cls.getName ().replaceAll ("\\.", "/") + ".class";

        java.net.URL url = loader.getResource (clsClssPaht);
        String realPath = url.getPath ();

        try {
            realPath = URLDecoder.decode (realPath, "UTF-8");
        } catch (Exception e) {
        }

        File fClass = new File (realPath);
        realPath = fClass.getPath ();

        int pos = realPath.indexOf ("file:");
        if (pos > -1) {
            realPath = realPath.substring (pos + 5);
        }

        return realPath;
    }


    /**
     * 使用文件路径构建一个该路径下（包含子路径）的所有java类的 {@link Class} 对象的集合，该集合结果添加到 classSet 中。
     * <p>
     * 该方法会{@link ClassUtils#getDefaultClassLoader()}得到的类加载器加载 {@link Class} 。
     *
     * @param filePath  用于构建 {@link Class} 对象的集合的路径。
     * @param classPath classpath 的路径，如果 filePath 指向的不与 classPath 制定的路径相同，则会抛出 IllegalArgumentException
     *                  ，如果为 null 则直接使用 classLoader 指定的加载器加载完整的文件路径。
     * @param classSet  用于存放结果的集合。
     *
     * @throws IllegalArgumentException 如果 filePath 不是指向的 classpath 的路径。
     * @throws TypeNotPresentException  如果有 .class 文件，但并不是 java 类文件时。
     */
    public static void buildClassSetByFilePath (String filePath, String classPath,
                                                Set<Class<?>> classSet)
            throws IllegalArgumentException, TypeNotPresentException {
        buildClassSetByFilePath (filePath, classPath, classSet, ClassUtils.getDefaultClassLoader ());
    }

    /**
     * 使用文件路径构建一个该路径下（包含子路径）的所有java类的 {@link Class} 对象的集合，该集合结果添加到 classSet 中。
     *
     * @param filePath    用于构建 {@link Class} 对象的集合的路径。
     * @param classPath   classpath 的路径，如果 filePath 指向的不与 classPath 制定的路径相同，则会抛出 IllegalArgumentException
     *                    ，如果为 null 则直接使用 classLoader 指定的加载器加载完整的文件路径。
     * @param classSet    用于存放结果的集合。
     * @param classLoader 用于加载 {@link Class} 的类加载器。
     *
     * @throws IllegalArgumentException 如果 filePath 不是指向的 classpath 的路径。
     * @throws TypeNotPresentException  如果有 .class 文件，但并不是 java 类文件时。
     */
    public static void buildClassSetByFilePath (String filePath, String classPath,
                                                Set<Class<?>> classSet, ClassLoader classLoader) {
        File[] files;
        try {
            files = IOUtils.fileArray (filePath, new FileFilter () {
                @Override
                public boolean accept (File pathname) {
                    if (pathname.getPath ().endsWith ("class") || pathname.isDirectory ()) {
                        return true;
                    }
                    return false;
                }
            });
        } catch (FileOperateException e) {
            throw new IllegalArgumentException (e.getMessage ());
        }
        for (File file : files) {
            if (file.isDirectory ()) {
                buildClassSetByFilePath (file.getPath (), classPath, classSet, classLoader);
            } else {
                String realPath = file.getPath ();
                if (classPath == null) {
                    if (realPath.endsWith (".class")) {
                        try {
                            Class<?> clazz = classLoader.loadClass (realPath);
                            classSet.add (clazz);
                        } catch (ClassNotFoundException e) {

                        }
                    }
                } else {
                    if (realPath.startsWith (ResourceUtils.SEPARATOR)) {
                        realPath = realPath.substring (1);
                    }
                    if (realPath.startsWith (classPath)) {
                        String className = realPath.substring (classPath.length ());
                        className = className.replaceAll (ResourceUtils.SEPARATOR, ".");
                        className = getClassName (className);
                        classSet.add (ClassUtils.applicationClass (className, classLoader));
                    } else {
                        throw new IllegalArgumentException (
                                "路径 [" + realPath + "] 不是 CLASSPATH [" + classPath + "] 下的路径。");
                    }
                }
            }
        }
    }

    /**
     * 得到 jarPath 路径下所有jar文件中的的所有java类的 {@link Class} 对象的集合。
     * <p>
     * 该方法会{@link ClassUtils#getDefaultClassLoader()}得到的类加载器加载 {@link Class} 。
     *
     * @param jarPath 存放jar文件的路径。
     *
     * @return jarPath 路径下所有jar文件中的的所有java类的 {@link Class} 对象的集合。
     * @throws TypeNotPresentException 如果有 .class 文件，但并不是 java 类文件时。
     */
    public static Set<Class<?>> getClassByJarPath (String jarPath) {
        return getClassByJarPath (jarPath, ClassUtils.getDefaultClassLoader ());
    }

    /**
     * 得到 jarPath 路径下所有jar文件中的的所有java类的 {@link Class} 对象的集合。
     *
     * @param jarPath     存放jar文件的路径。
     * @param classLoader 用于加载 {@link Class} 的类加载器。
     *
     * @return jarPath 路径下所有jar文件中的的所有java类的 {@link Class} 对象的集合。
     * @throws TypeNotPresentException 如果有 .class 文件，但并不是 java 类文件时。
     */
    public static Set<Class<?>> getClassByJarPath (String jarPath, ClassLoader classLoader) {
        Set<Class<?>> classSet = new HashSet<Class<?>> ();
        buildClassSetByJarPath (jarPath, classSet, classLoader);
        return classSet;
    }

    /**
     * 使用 jarPath 路径下所有jar文件中的的所有java类的 {@link Class} 对象的集合，该集合结果添加到 classSet 中。
     * <p>
     * 该方法会{@link ClassUtils#getDefaultClassLoader()}得到的类加载器加载 {@link Class} 。
     *
     * @param jarPath  存放jar文件的路径。
     * @param classSet 用于存放结果的集合。
     *
     * @throws TypeNotPresentException 如果有 .class 文件，但并不是 java 类文件时。
     */
    public static void buildClassSetByJarPath (String jarPath, Set<Class<?>> classSet) {
        buildClassSetByJarPath (jarPath, classSet, ClassUtils.getDefaultClassLoader ());
    }

    /**
     * 使用 jarPath 路径下所有jar文件中的的所有java类的 {@link Class} 对象的集合，该集合结果添加到 classSet 中。
     *
     * @param jarPath     存放jar文件的路径。
     * @param classSet    用于存放结果的集合。
     * @param classLoader 用于加载 {@link Class} 的类加载器。
     *
     * @throws TypeNotPresentException 如果有 .class 文件，但并不是 java 类文件时。
     */
    public static void buildClassSetByJarPath (String jarPath, Set<Class<?>> classSet,
                                               ClassLoader classLoader) {
        List<JarFile> jarFiles = null;
        try {
            jarFiles = JarUtils.getJarResource (jarPath);
            for (JarFile jarFile : jarFiles) {
                parseClassByJar (jarFile, classSet, classLoader, null);
            }
        } finally {
            if (CollectionUtils.isNotEmpty (jarFiles)) {
                for (JarFile jarFile : jarFiles) {
                    if (jarFile != null) {
                        try {
                            jarFile.close ();
                        } catch (IOException e) {
                            // 不作处理
                        }
                    }
                }
            }
        }
    }

    /**
     * 使用一个jar文件路径构建一个该jar文件中的的所有java类的 {@link Class} 对象的集合。
     * <p>
     * 该方法会{@link ClassUtils#getDefaultClassLoader()}得到的类加载器加载 {@link Class} 。
     *
     * @param jarPath jar文件的路径。
     *
     * @return jarPath 指定的jar文件中的的所有java类的 {@link Class} 对象的集合。
     * @throws IOException             如果发生 I/O 错误。
     * @throws TypeNotPresentException 如果有 .class 文件，但并不是 java 类文件时。
     */
    public static Set<Class<?>> getClassByJar (String jarPath) throws IOException {
        return getClassByJar (jarPath, ClassUtils.getDefaultClassLoader ());
    }

    /**
     * 使用一个jar文件路径构建一个该jar文件中的的所有java类的 {@link Class} 对象的集合。
     *
     * @param jarPath     jar文件的路径。
     * @param classLoader 用于加载 {@link Class} 的类加载器。
     *
     * @return jarPath 指定的jar文件中的的所有java类的 {@link Class} 对象的集合。
     * @throws IOException             如果发生 I/O 错误。
     * @throws TypeNotPresentException 如果有 .class 文件，但并不是 java 类文件时。
     */
    public static Set<Class<?>> getClassByJar (String jarPath, ClassLoader classLoader)
            throws IOException {
        Set<Class<?>> classSet = new HashSet<Class<?>> ();
        buildClassSetByJar (jarPath, classSet, classLoader);
        return classSet;
    }

    /**
     * 使用一个jar文件路径构建一个该jar文件中的的所有java类的 {@link Class} 对象的集合，该集合结果添加到 classSet 中。
     * <p>
     * 该方法会{@link ClassUtils#getDefaultClassLoader()}得到的类加载器加载 {@link Class} 。
     *
     * @param jarPath  jar文件的路径。
     * @param classSet 用于存放结果的集合。
     *
     * @throws IOException             如果发生 I/O 错误。
     * @throws TypeNotPresentException 如果有 .class 文件，但并不是 java 类文件时。
     */
    public static void buildClassSetByJar (String jarPath, Set<Class<?>> classSet)
            throws IOException {
        buildClassSetByJar (jarPath, classSet, ClassUtils.getDefaultClassLoader ());
    }

    /**
     * 使用一个jar文件路径构建一个该jar文件中的的所有java类的 {@link Class} 对象的集合，该集合结果添加到 classSet 中。
     *
     * @param jarPath     jar文件的路径。
     * @param classSet    用于存放结果的集合。
     * @param classLoader 用于加载 {@link Class} 的类加载器。
     *
     * @throws IOException             如果发生 I/O 错误。
     * @throws TypeNotPresentException 如果有 .class 文件，但并不是 java 类文件时。
     */
    public static void buildClassSetByJar (String jarPath, Set<Class<?>> classSet,
                                           ClassLoader classLoader) throws IOException {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile (jarPath);
            parseClassByJar (jarFile, classSet, classLoader, null);
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close ();
                } catch (IOException e) {
                    // 不作处理
                }
            }
        }
    }

    /**
     * 从一个 JarFile 里使用 {@link ClassLoader#loadClass(String)} 加载所有的 .class 为 {@link Class} 对象 。
     *
     * @param jarFile     需要从其中加载 class 的 JarFile。
     * @param classSet    用于存放加载的 {@link Class} 的 {@link Set} 。
     * @param classLoader 用于加载 {@link Class} 的类加载器，对该类加载器使用
     *                    {@link ClassUtils#applicationClass(String, ClassLoader)} 完成加载。
     * @param decider     用于判定 得到的 className 的类是否需要被加载并放入 classSet 中，当判定器为 null 或者 判定结果为 true 时加载得到的
     *                    className 的类并放入 classSet 中。
     *
     * @see ClassUtils#applicationClass(String, ClassLoader)
     */
    public static void parseClassByJar (JarFile jarFile, Set<Class<?>> classSet,
                                        ClassLoader classLoader, Decider<String> decider) {
        for (Enumeration<JarEntry> jarEntrys = jarFile.entries (); jarEntrys.hasMoreElements (); ) {
            JarEntry jarEntry = jarEntrys.nextElement ();
            if (!jarEntry.isDirectory () && jarEntry.getName ().endsWith (".class")) {
                String className = jarEntry.getName ().replaceAll ("/", ".").replaceAll ("\\\\", ".");
                if (decider == null) {
                    classSet.add (ClassUtils.applicationClass (getClassName (className), classLoader));
                } else if (decider.decide (className)) {
                    classSet.add (ClassUtils.applicationClass (getClassName (className), classLoader));
                }
            }
        }
    }

    /*------------------------------------------------------------------------------------
     *                         以下是 ClassUtil 的代码复制合并，新增方法请在此注释之上                                             -
     * ----------------------------------------------------------------------------------*/

    /*
     * ========================================================================== ==
     */
    /* 常量和singleton。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 得到 clazz 代表的类所有实现的接口，包括接口继承的接口，父类实现的接口。
     *
     * @param clazz 需要得到所有实现接口的类的 class 对象。
     *
     * @return 所有实现的接口。
     */
    public static Set<Class<?>> getAllInterfacesForClassAsSet (Class<?> clazz) {
        return getAllInterfacesForClassAsSet (clazz, null);
    }

    /**
     * 得到 clazz 代表的类所有实现的接口，包括接口继承的接口，父类实现的接口。
     * <p>
     * 如果接口不与 classLoader 兼容，则不会返回在结果里。
     *
     * @param clazz 需要得到所有实现接口的类的 class 对象。
     *
     * @return 所有实现的接口。
     */
    public static Set<Class<?>> getAllInterfacesForClassAsSet (Class<?> clazz,
                                                               ClassLoader classLoader) {
        Set<Class<?>> interfaces = new LinkedHashSet<Class<?>> ();
        getAllInterfacesForClassAsSet (interfaces, clazz, classLoader);
        return interfaces;
    }

    /**
     * 得到 clazz 代表的类所有实现的接口，包括接口继承的接口，父类实现的接口。
     *
     * @param clazz 需要得到所有实现接口的类的 class 对象。
     *
     * @return 所有实现的接口。
     */
    public static Class<?>[] getAllInterfacesForClass (Class<?> clazz) {
        Set<Class<?>> allInterfaces = getAllInterfacesForClassAsSet (clazz);
        return allInterfaces.toArray (new Class<?>[allInterfaces.size ()]);
    }

    /**
     * 得到 clazz 代表的类所有实现的接口，包括接口继承的接口，父类实现的接口。
     * <p>
     * 如果接口不与 classLoader 兼容，则不会返回在结果里。
     *
     * @param clazz 需要得到所有实现接口的类的 class 对象。
     *
     * @return 所有实现的接口。
     */
    public static Class<?>[] getAllInterfacesForClass (Class<?> clazz, ClassLoader classLoader) {
        Set<Class<?>> allInterfaces = getAllInterfacesForClassAsSet (clazz, classLoader);
        return allInterfaces.toArray (new Class<?>[allInterfaces.size ()]);
    }

    private static void getAllInterfacesForClassAsSet (Set<Class<?>> interfaces, Class<?> clazz,
                                                       ClassLoader classLoader) {
        Assert.notNull (clazz, "{clazz} 不能为 'null' 。");
        if (clazz.isInterface () && isVisible (clazz, classLoader)) {
            interfaces.add (clazz);
        }
        while (clazz != null) {
            Class<?>[] ifcs = clazz.getInterfaces ();
            for (Class<?> ifc : ifcs) {
                getAllInterfacesForClassAsSet (interfaces, ifc, classLoader);
            }
            clazz = clazz.getSuperclass ();
        }
    }

    /**
     * 判定类 clazz 对于 classLoader 是否可见。
     *
     * @param clazz       参与判定的类的 class 对象。
     * @param classLoader 参与判定的类加载器。
     *
     * @return 如果 classLoader 为 null 返回 true ，如果 clazz 对于 classLoader 兼容返回 true 。
     */
    public static boolean isVisible (Class<?> clazz, ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        }
        try {
            Class<?> actualClass = classLoader.loadClass (clazz.getName ());
            return (clazz == actualClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /*
     * ========================================================================== ==
     */
    /* 取得类名和package名的方法。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 取得对象所属的类的直观类名。
     * <p>
     * <p>
     * 相当于 <code>object.getClass().getName()</code> ，但不同的是，该方法用更直观的方式显示数组类型。 例如：
     * <p>
     *
     * <pre>
     *  int[].class.getName() = "[I" ClassUtil.getClassName(int[].class) = "int[]"
     *
     *  Integer[][].class.getName() = "[[Ljava.lang.Integer;" ClassUtil.getClassName(Integer[][].class) = "java.lang.Integer[][]"
     * </pre>
     * <p>
     * </p>
     * <p>
     * <p>
     * 对于非数组的类型，该方法等效于 <code>Class.getName()</code> 方法。
     * </p>
     * <p>
     * <p>
     * 注意，该方法所返回的数组类名只能用于显示给人看，不能用于 <code>Class.forName</code> 操作。
     * </p>
     *
     * @param object 要显示类名的对象
     *
     * @return 用于显示的直观类名，如果原类名为空或非法，则返回 <code>null</code>
     */
    public static String getClassNameForObject (Object object) {
        if (object == null) {
            return null;
        }

        return getClassName (object.getClass ().getName (), true);
    }

    /**
     * 取得直观的类名。
     * <p>
     * <p>
     * 相当于 <code>clazz.getName()</code> ，但不同的是，该方法用更直观的方式显示数组类型。 例如：
     * <p>
     *
     * <pre>
     *  int[].class.getName() = "[I" ClassUtil.getClassName(int[].class) = "int[]"
     *
     *  Integer[][].class.getName() = "[[Ljava.lang.Integer;" ClassUtil.getClassName(Integer[][].class) = "java.lang.Integer[][]"
     * </pre>
     * <p>
     * </p>
     * <p>
     * <p>
     * 对于非数组的类型，该方法等效于 <code>Class.getName()</code> 方法。
     * </p>
     * <p>
     * <p>
     * 注意，该方法所返回的数组类名只能用于显示给人看，不能用于 <code>Class.forName</code> 操作。
     * </p>
     *
     * @param clazz 要显示类名的类
     *
     * @return 用于显示的直观类名，如果原始类为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getClassName (Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getClassName (clazz.getName (), true);
    }

    /**
     * 取得直观的类名。
     * <p>
     * <p>
     * <code>className</code> 必须是从 <code>clazz.getName()</code> 所返回的合法类名。该方法用更直观的方式显示数组类型。 例如：
     * <p>
     *
     * <pre>
     *  int[].class.getName() = "[I" ClassUtil.getClassName(int[].class) = "int[]"
     *
     *  Integer[][].class.getName() = "[[Ljava.lang.Integer;" ClassUtil.getClassName(Integer[][].class) = "java.lang.Integer[][]"
     * </pre>
     * <p>
     * </p>
     * <p>
     * <p>
     * 对于非数组的类型，该方法等效于 <code>Class.getName()</code> 方法。
     * </p>
     * <p>
     * <p>
     * 注意，该方法所返回的数组类名只能用于显示给人看，不能用于 <code>Class.forName</code> 操作。
     * </p>
     *
     * @param className 要显示的类名
     *
     * @return 用于显示的直观类名，如果原类名为 <code>null</code> ，则返回 <code>null</code> ，如果原类名是非法的，则返回原类名
     */
    public static String getClassName (String className) {
        return getClassName (className, true);
    }

    /**
     * 取得直观的类名。
     *
     * @param className         类名
     * @param processInnerClass 是否将内联类分隔符 <code>'$'</code> 转换成 <code>'.'</code>
     *
     * @return 直观的类名，或 <code>null</code>
     */
    private static String getClassName (String className, boolean processInnerClass) {
        if (StringUtils.isEmpty (className)) {
            return className;
        }

        if (processInnerClass) {
            className = className.replace (INNER_CLASS_SEPARATOR_CHAR, PACKAGE_SEPARATOR_CHAR);
        }

        int length = className.length ();
        int dimension = 0;

        // 取得数组的维数，如果不是数组，维数为0
        for (int i = 0; i < length; i++, dimension++) {
            if (className.charAt (i) != '[') {
                break;
            }
        }

        // 如果不是数组，则直接返回
        if (dimension == 0) {
            return className;
        }

        // 确保类名合法
        if (length <= dimension) {
            return className; // 非法类名
        }

        // 处理数组
        StringBuffer componentTypeName = new StringBuffer ();

        switch (className.charAt (dimension)) {
            case 'Z':
                componentTypeName.append ("boolean");
                break;

            case 'B':
                componentTypeName.append ("byte");
                break;

            case 'C':
                componentTypeName.append ("char");
                break;

            case 'D':
                componentTypeName.append ("double");
                break;

            case 'F':
                componentTypeName.append ("float");
                break;

            case 'I':
                componentTypeName.append ("int");
                break;

            case 'J':
                componentTypeName.append ("long");
                break;

            case 'S':
                componentTypeName.append ("short");
                break;

            case 'L':

                if ((className.charAt (length - 1) != ';') || (length <= (dimension + 2))) {
                    // 非法类名
                    return className;
                }

                componentTypeName.append (className.substring (dimension + 1, length - 1));
                break;

            default:
                return className; // 非法类名
        }

        for (int i = 0; i < dimension; i++) {
            componentTypeName.append ("[]");
        }

        return componentTypeName.toString ();
    }

    /**
     * 取得指定对象所属的类的短类名，不包括package名。
     * <p>
     * <p>
     * 此方法可以正确显示数组和内联类的名称。
     * </p>
     * <p>
     * <p>
     * 例如：
     * <p>
     *
     * <pre>
     *  ClassUtil.getShortClassNameForObject(Boolean.TRUE) = "Boolean" ClassUtil.getShortClassNameForObject(new Boolean[10]) = "Boolean[]" ClassUtil.getShortClassNameForObject(new int[1][2]) = "int[][]"
     * </pre>
     * <p>
     * </p>
     *
     * @param object 要查看的对象
     *
     * @return 短类名，如果对象为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getShortClassNameForObject (Object object) {
        if (object == null) {
            return null;
        }

        return getShortClassName (object.getClass ().getName ());
    }

    /**
     * 取得短类名，不包括package名。
     * <p>
     * <p>
     * 此方法可以正确显示数组和内联类的名称。
     * </p>
     * <p>
     * <p>
     * 例如：
     * <p>
     *
     * <pre>
     *  ClassUtil.getShortClassName(Boolean.class) = "Boolean" ClassUtil.getShortClassName(Boolean[].class) = "Boolean[]" ClassUtil.getShortClassName(int[][].class) = "int[][]" ClassUtil.getShortClassName(Map.Entry.class) = "Map.Entry"
     * </pre>
     * <p>
     * </p>
     *
     * @param clazz 要查看的类
     *
     * @return 短类名，如果类为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getShortClassName (Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getShortClassName (clazz.getName ());
    }

    /**
     * 取得类名，不包括package名。
     * <p>
     * <p>
     * 此方法可以正确显示数组和内联类的名称。
     * </p>
     * <p>
     * <p>
     * 例如：
     * <p>
     *
     * <pre>
     *  ClassUtil.getShortClassName(Boolean.class.getName()) = "Boolean" ClassUtil.getShortClassName(Boolean[].class.getName()) = "Boolean[]" ClassUtil.getShortClassName(int[][].class.getName()) = "int[][]" ClassUtil.getShortClassName(Map.Entry.class.getName()) = "Map.Entry"
     * </pre>
     * <p>
     * </p>
     *
     * @param className 要查看的类名
     *
     * @return 短类名，如果类名为空，则返回 <code>null</code>
     */
    public static String getShortClassName (String className) {
        if (StringUtils.isEmpty (className)) {
            return className;
        }

        // 转换成直观的类名
        className = getClassName (className, false);

        char[] chars = className.toCharArray ();
        int lastDot = 0;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == PACKAGE_SEPARATOR_CHAR) {
                lastDot = i + 1;
            } else if (chars[i] == INNER_CLASS_SEPARATOR_CHAR) {
                chars[i] = PACKAGE_SEPARATOR_CHAR;
            }
        }

        return new String (chars, lastDot, chars.length - lastDot);
    }

    /**
     * 取得指定对象所属的类的package名。
     * <p>
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     *
     * @param object 要查看的对象
     *
     * @return package名，如果对象为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getPackageNameForObject (Object object) {
        if (object == null) {
            return null;
        }

        return getPackageName (object.getClass ().getName ());
    }

    /**
     * 取得指定类的package名。
     * <p>
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     *
     * @param clazz 要查看的类
     *
     * @return package名，如果类为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getPackageName (Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getPackageName (clazz.getName ());
    }

    /**
     * 取得指定类名的package名。
     * <p>
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     *
     * @param className 要查看的类名
     *
     * @return package名，如果类名为空，则返回 <code>null</code>
     */
    public static String getPackageName (String className) {
        if (StringUtils.isEmpty (className)) {
            return null;
        }

        // 转换成直观的类名
        className = getClassName (className, false);

        int i = className.lastIndexOf (PACKAGE_SEPARATOR_CHAR);

        if (i == -1) {
            return "";
        }

        return className.substring (0, i);
    }

    /*
     * ========================================================================== ==
     */
    /* 取得类名和package名的resource名的方法。 */
    /*                                                                              */
    /* 和类名、package名不同的是，resource名符合文件名命名规范，例如： */
    /* java/lang/String.class */
    /* com/mbb/commons/lang */
    /* etc. */
    /*
     * ========================================================================== ==
     */

    /**
     * 取得对象所属的类的资源名。
     * <p>
     * <p>
     * 例如：
     * <p>
     *
     * <pre>
     * ClassUtil.getClassNameForObjectAsResource(&quot;This is a string&quot;) = &quot;java/lang/String.class&quot;
     * </pre>
     * <p>
     * </p>
     *
     * @param object 要显示类名的对象
     *
     * @return 指定对象所属类的资源名，如果对象为空，则返回<code>null</code>
     */
    public static String getClassNameForObjectAsResource (Object object) {
        if (object == null) {
            return null;
        }

        return object.getClass ().getName ().replace (PACKAGE_SEPARATOR_CHAR, RESOURCE_SEPARATOR_CHAR)
                + ".class";
    }

    /**
     * 取得指定类的资源名。
     * <p>
     * <p>
     * 例如：
     * <p>
     *
     * <pre>
     * ClassUtil.getClassNameAsResource(String.class) = &quot;java/lang/String.class&quot;
     * </pre>
     * <p>
     * </p>
     *
     * @param clazz 要显示类名的类
     *
     * @return 指定类的资源名，如果指定类为空，则返回<code>null</code>
     */
    public static String getClassNameAsResource (Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return clazz.getName ().replace (PACKAGE_SEPARATOR_CHAR, RESOURCE_SEPARATOR_CHAR) + ".class";
    }

    /**
     * 取得指定类的资源名。
     * <p>
     * <p>
     * 例如：
     * <p>
     *
     * <pre>
     * ClassUtil.getClassNameAsResource(&quot;java.lang.String&quot;) = &quot;java/lang/String.class&quot;
     * </pre>
     * <p>
     * </p>
     *
     * @param className 要显示的类名
     *
     * @return 指定类名对应的资源名，如果指定类名为空，则返回<code>null</code>
     */
    public static String getClassNameAsResource (String className) {
        if (className == null) {
            return null;
        }

        return className.replace (PACKAGE_SEPARATOR_CHAR, RESOURCE_SEPARATOR_CHAR) + ".class";
    }

    /**
     * 取得指定对象所属的类的package名的资源名。
     * <p>
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     *
     * @param object 要查看的对象
     *
     * @return package名，如果对象为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getPackageNameForObjectAsResource (Object object) {
        if (object == null) {
            return null;
        }

        return getPackageNameForObject (object).replace (PACKAGE_SEPARATOR_CHAR,
                                                         RESOURCE_SEPARATOR_CHAR);
    }

    /**
     * 取得指定类的package名的资源名。
     * <p>
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     *
     * @param clazz 要查看的类
     *
     * @return package名，如果类为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getPackageNameAsResource (Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getPackageName (clazz).replace (PACKAGE_SEPARATOR_CHAR, RESOURCE_SEPARATOR_CHAR);
    }

    /**
     * 取得指定类名的package名的资源名。
     * <p>
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     *
     * @param className 要查看的类名
     *
     * @return package名，如果类名为空，则返回 <code>null</code>
     */
    public static String getPackageNameAsResource (String className) {
        if (className == null) {
            return null;
        }

        return getPackageName (className).replace (PACKAGE_SEPARATOR_CHAR, RESOURCE_SEPARATOR_CHAR);
    }

    /*
     * ========================================================================== ==
     */
    /* 取得类的信息，如父类, 接口, 数组的维数等。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 取得指定维数的 <code>Array</code>类.
     *
     * @param componentType 数组的基类
     * @param dimension     维数，如果小于 <code>0</code> 则看作 <code>0</code>
     *
     * @return 如果维数为0, 则返回基类本身, 否则返回数组类，如果数组的基类为 <code>null</code> ，则返回 <code>null</code>
     */
    public static Class<?> getArrayClass (Class<?> componentType, int dimension) {
        if (dimension <= 0) {
            return componentType;
        }

        if (componentType == null) {
            return null;
        }

        return Array.newInstance (componentType, new int[dimension]).getClass ();
    }

    /**
     * 取得数组元素的类型。
     *
     * @param type 要查找的类
     *
     * @return 如果是数组, 则返回数组元素的类型, 否则返回 <code>null</code>
     */
    public static Class<?> getArrayComponentType (Class<?> type) {
        if (type == null) {
            return null;
        }

        return getTypeInfo (type).getArrayComponentType ();
    }

    /**
     * 取得数组的维数。
     *
     * @param clazz 要查找的类
     *
     * @return 数组的维数. 如果不是数组, 则返回 <code>0</code> ，如果数组为 <code>null</code> ，是返回 <code>-1</code>
     */
    public static int getArrayDimension (Class<?> clazz) {
        if (clazz == null) {
            return -1;
        }

        return getTypeInfo (clazz).getArrayDimension ();
    }

    /**
     * 取得指定类的所有父类。
     * <p>
     * <p>
     * 对于一个 <code>Class</code> 实例，如果它不是接口，也不是数组，此方法依次列出从该类的父类开始直到 <code>Object</code> 的所有类。
     * </p>
     * <p>
     * <p>
     * 例如 <code>ClassUtil.getSuperclasses(java.util.ArrayList.class)</code> 返回以下列表：
     * <p>
     * <ol>
     * <li><code>java.util.AbstractList</code></li>
     * <li><code>java.util.AbstractCollection</code></li>
     * <li><code>java.lang.Object</code></li>
     * </ol>
     * </p>
     * <p>
     * <p>
     * 对于一个接口，此方法返回一个空列表。
     * </p>
     * <p>
     * <p>
     * 例如<code>ClassUtil.getSuperclasses(java.util.List.class)</code>将返回一个空列表。
     * </p>
     * <p>
     * <p>
     * 对于一个数组，此方法返回一个列表，列出所有component类型的父类的相同维数的数组类型。 例如：
     * <code>ClassUtil.getSuperclasses(java.util.ArrayList[][].class)</code> 返回以下列表：
     * <p>
     * <ol>
     * <li><code>java.util.AbstractList[][]</code></li>
     * <li><code>java.util.AbstractCollection[][]</code></li>
     * <li><code>java.lang.Object[][]</code></li>
     * <li><code>java.lang.Object[]</code></li>
     * <li><code>java.lang.Object</code></li>
     * </ol>
     * <p>
     * 注意，原子类型及其数组，将被转换成相应的包装类来处理。 例如： <code>ClassUtil.getSuperclasses(int[][].class)</code> 返回以下列表：
     * <p>
     * <ol>
     * <li><code>java.lang.Number[][]</code></li>
     * <li><code>java.lang.Object[][]</code></li>
     * <li><code>java.lang.Object[]</code></li>
     * <li><code>java.lang.Object</code></li>
     * </ol>
     * </p>
     *
     * @param clazz 要查找的类
     *
     * @return 所有父类的列表，如果指定类为 <code>null</code> ，则返回 <code>null</code>
     */
    @SuppressWarnings("rawtypes")
    public static List<Class> getSuperclasses (Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getTypeInfo (clazz).getSuperclasses ();
    }

    /**
     * 取得指定类的所有接口。
     * <p>
     * <p>
     * 对于一个 <code>Class</code> 实例，如果它不是接口，也不是数组，此方法依次列出从该类的父类开始直到 <code>Object</code> 的所有类。
     * </p>
     * <p>
     * <p>
     * 例如 <code>ClassUtil.getInterfaces(java.util.ArrayList.class)</code> 返回以下列表：
     * <p>
     * <ol>
     * <li><code>java.util.List</code></li>
     * <li><code>java.util.Collection</code></li>
     * <li><code>java.util.RandomAccess</code></li>
     * <li><code>java.lang.Cloneable</code></li>
     * <li><code>java.io.Serializable</code></li>
     * </ol>
     * </p>
     * <p>
     * <p>
     * 对于一个数组，此方法返回一个列表，列出所有component类型的接口的相同维数的数组类型。 例如：
     * <code>ClassUtil.getInterfaces(java.util.ArrayList[][].class)</code> 返回以下列表：
     * <p>
     * <ol>
     * <li><code>java.util.List[][]</code></li>
     * <li><code>java.util.Collection[][]</code></li>
     * <li><code>java.util.RandomAccess[][]</code></li>
     * <li><code>java.lang.Cloneable[][]</code></li>
     * <li><code>java.io.Serializable[][]</code></li>
     * </ol>
     * </p>
     * <p>
     * <p>
     * 注意，原子类型及其数组，将被转换成相应的包装类来处理。 例如： <code>ClassUtil.getInterfaces(int[][].class)</code> 返回以下列表：
     * <p>
     * <ol>
     * <li><code>java.lang.Comparable[][]</code></li>
     * <li><code>java.io.Serializable[][]</code></li>
     * </ol>
     * </p>
     *
     * @param clazz 要查找的类
     *
     * @return 所有接口的列表，如果指定类为 <code>null</code> ，则返回 <code>null</code>
     */
    @SuppressWarnings("rawtypes")
    public static List<Class> getInterfaces (Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getTypeInfo (clazz).getInterfaces ();
    }

    /**
     * 判断指定类是否为内联类。
     *
     * @param clazz 要查找的类
     *
     * @return 如果是，则返回 <code>true</code>
     */
    public static boolean isInnerClass (Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        return StringUtils.contains (clazz.getName (), INNER_CLASS_SEPARATOR_CHAR);
    }

    /**
     * 检查一组指定类型 <code>fromClasses</code> 的对象是否可以赋值给另一组类型 <code>classes</code>。
     * <p>
     * <p>
     * 此方法可以用来确定指定类型的参数 <code>object1, object2, ...</code> 是否可以用来调用确定参数类型为 <code>class1, class2,
     * ...</code> 的方法。
     * </p>
     * <p>
     * <p>
     * 对于 <code>fromClasses</code> 的每个元素 <code>fromClass</code> 和 <code>classes</code> 的每个元素
     * <code>clazz</code>， 按照如下规则：
     * <p>
     * <ol>
     * <li>如果目标类 <code>clazz</code> 为 <code>null</code> ，总是返回 <code>false</code>。</li>
     * <li>如果参数类型 <code>fromClass</code> 为 <code>null</code> ，并且目标类型 <code>clazz</code> 为非原子类型，则返回
     * <code>true</code>。 因为 <code>null</code> 可以被赋给任何引用类型。</li>
     * <li>调用 <code>Class.isAssignableFrom</code> 方法来确定目标类 <code>clazz</code> 是否和参数类
     * <code>fromClass</code> 相同或是其父类、接口，如果是，则返回 <code>true</code>。</li>
     * <li>如果目标类型 <code>clazz</code> 为原子类型，那么根据 <a href="http://java.sun.com/docs/books/jls/">The
     * Java Language Specification</a> ，sections 5.1.1, 5.1.2, 5.1.4定义的Widening Primitive
     * Conversion规则，参数类型 <code>fromClass</code> 可以是任何能扩展成该目标类型的原子类型及其包装类。 例如， <code>clazz</code> 为
     * <code>long</code> ，那么参数类型可以是 <code>byte</code>、
     * <code>short</code>、<code>int</code>、<code>long</code>、<code>char</code> 及其包装类
     * <code>java.lang.Byte</code>、<code>java.lang.Short</code>、 <code>java.lang.Integer</code>、
     * <code>java.lang.Long</code> 和 <code>java.lang.Character</code> 。如果满足这个条件，则返回
     * <code>true</code>。</li>
     * <li>不满足上述所有条件，则返回 <code>false</code>。</li>
     * </ol>
     * </p>
     *
     * @param classes     目标类型列表，如果是 <code>null</code> 总是返回 <code>false</code>
     * @param fromClasses 参数类型列表， <code>null</code> 表示可赋值给任意非原子类型
     *
     * @return 如果可以被赋值，则返回 <code>true</code>
     */
    @SuppressWarnings("rawtypes")
    public static boolean isAssignable (Class[] classes, Class[] fromClasses) {
        if (!ArrayUtils.isSameLength (fromClasses, classes)) {
            return false;
        }

        if (fromClasses == null) {
            fromClasses = ClassUtils.EMPTY_CLASSES;
        }

        if (classes == null) {
            classes = ClassUtils.EMPTY_CLASSES;
        }

        for (int i = 0; i < fromClasses.length; i++) {
            if (isAssignable (classes[i], fromClasses[i]) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查指定类型 <code>fromClass</code> 的对象是否可以赋值给另一种类型 <code>clazz</code>。
     * <p>
     * <p>
     * 此方法可以用来确定指定类型的参数 <code>object1, object2, ...</code> 是否可以用来调用确定参数类型 <code>class1, class2,
     * ...</code> 的方法。
     * </p>
     * <p>
     * <p>
     * 按照如下规则：
     * <p>
     * <ol>
     * <li>如果目标类 <code>clazz</code> 为 <code>null</code> ，总是返回 <code>false</code>。</li>
     * <li>如果参数类型 <code>fromClass</code> 为 <code>null</code> ，并且目标类型 <code>clazz</code> 为非原子类型，则返回
     * <code>true</code>。 因为 <code>null</code> 可以被赋给任何引用类型。</li>
     * <li>调用 <code>Class.isAssignableFrom</code> 方法来确定目标类 <code>clazz</code> 是否和参数类
     * <code>fromClass</code> 相同或是其父类、接口，如果是，则返回 <code>true</code>。</li>
     * <li>如果目标类型 <code>clazz</code> 为原子类型，那么根据 <a href="http://java.sun.com/docs/books/jls/">The
     * Java Language Specification</a> ，sections 5.1.1, 5.1.2, 5.1.4定义的Widening Primitive
     * Conversion规则，参数类型 <code>fromClass</code> 可以是任何能扩展成该目标类型的原子类型及其包装类。 例如， <code>clazz</code> 为
     * <code>long</code> ，那么参数类型可以是 <code>byte</code>、
     * <code>short</code>、<code>int</code>、<code>long</code>、<code>char</code> 及其包装类
     * <code>java.lang.Byte</code>、<code>java.lang.Short</code>、 <code>java.lang.Integer</code>、
     * <code>java.lang.Long</code> 和 <code>java.lang.Character</code> 。如果满足这个条件，则返回
     * <code>true</code>。</li>
     * <li>不满足上述所有条件，则返回 <code>false</code>。</li>
     * </ol>
     * </p>
     *
     * @param clazz     目标类型，如果是 <code>null</code> 总是返回 <code>false</code>
     * @param fromClass 参数类型， <code>null</code> 表示可赋值给任意非原子类型
     *
     * @return 如果可以被赋值，则返回 <code>null</code>
     */
    public static boolean isAssignable (Class<?> clazz, Class<?> fromClass) {
        if (clazz == null) {
            return false;
        }

        // 如果fromClass是null，只要clazz不是原子类型如int，就一定可以赋值
        if (fromClass == null) {
            return !clazz.isPrimitive ();
        }

        // 如果类相同或有父子关系，当然可以赋值
        if (clazz.isAssignableFrom (fromClass)) {
            return true;
        }

        // 对于原子类型，根据JLS的规则进行扩展
        // 目标class为原子类型时，fromClass可以为原子类型和原子类型的包装类型。
        if (clazz.isPrimitive ()) {
            // boolean可以接受：boolean
            if (Boolean.TYPE.equals (clazz)) {
                return Boolean.class.equals (fromClass);
            }

            // byte可以接受：byte
            if (Byte.TYPE.equals (clazz)) {
                return Byte.class.equals (fromClass);
            }

            // char可以接受：char
            if (Character.TYPE.equals (clazz)) {
                return Character.class.equals (fromClass);
            }

            // short可以接受：short, byte
            if (Short.TYPE.equals (clazz)) {
                return Short.class.equals (fromClass) || Byte.TYPE.equals (fromClass)
                        || Byte.class.equals (fromClass);
            }

            // int可以接受：int、byte、short、char
            if (Integer.TYPE.equals (clazz)) {
                return Integer.class.equals (fromClass) || Byte.TYPE.equals (fromClass)
                        || Byte.class.equals (fromClass) || Short.TYPE.equals (fromClass)
                        || Short.class.equals (fromClass) || Character.TYPE.equals (fromClass)
                        || Character.class.equals ((fromClass));
            }

            // long可以接受：long、int、byte、short、char
            if (Long.TYPE.equals (clazz)) {
                return Long.class.equals (fromClass) || Integer.TYPE.equals (fromClass)
                        || Integer.class.equals (fromClass) || Byte.TYPE.equals (fromClass)
                        || Byte.class.equals (fromClass) || Short.TYPE.equals (fromClass)
                        || Short.class.equals (fromClass) || Character.TYPE.equals (fromClass)
                        || Character.class.equals ((fromClass));
            }

            // float可以接受：float, long, int, byte, short, char
            if (Float.TYPE.equals (clazz)) {
                return Float.class.equals (fromClass) || Long.TYPE.equals (fromClass)
                        || Long.class.equals (fromClass) || Integer.TYPE.equals (fromClass)
                        || Integer.class.equals (fromClass) || Byte.TYPE.equals (fromClass)
                        || Byte.class.equals (fromClass) || Short.TYPE.equals (fromClass)
                        || Short.class.equals (fromClass) || Character.TYPE.equals (fromClass)
                        || Character.class.equals ((fromClass));
            }

            // double可以接受：double, float, long, int, byte, short, char
            if (Double.TYPE.equals (clazz)) {
                return Double.class.equals (fromClass) || Float.TYPE.equals (fromClass)
                        || Float.class.equals (fromClass) || Long.TYPE.equals (fromClass)
                        || Long.class.equals (fromClass) || Integer.TYPE.equals (fromClass)
                        || Integer.class.equals (fromClass) || Byte.TYPE.equals (fromClass)
                        || Byte.class.equals (fromClass) || Short.TYPE.equals (fromClass)
                        || Short.class.equals (fromClass) || Character.TYPE.equals (fromClass)
                        || Character.class.equals ((fromClass));
            }
        }

        return false;
    }

    /**
     * 取得指定类的 <code>TypeInfo</code>。
     *
     * @param type 指定类或接口
     *
     * @return <code>TypeInfo</code> 对象.
     */
    protected static TypeInfo getTypeInfo (Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException ("Parameter clazz should not be null");
        }

        TypeInfo classInfo;

        synchronized (TYPE_MAP) {
            classInfo = (TypeInfo) TYPE_MAP.get (type);

            if (classInfo == null) {
                classInfo = new TypeInfo (type);
                TYPE_MAP.put (type, classInfo);
            }
        }

        return classInfo;
    }

    /**
     * 返回指定类型所对应的primitive类型。
     *
     * @param clazz 要检查的类型
     *
     * @return 如果指定类型为<code>null</code>或不是primitive类型的包装类，则返回<code>null</code> ，否则返回相应的primitive类型。
     */
    public static Class<?> getPrimitiveType (Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        if (clazz.isPrimitive ()) {
            return clazz;
        }

        if (clazz.equals (Long.class)) {
            return long.class;
        }

        if (clazz.equals (Integer.class)) {
            return int.class;
        }

        if (clazz.equals (Short.class)) {
            return short.class;
        }

        if (clazz.equals (Byte.class)) {
            return byte.class;
        }

        if (clazz.equals (Double.class)) {
            return double.class;
        }

        if (clazz.equals (Float.class)) {
            return float.class;
        }

        if (clazz.equals (Boolean.class)) {
            return boolean.class;
        }

        if (clazz.equals (Character.class)) {
            return char.class;
        }

        return null;
    }

    /*
     * ========================================================================== ==
     */
    /* 有关primitive类型的方法。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 返回指定类型所对应的非primitive类型。
     *
     * @param clazz 要检查的类型
     *
     * @return 如果指定类型为<code>null</code>，则返回<code>null</code> ，如果是primitive类型，则返回相应的包装类，否则返回原始的类型。
     */
    public static Class<?> getNonPrimitiveType (Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        if (!clazz.isPrimitive ()) {
            return clazz;
        }

        if (clazz.equals (long.class)) {
            return Long.class;
        }

        if (clazz.equals (int.class)) {
            return Integer.class;
        }

        if (clazz.equals (short.class)) {
            return Short.class;
        }

        if (clazz.equals (byte.class)) {
            return Byte.class;
        }

        if (clazz.equals (double.class)) {
            return Double.class;
        }

        if (clazz.equals (float.class)) {
            return Float.class;
        }

        if (clazz.equals (boolean.class)) {
            return Boolean.class;
        }

        if (clazz.equals (char.class)) {
            return Character.class;
        }

        return null;
    }

    public static PropertyDescriptor[] getPropertyDescriptors (Class<?> clazz) {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo (clazz);
            return beanInfo.getPropertyDescriptors ();
        } catch (IntrospectionException e) {
            throw new RuntimeException (e);
        }
    }

    public static MethodDescriptor[] getMethodDescriptors (Class<?> clazz) {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo (clazz);
            return beanInfo.getMethodDescriptors ();
        } catch (IntrospectionException e) {
            throw new RuntimeException (e);
        }
    }

    public static boolean isBaseType (Class<?> clazz) {
        return String.class.equals (clazz) ||
                clazz.isPrimitive () ||
                PrimitiveUtils.isWrapperClass (clazz) ||
                Date.class.isAssignableFrom (clazz) ||
                Enum.class.equals (clazz);
    }

    /**
     * 代表一个类的信息, 包括父类, 接口, 数组的维数等.
     */
    @SuppressWarnings("rawtypes")
    protected static class TypeInfo {
        private final Class<?> type;
        private final Class<?> componentType;
        private final List<Class> superclasses = new ArrayList<Class> (2);
        private final List<Class> interfaces = new ArrayList<Class> (2);
        private int dimension;

        /**
         * 创建 <code>TypeInfo</code>。
         *
         * @param type 创建指定类的 <code>TypeInfo</code>
         */
        private TypeInfo (Class<?> type) {
            this.type = type;

            // 如果是array, 设置componentType和dimension
            Class<?> componentType = null;

            if (type.isArray ()) {
                componentType = type;

                do {
                    componentType = componentType.getComponentType ();
                    dimension++;
                } while (componentType.isArray ());
            }

            this.componentType = componentType;

            // 取得所有superclass
            if (dimension > 0) {
                // 将primitive类型转换成对应的包装类
                componentType = getNonPrimitiveType (componentType);

                Class<?> superComponentType = componentType.getSuperclass ();

                // 如果是primitive, interface, 则设置其基类为Object.
                if ((superComponentType == null) && !Object.class.equals (componentType)) {
                    superComponentType = Object.class;
                }

                if (superComponentType != null) {
                    Class<?> superclass = getArrayClass (superComponentType, dimension);

                    superclasses.add (superclass);
                    superclasses.addAll (getTypeInfo (superclass).superclasses);
                } else {
                    for (int i = dimension - 1; i >= 0; i--) {
                        superclasses.add (getArrayClass (Object.class, i));
                    }
                }
            } else {
                // 将primitive类型转换成对应的包装类
                type = getNonPrimitiveType (type);

                Class<?> superclass = type.getSuperclass ();

                if (superclass != null) {
                    superclasses.add (superclass);
                    superclasses.addAll (getTypeInfo (superclass).superclasses);
                }
            }

            // 取得所有interface
            if (dimension == 0) {
                Class[] typeInterfaces = type.getInterfaces ();
                List<Class> set = new ArrayList<Class> ();

                for (int i = 0; i < typeInterfaces.length; i++) {
                    Class<?> typeInterface = typeInterfaces[i];

                    set.add (typeInterface);
                    set.addAll (getTypeInfo (typeInterface).interfaces);
                }

                for (Iterator<Class> i = superclasses.iterator (); i.hasNext (); ) {
                    Class<?> typeInterface = (Class<?>) i.next ();

                    set.addAll (getTypeInfo (typeInterface).interfaces);
                }

                for (Iterator<Class> i = set.iterator (); i.hasNext (); ) {
                    Class<?> interfaceClass = (Class<?>) i.next ();

                    if (!interfaces.contains (interfaceClass)) {
                        interfaces.add (interfaceClass);
                    }
                }
            } else {
                for (Iterator<Class> i = getTypeInfo (componentType).interfaces.iterator (); i
                        .hasNext (); ) {
                    Class<?> componentInterface = (Class<?>) i.next ();

                    interfaces.add (getArrayClass (componentInterface, dimension));
                }
            }
        }

        /**
         * 将所有的原子类型转换成对应的包装类，其它类型不变。
         *
         * @param type 要转换的类型
         *
         * @return 非原子类型
         */
        private Class<?> getNonPrimitiveType (Class<?> type) {
            if (type.isPrimitive ()) {
                if (Integer.TYPE.equals (type)) {
                    type = Integer.class;
                } else if (Long.TYPE.equals (type)) {
                    type = Long.class;
                } else if (Short.TYPE.equals (type)) {
                    type = Short.class;
                } else if (Byte.TYPE.equals (type)) {
                    type = Byte.class;
                } else if (Float.TYPE.equals (type)) {
                    type = Float.class;
                } else if (Double.TYPE.equals (type)) {
                    type = Double.class;
                } else if (Boolean.TYPE.equals (type)) {
                    type = Boolean.class;
                } else if (Character.TYPE.equals (type)) {
                    type = Character.class;
                }
            }

            return type;
        }

        /**
         * 取得 <code>TypeInfo</code> 所代表的java类。
         *
         * @return <code>TypeInfo</code> 所代表的java类
         */
        public Class<?> getType () {
            return type;
        }

        /**
         * 取得数组元素的类型。
         *
         * @return 如果是数组, 则返回数组元素的类型, 否则返回 <code>null</code>
         */
        public Class<?> getArrayComponentType () {
            return componentType;
        }

        /**
         * 取得数组的维数。
         *
         * @return 数组的维数. 如果不是数组, 则返回 <code>0</code>
         */
        public int getArrayDimension () {
            return dimension;
        }

        /**
         * 取得所有的父类。
         *
         * @return 所有的父类
         */
        public List<Class> getSuperclasses () {
            return Collections.unmodifiableList (superclasses);
        }

        /**
         * 取得所有的接口。
         *
         * @return 所有的接口
         */
        public List<Class> getInterfaces () {
            return Collections.unmodifiableList (interfaces);
        }
    }
}
