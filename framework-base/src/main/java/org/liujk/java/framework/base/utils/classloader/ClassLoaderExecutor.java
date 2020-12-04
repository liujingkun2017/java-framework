package org.liujk.java.framework.base.utils.classloader;

public interface ClassLoaderExecutor {

    /**
     * 使用classloader作为加载环境，执行className的methodName方法
     *
     * @param className   需要执行实例的完整类名
     * @param methodName  方法名
     * @param classLoader 环境使用的类加载器
     * @param methodParam 方法参数
     * @return
     * @throws Exception
     */
    Object execute(String className, String methodName, ClassLoader classLoader,
                   Object... methodParam) throws Exception;


    /**
     * 使用classloader作为加载环境，执行className的methodName方法
     *
     * @param className        需要执行实例的完整类名
     * @param methodName       方法名
     * @param classLoader      环境使用的类加载器
     * @param classLoaderLevel 类加载器渗透级别
     * @param methodParam      方法参数
     * @return
     * @throws Exception
     */
    Object execute(String className, String methodName, ClassLoader classLoader,
                   ClassLoaderLevel classLoaderLevel, Object... methodParam) throws Exception;


    /**
     * 使用classloader作为加载环境，执行className的methodName方法
     *
     * @param className         需要执行实例的完整类名
     * @param methodName        方法名
     * @param classLoader       环境使用的类加载器
     * @param classLoaderLevel  类加载器渗透级别
     * @param constructorParams classname的构造器参数
     * @param constructorTypes  classname的构造器参数类型
     * @param methodParams      methodname的参数
     * @param methodTypes       methodname的参数类型
     * @return
     * @throws Exception
     */
    Object execute(String className, String methodName, ClassLoader classLoader,
                   ClassLoaderLevel classLoaderLevel, Object[] constructorParams,
                   Class<?>[] constructorTypes, Object[] methodParams, Class<?>[] methodTypes) throws Exception;


    /**
     * 使用classloader作为加载环境，执行className的methodName方法
     *
     * @param className         需要执行实例的完整类名
     * @param methodName        方法名
     * @param classLoader       环境使用的类加载器
     * @param classLoaderLevel  类加载器渗透级别
     * @param constructorParams classname的构造器参数
     * @param methodParams      methodname的参数
     * @return
     * @throws Exception
     */
    Object execute(String className, String methodName, ClassLoader classLoader,
                   ClassLoaderLevel classLoaderLevel, Object[] constructorParams, Object[] methodParams) throws Exception;


    /**
     * 使用classloader作为加载环境，创建classname的实例交给actionname执行
     *
     * @param className   需要执行实例的完整类名
     * @param actionName  指定的Action的实现完整类名
     * @param classLoader 环境使用的类加载器
     * @param param       参数
     * @return
     * @throws Exception
     */
    Object executeAction(String className, String actionName, ClassLoader classLoader, Object... param) throws Exception;


    /**
     * 使用classloader作为加载环境，创建classname的实例交给actionname执行
     *
     * @param className         需要执行实例的完整类名
     * @param actionName        指定的Action的实现完整类名
     * @param classLoader       环境使用的类加载器
     * @param classLoaderLevel  类加载器渗透级别
     * @param constructorParams classname的构造器参数
     * @param constructorTypes  classname的构造器参数类型
     * @param param
     * @return
     * @throws Exception
     */
    Object executeAction(String className, String actionName, ClassLoader classLoader,
                         ClassLoaderLevel classLoaderLevel, Object[] constructorParams,
                         Class<?>[] constructorTypes, Object... param) throws Exception;


}
