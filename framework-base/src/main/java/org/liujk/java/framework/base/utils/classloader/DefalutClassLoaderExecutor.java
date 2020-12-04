package org.liujk.java.framework.base.utils.classloader;

import org.apache.commons.lang.reflect.ConstructorUtils;
import org.apache.commons.lang.reflect.MethodUtils;

public class DefalutClassLoaderExecutor implements ClassLoaderExecutor {
    @Override
    public Object execute(String className,
                          String methodName,
                          ClassLoader classLoader,
                          Object... methodParam) throws Exception {
        return execute(className, methodName, classLoader, ClassLoaderLevel.NONE,
                null, null, null, null);
    }

    @Override
    public Object execute(String className,
                          String methodName,
                          ClassLoader classLoader,
                          ClassLoaderLevel classLoaderLevel,
                          Object... methodParam) throws Exception {
        return execute(className, methodName, classLoader, classLoaderLevel,
                null, null, methodParam, null);
    }

    @Override
    public Object execute(String className,
                          String methodName,
                          ClassLoader classLoader,
                          ClassLoaderLevel classLoaderLevel,
                          Object[] constructorParams,
                          Class<?>[] constructorTypes,
                          Object[] methodParams,
                          Class<?>[] methodTypes) throws Exception {

        Object result = null;
        switch (classLoaderLevel) {
            case NONE:
                result = doExecute(className, methodName, classLoader, classLoaderLevel,
                        constructorParams, constructorTypes, methodParams, methodTypes);
                break;
            case THREAD_CONTEXT:
                ClassLoader oldContextLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(classLoader);
                try {
                    result = doExecute(className, methodName, classLoader, classLoaderLevel,
                            constructorParams, constructorTypes, methodParams, methodTypes);
                } finally {
                    Thread.currentThread().setContextClassLoader(oldContextLoader);
                }
                break;
            default:
                break;
        }

        return result;
    }

    @Override
    public Object execute(String className,
                          String methodName,
                          ClassLoader classLoader,
                          ClassLoaderLevel classLoaderLevel,
                          Object[] constructorParams,
                          Object[] methodParams) throws Exception {
        return execute(className, methodName, classLoader, classLoaderLevel, constructorParams,
                null, methodParams, null);
    }


    protected Object doExecute(String className, String methodName, ClassLoader classLoader,
                               ClassLoaderLevel classLoaderLevel, Object[] constructorParams,
                               Class<?>[] constructorTypes, Object[] methodParams, Class<?>[] methodTypes) throws Exception {
        Class<?> clazz = classLoader.loadClass(className);
        Object obj = ConstructorUtils.invokeConstructor(clazz, constructorParams, constructorTypes);
        return MethodUtils.invokeMethod(obj, methodName, methodParams, methodTypes);
    }

    @Override
    public Object executeAction(String className,
                                String actionName,
                                ClassLoader classLoader,
                                Object... param) throws Exception {
        return doExecuteAction(className, actionName, classLoader, ClassLoaderLevel.NONE,
                null, null, param);
    }

    @Override
    public Object executeAction(String className,
                                String actionName,
                                ClassLoader classLoader,
                                ClassLoaderLevel classLoaderLevel,
                                Object[] constructorParams,
                                Class<?>[] constructorTypes,
                                Object... param) throws Exception {

        Object result = null;
        switch (classLoaderLevel) {
            case NONE:
                result = doExecuteAction(className, actionName, classLoader, classLoaderLevel,
                        constructorParams, constructorTypes, param);
                break;
            case THREAD_CONTEXT:
                ClassLoader oldContextLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(classLoader);
                try {
                    result = doExecuteAction(className, actionName, classLoader, classLoaderLevel,
                            constructorParams, constructorTypes, param);
                } finally {
                    Thread.currentThread().setContextClassLoader(oldContextLoader);
                }
                break;
            default:
                break;
        }

        return result;
    }


    protected Object doExecuteAction(String className, String actionName, ClassLoader classLoader,
                                     ClassLoaderLevel classLoaderLevel, Object[] constructorParams,
                                     Class<?>[] constructorTypes, Object... param) throws Exception {
        Class<?> clazz = classLoader.loadClass(className);
        Object obj = ConstructorUtils.invokeConstructor(clazz, constructorParams, constructorTypes);
        Class<?> actionClass = classLoader.loadClass(actionName);
        return MethodUtils.invokeMethod(actionClass.newInstance(), "execute",
                new Object[]{obj, classLoader, param},
                new Class<?>[]{Object.class, ClassLoader.class, Object[].class});
    }
}
