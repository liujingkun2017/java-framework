
package org.liujk.java.framework.base.utils.lang.beans.reference;


import org.liujk.java.framework.base.utils.lang.ClassUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.*;

/**
 * 说明：
 * <p>
 * 一个或者一组服务提供者的持有器。
 * <p>
 * 提供者配置文件文件名称是服务类型的完全限定<a href=
 * "http://download.oracle.com/javase/1.5.0/docs/api/java/lang/ClassLoader.html#name"
 * >二进制名称</a>。该文件包含一个具体提供者类的完全限定二进制名称列表 ，每行一个。忽略各名称周围的空格、制表符和空行。注释字符为 '#' ('&#92;u0023', NUMBER
 * SIGN)；忽略每行第一个注释字符后面的所有字符。文件必须使用 UTF-8 编码。
 * <p>
 * <p>
 * 如果在多个配置文件中指定了一个特定的具体提供者类，或在同一配置文件中多次被指定，则忽略重复的指定。指定特定提供者的配置文件不必像提供者本身一样位于同一个 jar
 * 文件或其他的分布式单元中。提供者必须可以从最初为了定位配置文件而查询的类加载器访问；注意，这不一定是实际加载文件的类加载器。
 * <p>
 * <p>
 * 该类是线程安全的。
 *
 * @param <E> 服务类型
 *
 * @see ServiceLoader
 */
public class Service<E> implements Iterable<E> {

    private static final String PREFIX = "META-INF/";

    private final List<E> services;

    private final E first;

    /**
     * 构造一个服务提供者的持有器。
     *
     * @param serviceType 服务提供者的 {@link Class} 类型信息。
     * @param services    持有服务提供者实例的列表。
     *
     * @throws ServiceConfigurationError 如果 services 为空。
     */
    protected Service (Class<E> serviceType, List<E> services) {
        if (!services.isEmpty ()) {
            this.first = services.get (0);
        } else {
            throw fail (serviceType, "没有服务提供者 '" + serviceType.getName () + "' 对应的服务提供者实现。");
        }
        this.services = Collections.unmodifiableList (services);
    }

    /**
     * 构造一个服务提供者的持有器。
     *
     * @param serviceType               服务提供者的 {@link Class} 类型信息。
     * @param loadServiceClassLoader    用于加载服务提供者的类加载器。
     * @param rootPath                  资源目录 META-INF 中放置提供者配置文件根路径。
     * @param findConfigFileClassLoader 用于查找提供者配置文件的类加载器。
     *
     * @throws ServiceConfigurationError 如果加载过程中发生错误。
     */
    protected Service (Class<E> serviceType, ClassLoader loadServiceClassLoader, String rootPath,
                       ClassLoader findConfigFileClassLoader) {
        if (rootPath.startsWith ("/")) {
            rootPath = rootPath.substring (1);
        }
        if (!rootPath.endsWith ("/")) {
            rootPath = rootPath + "/";
        }
        String fullName = PREFIX + rootPath + serviceType.getName ();
        Enumeration<URL> configs;
        try {
            if (findConfigFileClassLoader == null) {
                configs = ClassLoader.getSystemResources (fullName);
            } else {
                configs = findConfigFileClassLoader.getResources (fullName);
            }
        } catch (IOException e) {
            throw fail (serviceType, "获取服务提供者配置文件的URL时发生错误。", e);
        }
        Set<String> serviceNames = new HashSet<String> ();
        ArrayList<E> services = new ArrayList<E> ();
        while (configs.hasMoreElements ()) {
            parseAndMerge (serviceType, services, serviceNames, configs.nextElement (),
                           loadServiceClassLoader);
        }
        services.trimToSize ();
        if (!services.isEmpty ()) {
            this.first = services.get (0);
        } else {
            throw fail (serviceType,
                        "[" + fullName + "]下没有服务提供者 [" + serviceType.getName () + "] 对应的服务提供者实现。");
        }
        this.services = Collections.unmodifiableList (services);
    }

    @SuppressWarnings("unchecked")
    private static <E> void parseAndMerge (Class<E> serviceType, List<E> serviceList,
                                           Set<String> serviceNames, URL url, ClassLoader classLoader) {
        BufferedReader in = null;
        try {
            in = new BufferedReader (new InputStreamReader (url.openStream (), "UTF-8"));
            int num = 1, tempIndex, n, cp;
            for (String line = in.readLine (); line != null; line = in.readLine ()) {
                tempIndex = line.indexOf ("#");
                if (tempIndex >= 0) {
                    line = line.substring (0, tempIndex);
                }
                line = line.trim ();
                n = line.length ();
                if (n == 0) {
                    continue;
                }
                if ((line.indexOf (' ') >= 0) || (line.indexOf ('\t') >= 0)) {
                    throw fail (serviceType, url, num, "错误的配置文件语法：'" + line + "' 。");
                }
                cp = line.codePointAt (0);
                if (!Character.isJavaIdentifierStart (cp)) {
                    throw fail (serviceType, url, num, "错误的提供者类名称：：'" + line + "' 。");
                }
                for (int i = Character.charCount (cp); i < n; i += Character.charCount (cp)) {
                    cp = line.codePointAt (i);
                    if (!Character.isJavaIdentifierPart (cp) && (cp != '.')) {
                        throw fail (serviceType, url, num, "错误的提供者类名称：'" + line + "' 。");
                    }
                }
                if (!serviceNames.contains (line)) {
                    serviceNames.add (line);
                    try {
                        Class<?> c = ClassUtils.applicationClass (line, classLoader);
                        if (!serviceType.isAssignableFrom (c)) {
                            throw fail (serviceType, "服务提供者实现 '" + line + "' 不是服务提供者的类型；该问题发生在 URL：'"
                                    + url + "' ；第 '" + num + "' 行");
                        }
                        serviceList.add ((E) c.newInstance ());
                    } catch (TypeNotPresentException e) {
                        throw fail (serviceType, "服务提供者实现 '" + line + "' 的类没有找到；该问题发生在 URL：'" + url
                                + "' ；第 '" + num + "' 行", e);
                    } catch (InstantiationException e) {
                        throw fail (serviceType, "服务提供者实现'" + line + "' 无法创建；该问题发生在 URL：'" + url
                                + "' ；第 '" + num + "' 行", e);
                    } catch (Exception e) {
                        throw fail (serviceType,
                                    "创建服务提供者实现的实例时发生错误；该问题发生在 URL：'" + url + "' ；第 '" + num + "' 行", e);
                    }
                }
                num++;
            }
        } catch (IOException e) {
            throw fail (serviceType, "读取配置文件出错", e);
        } finally {
            if (in != null) {
                try {
                    in.close ();
                } catch (IOException e) {
                    // 不处理
                }
            }
        }
    }

    static ServiceConfigurationError fail (Class<?> serviceType, String msg, Throwable cause)
            throws ServiceConfigurationError {
        return new ServiceConfigurationError (serviceType.getName () + ": " + msg, cause);
    }

    static ServiceConfigurationError fail (Class<?> serviceType, String msg)
            throws ServiceConfigurationError {
        return new ServiceConfigurationError (serviceType.getName () + ": " + msg);
    }

    static ServiceConfigurationError fail (Class<?> serviceType, URL u, int line, String msg)
            throws ServiceConfigurationError {
        return fail (serviceType, u + "：第'" + line + "'行：" + msg);
    }

    /**
     * 得到 第一个服务加载器实例（如果有）。
     *
     * @return 第一个服务加载器实例，如果没有返回 null 。
     */
    public E get () {
        return this.first;
    }

    @Override
    public Iterator<E> iterator () {
        return this.services.iterator ();
    }

    /**
     * 得到 所有服务加载器实例的不可修改的列表。
     *
     * @return 所有服务加载器实例的不可修改的列表。
     */
    public List<E> list () {
        return this.services;
    }

}
