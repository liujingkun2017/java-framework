
package org.liujk.java.framework.base.utils.lang;


import java.util.Date;

/**
 * 说明：
 * <p>
 * 当前时间提供者
 *
 */
public interface NowProvider {

    /**
     * Returns the current time.
     *
     * @return
     */
    Date getNow();

    /**
     * @return
     */
    long currentTimeMillis();
}
