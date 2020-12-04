
package org.liujk.java.framework.base.enums;

/*
 *
 */
public enum WeekDay implements CodeMessageable {
    /**
     * 星期一
     */
    MONDAY("MONDAY", "星期一"),

    /**
     * 星期二
     */
    TUESDAY("TUESDAY", "星期二"),

    /**
     * 星期三
     */
    WEDNESDAY("WEDNESDAY", "星期三"),

    /**
     * 星期四
     */
    THURSDAY("THURSDAY", "星期四"),

    /**
     * 星期五
     */
    FRIDAY("FRIDAY", "星期五"),

    /**
     * 星期六
     */
    SATURDAY("SATURDAY", "星期六"),

    /**
     * 星期天
     */
    SUNDAY("SUNDAY", "星期天");

    /**
     * 枚举值
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String message;

    /**
     * 构造一个<code>WeekDay</code>枚举对象
     *
     * @param code
     * @param message
     */
    private WeekDay(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过枚举<code>code</code>获得枚举
     *
     * @param code
     * @return WeekDay.java
     */
    public static WeekDay getByCode(String code) {
        for (WeekDay _enum : values()) {
            if (_enum.getCode().equalsIgnoreCase(code)) {
                return _enum;
            }
        }
        return null;
    }

    /**
     * 获取全部枚举
     *
     * @return List<LocalCacheEnum>
     */
    public static java.util.List<WeekDay> getAllEnum() {
        java.util.List<WeekDay> list = new java.util.ArrayList<WeekDay>();
        for (WeekDay _enum : values()) {
            list.add(_enum);
        }
        return list;
    }

    /**
     * 获取全部枚举值
     *
     * @return List<String>
     */
    public static java.util.List<String> getAllEnumCode() {
        java.util.List<String> list = new java.util.ArrayList<String>();
        for (WeekDay _enum : values()) {
            list.add(_enum.getCode());
        }
        return list;
    }

    /**
     * @return Returns the code.
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * @return Returns the message.
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 判断给定的枚举，是否在列表中
     *
     * @param values 列表
     * @return
     */
    public boolean isInList(WeekDay... values) {
        for (WeekDay e : values) {
            if (this == e) {
                return true;
            }
        }
        return false;
    }
}
