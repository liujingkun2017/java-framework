
package org.liujk.java.framework.base.utils.thread;


/**
 * 说明：
 * <p>
 *
 */
public class NoDiscardRejectedExecutionHandler extends ReporttingRejectedExecutionHandler {

    private ReporttingRejectedExecutionHandler reporttingRejectedExecutionHandler;

    public NoDiscardRejectedExecutionHandler () {
        this ("NoDiscard");
    }

    public NoDiscardRejectedExecutionHandler (String threadPrefix) {
        super (threadPrefix, Policy.callerRun);
    }
}