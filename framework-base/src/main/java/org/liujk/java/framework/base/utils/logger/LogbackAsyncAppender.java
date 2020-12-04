
package org.liujk.java.framework.base.utils.logger;



import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 说明：
 * <p>
 * 适用于cqfae内部使用的异步日志appender<br/>
 * 当日志处理队列到达discardingThreshold时<br/>
 * 1.丢弃掉 所有TRACE, DEBUG日志<br/>
 * 2.保留level大于info的日志<br/>
 * 3.保留com.cqfae.pmo.base.utils.logger.Logger 下info的日志<br/>
 * <b>注意:</b><br/>
 * discardingThreshold=0，表示不丢弃日志 当队列满了后，会阻塞调用日志的线程
 *
 */
public class LogbackAsyncAppender extends AsyncAppender {

    /**
     * logger前缀名，下面这个前缀匹配的logger会做丢弃判断
     */
    private static final String LOGGER_NAME = "com.cqfae";

    public LogbackAsyncAppender () {
        super ();
        // 设置queue大小为1024
        this.setQueueSize (1024);
        // 如果队列剩余空间小于这个值后，就开始做丢弃判断
        this.setDiscardingThreshold (100);
        // 开启方法栈支持,配置文件中可以覆盖此配置.
        this.setIncludeCallerData (true);
    }

    /**
     * 当日志处理队列到达discardingThreshold时 1.丢弃掉 所有TRACE, DEBUG日志 2.保留level大于info的日志 3.保留com.cqfae
     * logger下info的日志
     *
     * @param event 日志事件对象
     *
     * @return 是否抛弃
     * @see AsyncAppender#isDiscardable(ILoggingEvent)
     */
    @Override
    protected boolean isDiscardable (ILoggingEvent event) {
        Level level = event.getLevel ();
        if (level.toInt () < Level.INFO_INT) {
            return true;
        } else if (level.toInt () > Level.INFO_INT) {
            return false;
        } else {
            return event.getLoggerName () == null
                    || !(event.getLoggerName ().startsWith (LOGGER_NAME));
        }
    }

}
