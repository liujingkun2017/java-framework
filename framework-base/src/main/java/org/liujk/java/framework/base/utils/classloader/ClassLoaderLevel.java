package org.liujk.java.framework.base.utils.classloader;

public enum ClassLoaderLevel {

    /**
     * 不做任何修改
     */
    NONE,

    /**
     * 替换当前线程的类加载器为指定的类加载器，在执行结束时重置回来
     */
    THREAD_CONTEXT;
}
