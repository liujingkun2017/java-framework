package org.liujk.java.framework.base.utils.classloader;

public interface Action<T, R> {


    /**
     * 执行逻辑
     *
     * @param t           执行对象的类型
     * @param classLoader 环境的类加载器
     * @param param       使用的参数
     * @return
     * @throws Exception
     */
    R execute(T t, ClassLoader classLoader, Object... param) throws Exception;

}
