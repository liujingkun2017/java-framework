
package org.liujk.java.framework.base.utils.lang;




import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 说明：
 * <p>
 * IP 白名单判断工具类
 *
 */
public class IPWhiteListUtils {
    private static volatile Map<String, Set<String>> ALLOWABLE_IP_LIST_CACHE = Maps.newConcurrentMap ();

    /**
     * IP的正则
     */
    private static Pattern pattern = Pattern
            .compile ("(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\."
                              + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\."
                              + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})\\."
                              + "(1\\d{1,2}|2[0-4]\\d|25[0-5]|\\d{1,2})");

    /**
     * 根据IP白名单设置获取可用的IP列表
     *
     * @param allowIp
     *
     * @return
     */
    private static Set<String> getAllowableIpList (String allowIp) {
        Set<String> ipList = ALLOWABLE_IP_LIST_CACHE.get (allowIp);
        if (ipList == null) {
            ipList = Sets.newHashSet ();
            for (String allow : StringUtils.removeBlankChar (allowIp).split (";+")) {
                if (allow.indexOf ("*") > -1) {
                    String[] ips = allow.split ("\\.");
                    String[] from = new String[]{"0", "0", "0", "0"};
                    String[] end = new String[]{"255", "255", "255", "255"};
                    List<String> tem = new ArrayList<String> ();
                    for (int i = 0; i < ips.length; i++) {
                        if (ips[i].indexOf ("*") > -1) {
                            tem = complete (ips[i]);
                            from[i] = null;
                            end[i] = null;
                        } else {
                            from[i] = ips[i];
                            end[i] = ips[i];
                        }
                    }

                    StringBuffer fromIP = new StringBuffer ();
                    StringBuffer endIP = new StringBuffer ();
                    for (int i = 0; i < 4; i++) {
                        if (from[i] != null) {
                            fromIP.append (from[i]).append (".");
                            endIP.append (end[i]).append (".");
                        } else {
                            fromIP.append ("[*].");
                            endIP.append ("[*].");
                        }
                    }
                    fromIP.deleteCharAt (fromIP.length () - 1);
                    endIP.deleteCharAt (endIP.length () - 1);

                    for (String s : tem) {
                        String ip = fromIP.toString ().replace ("[*]",
                                                                s.split (";")[0])
                                + "-"
                                + endIP.toString ().replace ("[*]", s.split (";")[1]);
                        if (validate (ip)) {
                            ipList.add (ip);
                        }
                    }
                } else {
                    if (validate (allow)) {
                        ipList.add (allow);
                    }
                }
            }
            ALLOWABLE_IP_LIST_CACHE.put (allowIp, ipList);
        }
        return ipList;
    }

    /**
     * 对单个IP节点进行范围限定
     *
     * @param arg
     *
     * @return 返回限定后的IP范围，格式为List[10;19, 100;199]
     */
    private static List<String> complete (String arg) {
        List<String> com = new ArrayList<String> ();
        if (arg.length () == 1) {
            com.add ("0;255");
        } else if (arg.length () == 2) {
            String s1 = complete (arg, 1);
            if (s1 != null) {
                com.add (s1);
            }
            String s2 = complete (arg, 2);
            if (s2 != null) {
                com.add (s2);
            }
        } else {
            String s1 = complete (arg, 1);
            if (s1 != null) {
                com.add (s1);
            }
        }
        return com;
    }

    private static String complete (String arg, int length) {
        String from = "";
        String end = "";
        if (length == 1) {
            from = arg.replace ("*", "0");
            end = arg.replace ("*", "9");
        } else {
            from = arg.replace ("*", "00");
            end = arg.replace ("*", "99");
        }
        if (Integer.valueOf (from) > 255) {
            return null;
        }
        if (Integer.valueOf (end) > 255) {
            end = "255";
        }
        return from + ";" + end;
    }

    /**
     * 在添加至白名单时进行格式校验
     *
     * @param ip
     *
     * @return
     */
    private static boolean validate (String ip) {
        for (String s : ip.split ("-")) {
            if (!pattern.matcher (s).matches ()) {
                return false;
            }
        }
        return true;
    }

    /**
     * ip是否包含在白名单列表之中
     *
     * @param ip
     * @param ipList
     *
     * @return
     */
    private static boolean isAllowableIp (String ip, Set<String> ipList) {
        if (ipList.isEmpty () || ipList.contains (ip)) {
            return true;
        } else {
            for (String allow : ipList) {
                if (allow.indexOf ("-") > -1) {
                    String[] from = allow.split ("-")[0].split ("\\.");
                    String[] end = allow.split ("-")[1].split ("\\.");
                    String[] tag = ip.split ("\\.");

                    // 对IP从左到右进行逐段匹配
                    boolean check = true;
                    for (int i = 0; i < 4; i++) {
                        int s = Integer.valueOf (from[i]);
                        int t = Integer.valueOf (tag[i]);
                        int e = Integer.valueOf (end[i]);
                        if (!(s <= t && t <= e)) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断IP是否包含在白名单规则之中
     * <p>
     * 白名单规则：
     * "192.168.1.1;" +                 //设置单个IP的白名单
     * "192.168.2.*;" +                 //设置ip通配符,对一个ip段进行匹配
     * "192.168.3.17-192.168.3.38";     //设置一个IP范围
     *
     *
     * @param ip
     * @param ipWhiteConfig
     *
     * @return
     */
    public static boolean isAllowableIp (String ip, String ipWhiteConfig) {
        Set<String> ipList = getAllowableIpList (ipWhiteConfig);
        return isAllowableIp (ip, ipList);
    }
}
