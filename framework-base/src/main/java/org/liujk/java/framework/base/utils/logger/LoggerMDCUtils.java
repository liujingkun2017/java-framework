
package org.liujk.java.framework.base.utils.logger;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 说明：
 * <p>
 * 使用logback mdc 特性，打印交易号或者其他可以作为用户请求凭证的信息，便于日志分析
 *
 */
public class LoggerMDCUtils {
    /**
     * 使用logback mdc 特性，打印交易号或者其他可以作为用户请求凭证的信息，便于日志分析
     */
    public static final String LOG_KEY_SID = "sid";
    public static final String LOG_KEY_GID = "gid";
    private static Logger logger = LoggerFactory.getLogger (LoggerMDCUtils.class);

    public static void clear () {
        MDC.clear ();
    }

    public static void setLogKey (String gid, String sid) {
        clear ();
        MDC.put (LOG_KEY_GID, gid);
        MDC.put (LOG_KEY_SID, sid);
    }

    public static String getGid () {
        return MDC.get (LOG_KEY_GID);
    }

    public static void setGid (String gid) {
        clear ();
        MDC.put (LOG_KEY_GID, gid);
    }

    public static String getSid () {
        return MDC.get (LOG_KEY_SID);
    }

    public static void setSid (String sid) {
        MDC.put (LOG_KEY_SID, sid);
    }

    public static String getLogKey () {
        return getGid () + ":" + getSid ();
    }


    @Deprecated
    public static void setLogKey (String logKey) {
        clear ();
        MDC.put (LOG_KEY_GID, logKey);
    }
}