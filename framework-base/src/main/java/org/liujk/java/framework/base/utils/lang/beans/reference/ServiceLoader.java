
package org.liujk.java.framework.base.utils.lang.beans.reference;



import org.liujk.java.framework.base.utils.lang.ClassUtils;
import org.springframework.util.Assert;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 说明：
 * <p>
 * 一个简单的服务提供者加载设施。
 * <p>
 * 服务 是一个熟知的接口和类（通常为抽象类）集合。服务提供者 是服务的特定实现。提供者中的类通常实现接口，并子类化在服务本身中定义的子类。服务提供者可以以扩展的形式安装在 Java
 * 平台的实现中，也就是将 jar 文件放入任意常用的扩展目录中。也可通过将提供者加入应用程序类路径，或者通过其他某些特定于平台的方式使其可用。
 * <p>
 * <p>
 * 为了加载，服务由单个类型表示，也就是单个接口或抽象类。（可以使用具体类，但建议不要这样做。）一个给定服务的提供者包含一个或多个具体类，
 * 这些类扩展了此服务类型，具有特定于提供者的数据和代码。提供者类 通常不是整个提供者本身而是一个代理，它包含足够的信息来决定提供者是否能满足特定请求，还包含可以根据需要创建实际提供者的代码
 * 。提供者类的详细信息高度特定于服务 ；任何单个类或接口都不能统一它们，因此这里没有定义任何这种类型。此设施唯一强制要求的是，提供者类必须具有不带参数的构造方法，以便它们可以在加载中被实例化。
 * <p>
 * <p>
 * 通过在资源目录 META-INF 中放置提供者配置文件来标识服务提供者。
 * <p>
 * <p>
 * rootPath与serviceType的二进制名称构成了整个提供者配置文件文件路径。
 * <p>
 * <p>
 * <strong>示例：</strong>假定服务类型为 com.example.CodecSet，它用来表示某些协议的编码器/解码器对集合。在这种情况下，它是一个具有两种抽象方法的抽象类：
 * <p>
 *
 * <pre>
 * <code>
 * public abstract Encoder getEncoder(String encodingName);
 * public abstract Decoder getDecoder(String encodingName);
 * </code>
 * 每种方法都返回一个相应的对象；如果提供者不支持给定编码，则返回 null。典型的提供者支持一种以上的编码。
 * 如果 com.example.impl.StandardCodecs 是 CodecSet 服务的实现，则其 jar 文件还包含一个指定如下的文件：
 * <code>
 * META-INF/services/com.example.CodecSet
 * </code>
 * 此文件包含一行：
 * <code>
 * com.example.impl.StandardCodecs    # Standard codecsCodecSet
 * </code>
 * 类在初始化时创建并保存一个服务实例：
 * <code>
 * private static Service&lt;CodecSet&gt; service = ServiceLoader.load("services").load(CodecSet.class);
 * </code>
 * 为了查找给定编码名称的编码器，它定义了一个静态工厂方法，该方法迭代所有已知并可用的提供者，只在找到适当的编码器或迭代完提供者时返回。
 * <code>
 * public static Encoder getEncoder(String encodingName) {
 *     for (CodecSet cp :service) {
 *         Encoder enc = cp.getEncoder(encodingName);
 *         if (enc != null) {
 *             return enc;
 *         }
 *     }
 *     return null;
 * }
 *  </code>
 *  getDecoder 方法的定义类似。
 * </pre>
 * <p>
 * <p>
 * 如果请求的资源不存在则抛出 {@link ServiceConfigurationError} 。
 * <p>
 * <p>
 * 该类的静态方法和实例方法都是线程安全的。
 *
 * @see Service
 */
public class ServiceLoader {
    private static final ConcurrentMap<KeyWrapper, ServiceLoader> SERVICE_LOADER_MAP
            = new ConcurrentHashMap<KeyWrapper, ServiceLoader> ();

    private static final FinalizableReferenceQueue FINALIZABLE_REFERENCE_QUEUE = new FinalizableReferenceQueue (
            "ServiceLoaderReferenceQueue");

    private final String rootPath;

    private final Reference<ClassLoader> classLoader;

    private final ConcurrentMap<Class<?>, Service<?>> serviceMap = new ConcurrentReferenceMap<Class<?>, Service<?>> (
            ReferenceKeyType.WEAK, ReferenceValueType.STRONG);

    ServiceLoader (String rootPath, ClassLoader classLoader) {
        this.rootPath = rootPath;
        this.classLoader = new WeakReference<ClassLoader> (classLoader);
    }

    /**
     * 通过给定的资源目录 META-INF 中放置提供者配置文件根路径加载一个服务提供者加载器。
     * <p>
     * 该方法是实际为调用 <code>load(rootPath, ClassUtils.getDefaultClassLoader())</code>
     *
     * @param rootPath 资源目录 META-INF 中放置提供者配置文件根路径。
     *
     * @return 参数对应的服务提供者加载器。
     * @throws IllegalAccessException 如果 rootPath 为 null 。
     * @see #load(String, ClassLoader)
     */
    public static ServiceLoader load (String rootPath) {
        return load (rootPath, ClassUtils.getDefaultClassLoader ());
    }

    /**
     * 通过给定的资源目录 META-INF 中放置提供者配置文件根路径与类加载器加载一个服务提供者加载器。
     * <p>
     * 对于rootPath和classLoader都相同的情况，返回的是同一个服务提供者加载器实例。
     *
     * @param rootPath    资源目录 META-INF 中放置提供者配置文件根路径。
     * @param classLoader 加载服务提供者加载器所使用的类加载器，如果为 null 则使用 {@link ClassUtils#getDefaultClassLoader()} 得到的类加载器
     *                    。
     *
     * @return 参数对应的服务提供者加载器。
     * @throws IllegalAccessException 如果 rootPath 为 null 。
     */
    public static ServiceLoader load (String rootPath, ClassLoader classLoader) {
        Assert.notNull (rootPath, "{rootPath} 不能为 'null'。");
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader ();
        }
        KeyWrapper key = new KeyWrapper (rootPath, classLoader);
        ServiceLoader serviceLoader = SERVICE_LOADER_MAP.get (key);
        if (serviceLoader == null) {
            serviceLoader = new ServiceLoader (rootPath, classLoader);
            ServiceLoader oldServiceLoader = SERVICE_LOADER_MAP.putIfAbsent (key, serviceLoader);
            if (oldServiceLoader != null) {
                serviceLoader = oldServiceLoader;
            }
        }
        return serviceLoader;
    }

    /**
     * 使用扩展类加载器调用 {@link #load(String, ClassLoader)} 完成加载。
     *
     * @param rootPath 资源目录 META-INF 中放置提供者配置文件根路径。
     *
     * @return 参数对应的服务提供者加载器。
     * @throws IllegalAccessException 如果 rootPath 为 null 。
     * @see #load(String, ClassLoader)
     */
    public static ServiceLoader loadInstalled (String rootPath) {
        ClassLoader cl = ClassLoader.getSystemClassLoader ();
        ClassLoader prev = null;
        while (cl != null) {
            prev = cl;
            cl = cl.getParent ();
        }
        return load (rootPath, prev);
    }

    /**
     * 得到 资源目录 META-INF 中放置提供者配置文件根路径。
     *
     * @return 资源目录 META-INF 中放置提供者配置文件根路径。
     */
    public String getRootPath () {
        return this.rootPath;
    }

    /**
     * 得到 当前加载器所使用的类加载器。
     *
     * @return 当前加载器所使用的类加载器。
     */
    public ClassLoader getClassLoader () {
        return this.classLoader.get ();
    }

    /**
     * 清除此加载器的 {@link Service} 缓存，以重载此加载器加载的所有 {@link Service} 。
     */
    public void reload () {
        this.serviceMap.clear ();
    }

    /**
     * 针对给定服务类型和类加载器创建新的服务。
     * <p>
     * 该方法是实际为调用 <code>load(serviceType, ClassUtils.getDefaultClassLoader())</code>
     *
     * @param serviceType 表示服务的接口或抽象类。
     *
     * @return 对应的服务。
     * @throws IllegalAccessException 如果 serviceType 为 null 。
     */
    public <E> Service<E> load (Class<E> serviceType) {
        return load (serviceType, ClassUtils.getDefaultClassLoader ());
    }

    /**
     * 针对给定服务类型和类加载器创建新的服务。
     * <p>
     * 对于相同的serviceType返回的是同一个 {@link Service} 的实例。
     *
     * @param serviceType 表示服务的接口或抽象类。
     * @param classLoader 用来加载提供者配置文件和提供者类的类加载器。
     *
     * @return 对应的服务。
     * @throws IllegalAccessException 如果 serviceType 或者 classLoader 为 null 。
     */
    @SuppressWarnings("unchecked")
    public <E> Service<E> load (Class<E> serviceType, ClassLoader classLoader) {
        Assert.notNull (serviceType, "{serviceType} 不能为 'null'。");
        Assert.notNull (classLoader, "{classLoader} 不能为 'null'。");
        Service<E> service = (Service<E>) this.serviceMap.get (serviceType);
        if (service == null) {
            service = new Service<E> (serviceType, classLoader, this.rootPath,
                                      this.classLoader.get ());
            Service<E> oldService = (Service<E>) this.serviceMap.putIfAbsent (serviceType, service);
            if (oldService != null) {
                service = oldService;
            }
        }
        return service;
    }

    static class KeyWrapper {

        private final String name;

        private final Reference<ClassLoader> classLoader;

        KeyWrapper (String name, ClassLoader classLoader) {
            this.name = name;
            this.classLoader = new FinalizableWeakReference<ClassLoader> (classLoader,
                                                                          FINALIZABLE_REFERENCE_QUEUE) {

                @Override
                public void finalizeReferent () {
                    SERVICE_LOADER_MAP.remove (this);
                }
            };
        }

        public String getName () {
            return this.name;
        }

        public ClassLoader getClassLoader () {
            return this.classLoader.get ();
        }

        @Override
        public int hashCode () {
            final int prime = 31;
            int result = 1;
            ClassLoader classLoader = getClassLoader ();
            result = prime * result + ((classLoader == null) ? 0 : classLoader.hashCode ());
            result = prime * result + ((getName () == null) ? 0 : getName ().hashCode ());
            return result;
        }

        @Override
        public boolean equals (Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass () != obj.getClass ()) {
                return false;
            }
            KeyWrapper other = (KeyWrapper) obj;
            ClassLoader classLoader = getClassLoader ();
            ClassLoader otherClassLoader = other.getClassLoader ();
            if (classLoader == null) {
                if (this.classLoader == other.classLoader) {
                    return true;
                }
                if (otherClassLoader != null) {
                    return false;
                }
            } else if (!classLoader.equals (otherClassLoader)) {
                return false;
            }
            if (getName () == null) {
                if (other.getName () != null) {
                    return false;
                }
            } else if (!getName ().equals (other.getName ())) {
                return false;
            }
            return true;
        }

    }

}
