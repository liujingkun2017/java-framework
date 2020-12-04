
package org.liujk.java.framework.base.utils.thread;




import java.util.concurrent.TimeUnit;

/**
 * 说明：
 * <p>
 *
 */
public class ThreadUtils {

    /**
     * 包装 Thread.sleep
     *
     * @param duration
     * @param unit
     */
    public static final void sleep (long duration, TimeUnit unit) {
        try {
            if (unit == TimeUnit.NANOSECONDS) {
                Thread.sleep (unit.toMillis (duration), (int) (unit.toNanos (duration) % (1000L * 1000L)));
            } else {
                Thread.sleep (unit.toMillis (duration));
            }
        } catch (InterruptedException e) {
        }
    }
}
