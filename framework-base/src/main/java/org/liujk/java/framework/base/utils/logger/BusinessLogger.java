
package org.liujk.java.framework.base.utils.logger;


import com.google.common.collect.Maps;
import org.liujk.java.framework.base.api.SerializableObject;
import org.liujk.java.framework.base.env.Env;
import org.liujk.java.framework.base.utils.lang.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 说明：
 * <p>
 * 业务监控日志logger
 *
 */
public class BusinessLogger {
    private static final Logger logger = LoggerFactory.getLogger (BusinessLogger.class);

    private static final String APP_NAME;
    private static final String IP;
    private static final String NULL = "null";

    static {
        APP_NAME = System.getProperty ("pmo.appName");
        Assert.notNull (APP_NAME, "应用名不能为空");
        IP = IPUtils.getFirstNoLoopbackIPV4Address ();
    }

    /**
     * 记录业务日志
     *
     * @param businessName 业务名称
     * @param bodys        业务数据
     */
    public static void log (String businessName, Object... bodys) {
        BusinessDO businessDO = new BusinessDO ();
        businessDO.setLogType (businessName);
        Assert.notNull (bodys, "业务内容不能为空");
        Assert.isTrue (bodys.length % 2 == 0, "业务信息为键值对形式");
        Map<String, Object> body = Maps.newHashMapWithExpectedSize (bodys.length / 2);
        for (int i = 0; i < bodys.length - 1; ) {
            String key;
            if (bodys[i] == null) {
                key = NULL;
            } else {
                key = bodys[i].toString ();
            }
            body.put (key, bodys[i + 1]);
            i = i + 2;
        }
        businessDO.body = body;
        logger.info (businessDO.toJsonString ());
    }

    /**
     * 记录业务日志
     *
     * @param businessDO 业务日志对象
     */
    public static void log (BusinessDO businessDO) {
        logger.info (businessDO.toJsonString ());
    }

    /**
     * 业务日志对象
     */
    public static class BusinessDO extends SerializableObject {
        /**
         * 应用名称
         */
        private String appName = BusinessLogger.APP_NAME;
        /**
         * 业务名称
         */
        private String logType;
        /**
         * 环境名称
         */
        private String env = Env.getEnv ();

        private String hostName = IP;
        /**
         * 生成时间
         */
        private long timestamp = System.currentTimeMillis ();
        /**
         * 自定义业务参数
         */
        private Map<String, Object> body;

        public String getAppName () {
            return appName;
        }

        public Map<String, Object> getBody () {
            return body;
        }

        public String getEnv () {
            return env;
        }

        public String getLogType () {
            return logType;
        }

        public void setLogType (String logType) {
            this.logType = logType;
        }

        public long getTimestamp () {
            return timestamp;
        }

        public String getHostName () {
            return hostName;
        }

        /**
         * 添加业务内容
         *
         * @param key   key
         * @param value value
         */
        public BusinessDO addContent (String key, Object value) {
            if (body == null) {
                body = Maps.newHashMap ();
            }
            if (key == null) {
                key = NULL;
            }
            body.put (key, value);
            return this;
        }

        @Override
        public String toJsonString () {
            Assert.notNull (logType, "业务名称不能为空");
            return super.toJsonString ();
        }

    }
}
