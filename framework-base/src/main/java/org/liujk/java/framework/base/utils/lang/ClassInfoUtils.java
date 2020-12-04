
package org.liujk.java.framework.base.utils.lang;




import org.liujk.java.framework.base.utils.lang.beans.ClassInfo;
import org.liujk.java.framework.base.utils.lang.beans.reference.ConcurrentReferenceMap;
import org.liujk.java.framework.base.utils.lang.beans.reference.ReferenceKeyType;
import org.liujk.java.framework.base.utils.lang.beans.reference.ReferenceValueType;

/**
 * 说明：
 * <p>
 * class信息获取工具类
 *
 */
public class ClassInfoUtils {
    /**
     * 缓存的 map
     */
    private static final ConcurrentReferenceMap<Class<?>, ClassInfo> CACHE_CLASS_INFO
            = new ConcurrentReferenceMap<Class<?>, ClassInfo> (
            ReferenceKeyType.WEAK, ReferenceValueType.SOFT, 1024);

    /**
     * 清除该工具缓存的类信息。
     */
    public static final void clearCache () {
        synchronized (CACHE_CLASS_INFO) {
            CACHE_CLASS_INFO.clear ();
        }
    }

    public static ClassInfo getClassInfo (Class<?> clazz) {

        ClassInfo classInfo = CACHE_CLASS_INFO.get (clazz);
        if (classInfo == null) {
            synchronized (clazz) {
                classInfo = ClassInfo.analyze (clazz);
                CACHE_CLASS_INFO.put (clazz, classInfo);
            }
        }
        return classInfo;
    }
}
