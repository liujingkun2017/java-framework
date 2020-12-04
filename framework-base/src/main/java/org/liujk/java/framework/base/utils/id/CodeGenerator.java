
package org.liujk.java.framework.base.utils.id;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.liujk.java.framework.base.env.Env;
import org.liujk.java.framework.base.exceptions.AppException;
import org.liujk.java.framework.base.utils.lang.IPUtils;
import org.liujk.java.framework.base.utils.lang.RandomStringUtils;
import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.base.utils.security.MD5Utils;
import org.liujk.java.framework.base.utils.thread.ShutdownHooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CodeGenerator {
    public static final int REGISTER_TRY_TIME = 3;
    /**
     * 系统编码长度
     */
    public static final int SYSTEM_CODE_LEN = 4;
    /**
     * 节点编码长度
     */
    public static final int NODE_CODE_LEN = 3;
    /**
     * 时间戳
     */
    public static final int TIMESTAMP_LEN = 12;
    /**
     * 保留域长度
     */
    public static final int RESERVED_LEN = 9;
    /**
     * 顺序号长度
     */
    public static final int SEQUENCE_LEN = 4;
    /**
     * id长度
     */
    public static final int GID_LEN = SYSTEM_CODE_LEN + NODE_CODE_LEN
            + RESERVED_LEN + TIMESTAMP_LEN + SEQUENCE_LEN;
    public static final char PADDING_CHAR = '0';
    private static final Logger logger = LoggerFactory
            .getLogger (CodeGenerator.class);
    /**
     * 默认物理地址，如果获取mac失败，用此物理地址
     */
    private static final String DEFAULT_MAC = "00:00:00:00:00:00";
    private final static char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6',
                                          '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                                          'm', 'n',
                                          'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',};

    private static final char[] INDEX_CHARS = new char[]{'0', '1', '2', '3', '4',
                                                         '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                                                         'h', 'i', 'j', 'k', 'l',
                                                         'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                                                         'y', 'z'};
    private final static int RADIX = DIGITS.length;
    /**
     * 12位的时间格式
     */
    private static final String TIME_FORMAT = "yyMMddHHmmss";
    private static final String DEFAULT_RESERVED = "000000000";
    /**
     * 每秒内生成id最大数
     */
    private static final int COUNT_IN_SECOND = 10000000;
    /**
     * 从配置中心服务器注册服务时的安全码
     */
    private static final String SECURITY_KEY = "pmo-sid-apollo";
    /**
     * 时间缓存
     */
    private static volatile long lastTimeCache = 0L;
    /**
     * 时间字符串缓存
     */
    private static volatile String lastTimeStrCache = null;
    private static AtomicLong lastTimeWithCount = new AtomicLong (0);
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat
            .forPattern (TIME_FORMAT);
    private static String ccUrl;
    private static String configCenterRegisterUrl = getApolloRegisterUrl ();
    private static String systemCode;
    private static String nodeCode;
    private static Random rand = new Random ();

    static {
        ShutdownHooks.addShutdownHook (new Runnable () {
            @Override
            public void run () {
                CodeGenerator.unregisterNode ();
            }
        }, "取消配置中心 服务节点注册");
    }

    public static void setConfigCenterRegisterUrl (String registerUrl) {
        configCenterRegisterUrl = registerUrl;
    }

    /**
     * 获取当前应用系统编码,长度4位
     */
    public static String getSystemCode () {
        return genSystemCode ();
    }

    /**
     * 获取当前应用节点编码,长度3位
     */
    public static String getNodeCode () {
        return genNodeCode ();
    }

    public static String getReservedCode () {
        return DEFAULT_RESERVED;
    }

    public static String getTimeStrCode (DateTime now) {
        String timeStr;
        if (lastTimeCache == now.getMillis ()) {
            timeStr = lastTimeStrCache;
        } else {
            timeStr = now.toString (dateTimeFormatter);
            lastTimeStrCache = timeStr;
            lastTimeCache = now.getMillis ();
        }
        return timeStr;
    }

    public static String getSequenceNoCode (DateTime now) {
        long timeWithCount = (now.getMillis () / 1000) * COUNT_IN_SECOND;
        while (true) {
            long last = lastTimeWithCount.get ();
            if (timeWithCount > last) {
                if (lastTimeWithCount.compareAndSet (last, timeWithCount)) {
                    break;
                }
            } else {
                if (lastTimeWithCount.compareAndSet (last, last + 1)) {
                    timeWithCount = last + 1;
                    break;
                }
            }
        }
        int seqNo = (int) (timeWithCount % COUNT_IN_SECOND);
        String seqNoCode = padding (toString (seqNo), SEQUENCE_LEN, PADDING_CHAR);
        return seqNoCode;
    }

    public static String genSystemCode () {
        if (systemCode != null) {
            return systemCode;
        } else {
            synchronized (CodeGenerator.class) {
                if (systemCode == null) {
                    systemCode = getSystemCodeFormConfigCenter ();
                }
            }
        }
        return systemCode;
    }

    public static String genNodeCode () {
        if (nodeCode != null) {
            return nodeCode;
        } else {
            synchronized (CodeGenerator.class) {
                if (nodeCode == null) {
                    nodeCode = getNodeCodeFormConfigCenter ();
                }
            }
        }
        return nodeCode;
    }

    public static DateTime parseTime (String timeStamp) {
        return DateTime.parse (timeStamp, dateTimeFormatter);
    }

    /**
     * 随机字符串
     *
     * @param len
     *
     * @return
     */
    public static String getRandomStr (int len) {
        return RandomStringUtils.random (len, INDEX_CHARS);
    }

    private static String getSystemCodeFormConfigCenter () {
        Map<String, Object> dataMap = new HashMap<> ();
        String result =  "0000";
        try {
            String ip = IPUtils.getFirstNoLoopbackIPV4Address ();
            ip = ip.substring (ip.lastIndexOf ('.')+1);
            result =StringUtils.right (result+ip,4);
        } catch (Exception e) {
        }

        //TODO 配置中心还未完成
        //        try {
        //            result = getDataFromConfigCenter("project/getSystemCode.json", dataMap);
        //            if (result == null || result.length() != SYSTEM_CODE_LEN) {
        //                throw Exceptions.newRuntimeException(
        //                        "从配置中心读取SystemCode长度不对,请联系配置中心开发者,systemCode=" + result);
        //            }
        //            logger.info("请求配置中心成功, 获取到system code:{}", result);
        //        } catch (RuntimeException e) {
        //            if (isTestEnv()) {
        //                result = "0000";
        //                logger.info("测试环境获取系统码失败,使用默认系统码:{}", result);
        //            } else if ("false".equalsIgnoreCase(System.getProperty("pmo.apollo.enable"))) {
        //                // 未启用配置中心
        //                String appName = padding(System.getProperty("pmo.appName"), 4, PADDING_CHAR);
        //                result = StringUtils.right(appName, 4).toLowerCase();
        //                logger.info("未启用配置中心,使用默认系统码:{}", result);
        //
        //            } else {
        //                logger.error("获取系统码失败,系统关闭", e);
        //                System.exit(0);
        //            }
        //        }
        return result;
    }

    private static String getNodeCodeFormConfigCenter () {
        Map<String, Object> dataMap = new HashMap<> ();
        String ip = IPUtils.getFirstNoLoopbackIPV4Address ();
        String port = Env.getPort ();
        String ipAndPort = ip + ":" + port;
        dataMap.put ("ipAndPort", ipAndPort);
        String result = null;

        //TODO 配置中心还未完成
        result = getRandomStr (NODE_CODE_LEN);

        //        try {
        //            result = getDataFromConfigCenter("project/getNodeCode.json", dataMap);
        //            if (result == null || result.length() != NODE_CODE_LEN) {
        //                throw Exceptions
        //                        .newRuntimeException("从Apollo读取NodeCode长度不对,请联系配置中心开发者,NodeCode=" + result);
        //            }
        //            logger.info("请求Apollo成功, 获取到node code:{}", result);
        //        } catch (RuntimeException e) {
        //            if (isTestEnv()) {
        //                result = getRandomStr(NODE_CODE_LEN);
        //                logger.info("测试环境获取节点码失败,使用随机节点码:{}", result);
        //            } else if ("false".equalsIgnoreCase(System.getProperty("pmo.apollo.enable"))) {
        //                // 未启用配置中心
        //                if (StringUtils.isBlank(ip)) {
        //                    result = getServerID(NODE_CODE_LEN);
        //                } else {
        //                    result = padding(ip.substring(ip.lastIndexOf('.') + 1), NODE_CODE_LEN,
        //                            PADDING_CHAR);
        //                }
        //                logger.info("未启用Apollo,使用默认系统码:{}", result);
        //            } else {
        //                logger.error("获取节点码失败,系统关闭", e);
        ////                System.exit(0);
        //            }
        //        }
        return result;
    }

    public static void unregisterNode () {
        Map<String, Object> dataMap = new HashMap<> ();
        String ip = IPUtils.getFirstNoLoopbackIPV4Address ();
        String port = Env.getPort ();
        String ipAndPort = ip + ":" + port;
        dataMap.put ("ipAndPort", ipAndPort);

        //TODO 配置中心还未完成

        //        try {
        //            String result = getDataFromConfigCenter("project/delNodeCode.json", dataMap);
        //            logger.info("ID服务取消注册返回结果:{}", result);
        //        } catch (Exception e) {
        //            logger.warn("ID服务取消注册失败");
        //        }
    }

    private static String getDataFromConfigCenter (String uri, Map<String, Object> dataMap) {
        // String projectCode = System.getProperty ("pmo.appName");
        // if (StringUtils.isEmpty (projectCode)) {
        // projectCode = System.getProperty ("sys.name");
        // }
        // if (StringUtils.isEmpty (projectCode)) {
        // throw Exceptions.newRuntimeException ("系统变量中没有发现配置系统名:pmo.appName或sys.name");
        // }
        // int retry = REGISTER_TRY_TIME;
        // if (isTestEnv ()) {
        // retry = 1;
        // }
        // for (int i = 0; i < retry; i++) {
        // try {
        // dataMap.put ("projectCode", projectCode);
        // dataMap
        // .put (DigestUtil.SIGN_KEY,
        // DigestUtil.digest (dataMap, SECURITY_KEY, DigestUtil.DigestALGEnum.MD5));
        // String url = configCenterRegisterUrl + uri;
        // logger.info ("注册配置中心，请求数据,url={}", url);
        // int timeout = 5000;
        // if (isTestEnv ()) {
        // timeout = 2000;
        // }
        // String data = HttpRequest.post (url, dataMap, false).readTimeout (timeout).connectTimeout
        // (timeout)
        // .body ();
        // logger.info ("注册配置中心,返回数据:{}", data);
        // JSONObject resultObject = JSON.parseObject (data);
        // if (resultObject.getString ("success").equals ("true")) {
        // String result = resultObject.getString ("singleResult");
        // logger.info ("请求配置中心成功, 获取到数据:{}", result);
        // return result;
        // } else {
        // throw new AppException ("配置中心注册服务返回失败:" + resultObject.getString ("message"));
        // }
        // } catch (Exception e) {
        // logger.error ("请求配置中心注册失败:{}, 将进行第{}次重试", e.getMessage (), i);
        // }
        // try {
        // Thread.sleep (500);
        // } catch (InterruptedException e) {
        // logger.warn (e.getMessage ());
        // }
        // }
        throw new AppException ("从配置中心获取返回失败");
    }

    /**
     * 填充字符串
     *
     * @param str     待填充字符串
     * @param len     填充后的位数
     * @param padding 填充字符
     */
    public static String padding (String str, int len, char padding) {
        if (str.length () < len) {
            StringBuilder sb = new StringBuilder (len);
            int toPadLen = len - str.length ();
            for (int i = 1; i <= toPadLen; i++) {
                sb.append (padding);
            }
            sb.append (str);
            return sb.toString ();
        } else {
            return str;
        }
    }

    private static String toString (long i) {
        char[] buf = new char[65];
        int charPos = 64;
        boolean negative = (i < 0);

        if (!negative) {
            i = -i;
        }

        while (i <= -RADIX) {
            buf[charPos--] = DIGITS[(int) (-(i % RADIX))];
            i = i / RADIX;
        }
        buf[charPos] = DIGITS[(int) (-i)];

        if (negative) {
            buf[--charPos] = '-';
        }
        return new String (buf, charPos, (65 - charPos));
    }

    public static String getApolloRegisterUrl () {
        if (ccUrl != null) {
            return ccUrl;
        }
        String url = System.getProperty ("pmo.apollo.urls", null);
        if (StringUtils.isNotBlank (url)) {
            String[] urls = url.split (",");
            if (urls.length > 0) {
                url = urls[0];
            }

            logger.info ("使用系统参数[pmo.apollo.urls]配置的配置中心地址:" + url);
        }
        ccUrl = url;
        return url;
    }

    private static boolean isTestEnv () {
        return !Env.isOnline () && !Env.isPre ();
    }

    private static String getServerID (int len) {
        // 随机生成长度为5的serverId
        return StringUtils.right (MD5Utils.encode (getMAC () + rand.nextInt (100000) + getIp ())
                                          .substring (5, 12).toUpperCase (), len);
    }

    private static String getMAC () {
        String mac;
        try {
            mac = IPUtils.getMACAddress ();
            mac = mac.replace ("-", ":");
        } catch (Exception e) {
            mac = DEFAULT_MAC;
            // logger.info("获取mac失败，使用默认物理地址:{}", mac);
        }
        return mac;
    }

    private static String getIp () {
        return IPUtils.getFirstNoLoopbackIPV4Address ();
    }
}
