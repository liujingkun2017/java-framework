
package org.liujk.java.framework.base.utils.id;


import org.liujk.java.framework.base.utils.lang.DateUtils;
import org.liujk.java.framework.base.utils.lang.StringUtils;

import java.util.Date;
import java.util.Random;

/**
 * 说明：
 * <p>
 *
 */
public class GID {
    public static final String DATE_FORMAT_STRING = "yyyyMMddHHmmssSSS";

    private static final String DEFAULT_RESERVED = "0000";
    /* 3位32进制 */
    private static final long MAX_INDEX = 32767;
    private static Random rand = new Random ();
    private static String $NODE_CODE;
    private static long index = 1;
    private static String systemCode;
    private static String $RESERVED_CODE = DEFAULT_RESERVED;

    static {
        systemCode = CodeGenerator.genSystemCode ();
        $NODE_CODE = CodeGenerator.padding (CodeGenerator.genNodeCode (), 3,
                                            CodeGenerator.PADDING_CHAR);
    }

    /**
     * 生成全局唯一ID
     *
     * @return
     */
    public static String newGid () {
       return newGid (false);
    }

    public static String newGid (boolean sign) {
        Date now = DateUtils.now ();
        String index = systemCode + DateUtils.formatDate (now, DATE_FORMAT_STRING) + $NODE_CODE + $RESERVED_CODE
                + getIndex () + CodeGenerator.getRandomStr (sign ? 3 : 4);
        return sign ? sign (index) : index;
    }

    private static boolean checkSign (String index) {
        String sign = Integer.toString (index.substring (0, 34).hashCode (), 32);
        return StringUtils.right (index, 1).toUpperCase ()
                .equalsIgnoreCase (StringUtils.right (sign, 1));
    }

    /**
     * 增加校验位
     *
     * @param index
     *
     * @return
     */
    private static String sign (String index) {
        String sign = Integer.toString (index.hashCode (), 32);
        return index + StringUtils.right (sign, 1).toUpperCase ();
    }

    public static void setReservedCode (String reservedCode) {
        $RESERVED_CODE = StringUtils.right (StringUtils.lpad (reservedCode, 6, '0'), 6).toLowerCase ();

    }

    /**
     * 序号
     *
     * @return
     */
    private static String getIndex () {
        String idx;
        synchronized ($NODE_CODE) {
            if (index >= MAX_INDEX) {
                index = 0;
            }
            idx = Long.toString (index++, 32);
        }
        if (idx.length () == 1) {
            return "00" + idx.toLowerCase ();
        } else if (idx.length () == 2) {
            return "0" + idx.toLowerCase ();
        } else {
            return idx.toLowerCase ();
        }
    }
}
