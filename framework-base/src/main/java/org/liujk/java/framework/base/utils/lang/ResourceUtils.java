
package org.liujk.java.framework.base.utils.lang;



import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 说明：
 * <p>
 * 资源工具。
 *
 */
public class ResourceUtils {
    static final String SEPARATOR = '\\' == File.separatorChar ? "\\\\" : File.separator;

    /**
     * 得到 classpath 下的 resourceName 所对应的资源的一组 {@link URL} 。
     * <p>
     * 该方法查找有几个优先级，从高到低依次为：
     * <p>
     *
     * <pre>
     *  线程中的类加载器中使用 {@link ClassLoader#getResources(String)} 查找。
     *  使用加载 ResourceUtils 的类加载器的 {@link ClassLoader#getResources(String)} 查找。
     *  使用 callingClass 参数的类加载器的 {@link ClassLoader#getResources(String)} 查找。
     * </pre>
     * <p>
     * <p>
     * 在按照优先级查询结束后，如果没有找到资源并且 resourceName 的第一个字符不是‘/’，则会在 resourceName 前加上‘/’后再次调用一次该方法。
     *
     * @param resourceName 资源的名称。
     * @param callingClass 指定一个用于在加载该 {@link Class} 的类加载器上搜索资源的 {@link Class} 对象。
     * @param aggregate    如果该参数为 true ，则会在找到资源后继续往更低级别找，并将找到的资源 {@link URL} 加入返回结果中。
     *
     * @return classpath 下的 resourceName 所对应的资源的一组 {@link URL} 。
     * @throws IOException 如果发生 I/O 异常。
     */
    public static Iterator<URL> getResources (String resourceName, Class<?> callingClass,
                                              boolean aggregate) throws IOException {
        AggregateIterator<URL> iterator = new AggregateIterator<URL> ();
        iterator.addEnumeration (
                Thread.currentThread ().getContextClassLoader ().getResources (resourceName));
        if (!iterator.hasNext () || aggregate) {
            iterator.addEnumeration (
                    ResourceUtils.class.getClassLoader ().getResources (resourceName));
        }
        if (!iterator.hasNext () || aggregate) {
            ClassLoader cl = callingClass.getClassLoader ();

            if (cl != null) {
                iterator.addEnumeration (cl.getResources (resourceName));
            }
        }
        if (!iterator.hasNext () && resourceName != null
                && (resourceName.length () == 0 || resourceName.charAt (0) != '/')) {
            return getResources ('/' + resourceName, callingClass, aggregate);
        }
        return iterator;
    }

    /**
     * 得到 classpath 下的 resourceName 所对应的一个资源的 {@link URL} 。
     * <p>
     * 该方法查找有几个优先级，从高到低依次为：
     * <p>
     *
     * <pre>
     *  线程中的类加载器中使用 {@link ClassLoader#getResources(String)} 查找。
     *  使用加载 ResourceUtils 的类加载器的 {@link ClassLoader#getResources(String)} 查找。
     *  使用 callingClass 参数的类加载器的 {@link ClassLoader#getResources(String)} 查找。
     * </pre>
     * <p>
     * <p>
     * 在按照优先级查询结束后，如果没有找到资源并且 resourceName 的第一个字符不是‘/’，则会在 resourceName 前加上‘/’后再次调用一次该方法。
     *
     * @param resourceName 资源的名称。
     * @param callingClass 指定一个用于在加载该 {@link Class} 的类加载器上搜索资源的 {@link Class} 对象。
     *
     * @return classpath 下的 resourceName 所对应的一个资源的 {@link URL} 。
     */
    public static URL getResource (String resourceName, Class<?> callingClass) {
        URL url = Thread.currentThread ().getContextClassLoader ().getResource (resourceName);

        if (url == null) {
            url = ResourceUtils.class.getClassLoader ().getResource (resourceName);
        }

        if (url == null) {
            ClassLoader cl = callingClass.getClassLoader ();

            if (cl != null) {
                url = cl.getResource (resourceName);
            }
        }

        if ((url == null) && (resourceName != null)
                && ((resourceName.length () == 0) || (resourceName.charAt (0) != '/'))) {
            return getResource ('/' + resourceName, callingClass);
        }

        return url;
    }

    /**
     * 得到 classpath 下的 resourceName 所对应的一个资源的输入流 。
     * <p>
     * 该方法调用 {@link #getResource(String, Class)} 得到 URL 后调用 {@link URL#openStream()} 得到输入流。
     *
     * @param resourceName 资源的名称。
     * @param callingClass 指定一个用于在加载该 {@link Class} 的类加载器上搜索资源的 {@link Class} 对象。
     *
     * @return classpath 下的 resourceName 所对应的一个资源的输入流 。
     * @see #getResource(String, Class)
     */
    public static InputStream getResourceAsStream (String resourceName, Class<?> callingClass) {
        URL url = getResource (resourceName, callingClass);

        try {
            return (url != null) ? url.openStream () : null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 通过文件系统路径列表构造一个资源列表
     *
     * @param filePaths 文件系统路径列表
     *
     * @return 文件系统路径列表对应的资源列表
     */
    public static List<Resource> buildFileSystemResources (List<String> filePaths) {
        if (filePaths == null) {
            return null;
        }
        List<Resource> resources = new ArrayList<Resource> (filePaths.size ());
        for (String filePath : filePaths) {
            resources.add (new FileSystemResource(filePath == null ? "" : filePath.trim ()));
        }
        return resources;
    }

    /**
     * 通过文件系统路径列表构造一个资源列表
     *
     * @param filePaths 文件系统路径列表
     *
     * @return 文件系统路径列表对应的资源列表
     */
    public static List<Resource> buildFileSystemResources (String[] filePaths) {
        return buildFileSystemResources (Arrays.asList (filePaths));
    }

    /**
     * 通过类路径列表构造一个资源列表
     *
     * @param filePaths 类路径路径列表
     *
     * @return 类路径列表对应的资源列表
     */
    public static List<Resource> buildClassPathResources (List<String> filePaths) {
        List<Resource> resources = new ArrayList<Resource> (filePaths.size ());
        for (String filePath : filePaths) {
            resources.add (new ClassPathResource(filePath == null ? null : filePath.trim ()));
        }
        return resources;
    }

    /**
     * 通过类路径列表构造一个资源列表
     *
     * @param filePaths 类路径路径列表
     *
     * @return 类路径列表对应的资源列表
     */
    public static List<Resource> buildClassPathResources (String[] filePaths) {
        return buildClassPathResources (Arrays.asList (filePaths));
    }

    /**
     * 通过输入流列表构造一个资源列表
     *
     * @param inputStreams 输入流列表
     *
     * @return 输入流列表对应的资源列表
     */
    public static List<Resource> buildInputStreamResources (List<InputStream> inputStreams) {
        List<Resource> resources = new ArrayList<Resource> (inputStreams.size ());
        for (InputStream inputStream : inputStreams) {
            resources.add (new InputStreamResource(inputStream));
        }
        return resources;
    }

    /**
     * 通过输入流列表构造一个资源列表
     *
     * @param inputStreams 输入流列表
     *
     * @return 输入流列表对应的资源列表
     */
    public static List<Resource> buildInputStreamResources (InputStream[] inputStreams) {
        return buildInputStreamResources (Arrays.asList (inputStreams));
    }

    static class AggregateIterator<E> implements Iterator<E> {
        LinkedList<Enumeration<E>> enums = new LinkedList<Enumeration<E>> ();
        Enumeration<E> cur = null;
        E next = null;
        Set<E> loaded = new HashSet<E> ();

        public AggregateIterator<E> addEnumeration (Enumeration<E> e) {
            if (e.hasMoreElements ()) {
                if (this.cur == null) {
                    this.cur = e;
                    this.next = e.nextElement ();
                    this.loaded.add (this.next);
                } else {
                    this.enums.add (e);
                }
            }
            return this;
        }

        @Override
        public boolean hasNext () {
            return (this.next != null);
        }

        @Override
        public E next () {
            if (this.next != null) {
                E prev = this.next;
                this.next = loadNext ();
                return prev;
            } else {
                throw new NoSuchElementException ();
            }
        }

        private Enumeration<E> determineCurrentEnumeration () {
            if (this.cur != null && !this.cur.hasMoreElements ()) {
                if (this.enums.size () > 0) {
                    this.cur = this.enums.removeLast ();
                } else {
                    this.cur = null;
                }
            }
            return this.cur;
        }

        private E loadNext () {
            if (determineCurrentEnumeration () != null) {
                E tmp = this.cur.nextElement ();
                while (this.loaded.contains (tmp)) {
                    tmp = loadNext ();
                    if (tmp == null) {
                        break;
                    }
                }
                if (tmp != null) {
                    this.loaded.add (tmp);
                }
                return tmp;
            }
            return null;

        }

        @Override
        public void remove () {
            throw new UnsupportedOperationException ();
        }
    }
}
