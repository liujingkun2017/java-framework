package org.liujk.java.framework.base.utils.id;

public class GidHolder {
    private static ThreadLocal<String> gidThreadLocal = new ThreadLocal<> ();

    /**
     * 当前线程内的GID
     *
     * @return
     */
    public static String getGid () {
        return gidThreadLocal.get ();
    }

    /**
     * 保存GID
     *
     * @param gid
     */
    public static void setGid (String gid) {
        gidThreadLocal.set (gid);
    }

    /**
     * 如果当前线程未使用gid，那么创建gid，如果存在就使用存在的
     *
     * @return
     */
    public synchronized static String requestGid () {
        String gid = gidThreadLocal.get ();
        if (gid == null) {
            gid = GID.newGid ();
            gidThreadLocal.set (gid);
        }
        return gid;
    }

    /**
     * 创建新的gid，并保存到缓存线程变量中
     *
     * @return
     */
    public static String newGid () {
        String gid = GID.newGid ();
        gidThreadLocal.set (gid);
        return gid;
    }

}
