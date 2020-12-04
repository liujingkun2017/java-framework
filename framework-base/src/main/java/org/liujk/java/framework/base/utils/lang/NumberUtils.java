
package org.liujk.java.framework.base.utils.lang;




import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * 说明：
 * <p>
 * 数字工具类
 *
 */
public class NumberUtils {
    private static final String DOUBLE_REGEX = "(-)?(([0-9]+(\\.[0-9]*)?)|(\\.[0-9]+))((e|E)[0-9]+)?";
    private static final String INTEGER_REGEX = "(-)?\\d+(.0+)?";
    public static final String DOMAIN_REGEX = "(\\[|\\()" + DOUBLE_REGEX + "\\," + DOUBLE_REGEX + "(\\]|\\))";


    /**
     * 判断是否为数字
     *
     * @param
     * @return
     */
    public static boolean isNumeric(String s) {
        return isDouble(s);
    }

    /**
     * 判断是否为
     *
     * @param
     * @return
     */
    public static boolean isInt(String s) {
        if (s == null || "".equals(s)) {
            return false;
        }
        return StringUtils.isMatches(s, INTEGER_REGEX);
    }

    /**
     * 判断是否为
     *
     * @param
     * @return
     */
    public static boolean isDouble(String s) {
        if (s == null || StringUtils.EMPTY_STRING.equals(s)) {
            return false;
        }
        return StringUtils.isMatches(s, DOUBLE_REGEX);
    }


    /**
     * @param s
     * @return
     * @name toInteger
     * @date 2011-7-7
     * @description 将字符串转为整型
     */
    public static int toInt(String s) {
        s = s.replaceAll("\\.0*$", "");
        if (isInt(s)) {
            return Integer.parseInt(s);
        } else {
            return 0;
        }
    }

    /**
     * 生成 从 0 - size-1 的数组
     *
     * @param len 数组长度
     * @return
     */
    public static int[] range(int len) {
        Assert.isTrue(len > 0, "len[数组长度]应大于0");
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = i;
        }
        return arr;
    }

    /**
     * 生成 from - to 的数组
     *
     * @param start 数组起始值
     * @param len   数组长度
     * @return
     */
    public static int[] range(int start, int len) {
        return range(start, len, 1);
    }

    /**
     * 生成 from 指定步进的数组
     *
     * @param start 数组起始值
     * @param len   数组长度
     * @param step
     * @return
     */
    public static int[] range(int start, int len, int step) {
        Assert.isTrue(len > 0, "len[数组长度]应大于0");


        int[] arr;
        if (step == 0) {
            // 步进为0，就以from为填充
            arr = new int[len];
            Arrays.fill(arr, start);
            return arr;
        }

        arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = start + (step * i);
        }
        return arr;
    }
}
