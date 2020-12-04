
package org.liujk.java.framework.base.utils.lang.beans.property;



import org.liujk.java.framework.base.utils.lang.ClassUtils;
import org.liujk.java.framework.base.utils.lang.ReflectionUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 说明：
 * <p>
 * 默认的属性实例工厂实现，该实现使用反射创建属性实例。
 * <p>
 * 该工厂支持所有的有默认构造方法的类的实例创建和二维数组实例创建。
 * <p>
 * 对于接口实例创建，支持的有：
 * <ul>
 * <li>java.util.List</li>
 * <li>java.util.Set</li>
 * <li>java.util.Queue</li>
 * <li>java.util.concurrent.BlockingQueue</li>
 * <li>java.util.Collection</li>
 * <li>java.util.concurrent.ConcurrentMap</li>
 * <li>java.util.Map</li>
 * <li>java.util.SortedMap</li>
 * <li>java.util.SortedSet</li>
 * <li>java.util.Deque</li>
 * <li>java.util.concurrent.ConcurrentNavigableMap</li>
 * <li>java.util.NavigableMap</li>
 * <li>java.util.NavigableSet</li>
 * </ul>
 *
 */
public class DefaultPropertyInstanceFactory implements PropertyInstanceFactory {

    private static final Class<?> DEQUE_CLASS;

    private static final Class<?> CONCURRENT_NAVIGABLE_MAP_CLASS;

    private static final Class<?> BLOCKING_DEQUE_CLASS;

    private static final Class<?> NAVIGABLE_MAP_CLASS;

    private static final Class<?> NAVIGABLE_SET_CLASS;

    static {
        DEQUE_CLASS = loadClass ("java.util.Deque");
        CONCURRENT_NAVIGABLE_MAP_CLASS = loadClass ("java.util.concurrent.ConcurrentNavigableMap");
        BLOCKING_DEQUE_CLASS = loadClass ("java.util.concurrent.BlockingDeque");
        NAVIGABLE_MAP_CLASS = loadClass ("java.util.NavigableMap");
        NAVIGABLE_SET_CLASS = loadClass ("java.util.NavigableSet");
    }

    private static Class<?> getType (Class<?> propertyType, Class<?> clazz, String className) {
        if (clazz == null) {
            return null;
        }
        if (propertyType == clazz) {
            return loadClass (className);
        }
        return null;
    }

    private static Class<?> loadClass (String className) {
        try {
            return ClassUtils.applicationClass ("className");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object newInstance (Class<?> type) throws NoSuchMethodException {
        if (type.isArray ()) {
            return Array.newInstance (type.getComponentType (), 0);
        } else if (List.class == type) {
            return ReflectionUtils.createObject (ArrayList.class);
        } else if (Set.class == type) {
            return ReflectionUtils.createObject (HashSet.class);
        } else if (Queue.class == type) {
            return ReflectionUtils.createObject (LinkedList.class);
        } else if (BlockingQueue.class == type) {
            return ReflectionUtils.createObject (LinkedBlockingQueue.class);
        } else if (Collection.class == type) {
            return ReflectionUtils.createObject (ArrayList.class);
        } else if (ConcurrentMap.class == type) {
            return ReflectionUtils.createObject (ConcurrentHashMap.class);
        } else if (Map.class == type) {
            return ReflectionUtils.createObject (HashMap.class);
        } else if (SortedMap.class == type) {
            return ReflectionUtils.createObject (TreeMap.class);
        } else if (SortedSet.class == type) {
            return ReflectionUtils.createObject (TreeSet.class);
        } else if (Stack.class == type) {
            return ReflectionUtils.createObject (Stack.class);
        }
        Class<?> newType = getType (type, DEQUE_CLASS, "java.util.LinkedList");
        if (newType != null) {
            ReflectionUtils.createObject (newType);
        }
        newType = getType (type, CONCURRENT_NAVIGABLE_MAP_CLASS,
                           "java.util.concurrent.ConcurrentSkipListMap");
        if (newType != null) {
            ReflectionUtils.createObject (newType);
        }
        newType = getType (type, BLOCKING_DEQUE_CLASS, "java.util.concurrent.LinkedBlockingDeque");
        if (newType != null) {
            ReflectionUtils.createObject (newType);
        }
        newType = getType (type, NAVIGABLE_MAP_CLASS, "java.util.TreeMap");
        if (newType != null) {
            ReflectionUtils.createObject (newType);
        }
        newType = getType (type, NAVIGABLE_SET_CLASS, "java.util.TreeSet");
        if (newType != null) {
            ReflectionUtils.createObject (newType);
        }
        return ReflectionUtils.createObject (type);
    }

}
