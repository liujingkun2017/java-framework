
package org.liujk.java.framework.base.enums;

/*
 *
 */
public enum Gender {
    /**
     * 男性
     */
    MALE("M", "男性"),

    /**
     * 女性
     */
    FEMALE("F", "女性"),

    /**
     * 未知
     */
    UNKNOWN("U", "未知");

    /**
     * 枚举值
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String message;

    /**
     * 构造一个<code>Gender</code>枚举对象
     *
     * @param code
     * @param message
     */
    private Gender(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过枚举<code>code</code>获得枚举
     *
     * @param code
     * @return Gender.java
     */
    public static Gender getByCode(String code) {
        for (Gender _enum : values()) {
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
    public static java.util.List<Gender> getAllEnum() {
        java.util.List<Gender> list = new java.util.ArrayList<Gender>();
        for (Gender _enum : values()) {
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
        for (Gender _enum : values()) {
            list.add(_enum.getCode());
        }
        return list;
    }

    /**
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * 判断给定的枚举，是否在列表中
     *
     * @param values 列表
     * @return
     */
    public boolean isInList(Gender... values) {
        for (Gender e : values) {
            if (this == e) {
                return true;
            }
        }
        return false;
    }
}
