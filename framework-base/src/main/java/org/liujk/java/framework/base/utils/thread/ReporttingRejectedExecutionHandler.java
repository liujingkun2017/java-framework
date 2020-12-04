
package org.liujk.java.framework.base.utils.thread;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.liujk.java.framework.base.enums.CodeMessageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 说明：
 * <p>
 * 提供监控报警日志输出的RejectedExecutionHandler
 *
 */
public class ReporttingRejectedExecutionHandler implements RejectedExecutionHandler {

    private static final Logger logger = LoggerFactory
            .getLogger (ReporttingRejectedExecutionHandler.class);
    /**
     * 日志收集logger
     */
    private static final Logger threadpoolLogger = LoggerFactory
            .getLogger ("THREADPOOL_LOG");
    public static ReporttingRejectedExecutionHandler DEFAULT = new ReporttingRejectedExecutionHandler (
            "default");
    private final String threadPrefix;
    private final Policy policy;

    /**
     * @param threadPrefix 线程前缀
     */
    public ReporttingRejectedExecutionHandler (String threadPrefix) {
        this (threadPrefix, Policy.callerRun);
    }

    /**
     * @param policy 拒绝策略
     */
    public ReporttingRejectedExecutionHandler (Policy policy) {
        this (policy.getCode (), policy);
    }

    public ReporttingRejectedExecutionHandler (String threadPrefix, Policy policy) {
        Assert.notNull (policy, "必须制定拒绝策略");
        Assert.notNull (threadPrefix, "必须制定线程前缀");
        this.policy = policy;
        this.threadPrefix = threadPrefix;
    }

    @Override
    public void rejectedExecution (Runnable r, ThreadPoolExecutor e) {
        BlockingQueue<Runnable> queue = e.getQueue ();
        if (queue instanceof MonitoredThreadPoolQueue) {
            // 如果队列可插入任务，插入当前任务
            if (((MonitoredThreadPoolQueue) queue).force (r)) {
                return;
            }
        }
        int queuedTask = e.getQueue ().size ();
        ThreadInfo threadInfo = new ThreadInfo ();
        threadInfo.setLogType ("threadPool");
        threadInfo.addContent ("threadPrefix", threadPrefix);
        threadInfo.addContent ("queuedTask", queuedTask);
        if (e instanceof MonitoredThreadPoolExecutor) {
            threadInfo.addContent ("submittedCount",
                                   ((MonitoredThreadPoolExecutor) e).getSubmittedCount ());
        }
        String msg = threadInfo.toJSONString ();
        logger.error ("线程池[{}]满了,{},处理策略:{}({})", threadPrefix, msg,
                      policy.getCode (), policy.getMessage ());
        threadpoolLogger.info ("{}", msg);
        policy.getRejectedExecutionHandler ().rejectedExecution (r, e);
    }

    public String getThreadPrefix () {
        return threadPrefix;
    }

    public static enum Policy implements CodeMessageable {
        /**
         * 直接调用runner
         */
        callerRun (new ThreadPoolExecutor.CallerRunsPolicy (), "CallerRuns", "直接在当前线程中调用方法"),

        abortPolicy (new ThreadPoolExecutor.AbortPolicy (), "Abort", "终止并抛出异常"),

        /**
         * 直接丢弃
         */
        discard (new ThreadPoolExecutor.DiscardPolicy (), "Discard", "直接丢弃当前线程方法"),

        /**
         * 丢弃老的
         */
        discardOldest (new ThreadPoolExecutor.DiscardOldestPolicy (), "DiscardOldest", "丢弃最老的线程");


        private final RejectedExecutionHandler rejectedExecutionHandler;
        /**
         * code
         */
        private final String code;
        /**
         * 描述信息
         */
        private final String message;

        Policy (RejectedExecutionHandler rejectedExecutionHandler, String code, String messgae) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            this.code = code;
            this.message = messgae;
        }

        public RejectedExecutionHandler getRejectedExecutionHandler () {
            return rejectedExecutionHandler;
        }


        @Override
        public String getCode () {
            return code;
        }

        @Override
        public String getMessage () {
            return message;
        }
    }

    public static class ThreadInfo {
        String logType;
        Map<String, Object> contentMap;

        public void addContent (String key, Object content) {
            synchronized (this) {
                if (contentMap == null) {
                    this.contentMap = Maps.newHashMap ();
                }
            }
            this.contentMap.put (key, content);
        }

        public String getLogType () {
            return logType;
        }

        public void setLogType (String logType) {
            this.logType = logType;
        }

        public Map<String, Object> getContentMap () {
            return contentMap;
        }

        public void setContentMap (Map<String, Object> contentMap) {
            this.contentMap = contentMap;
        }

        public String toJSONString () {
            return JSON.toJSONString (this);
        }
    }
}
