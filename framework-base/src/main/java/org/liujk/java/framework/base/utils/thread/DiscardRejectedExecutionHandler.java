
package org.liujk.java.framework.base.utils.thread;



/**
 * 说明：
 * <p>
 * 当线程池满后抛弃新线程
 *
 */
public class DiscardRejectedExecutionHandler extends ReporttingRejectedExecutionHandler {


    public DiscardRejectedExecutionHandler () {
        this ("Discard");
    }

    public DiscardRejectedExecutionHandler (String threadPrefix) {
        super (threadPrefix, Policy.discard);
    }

}