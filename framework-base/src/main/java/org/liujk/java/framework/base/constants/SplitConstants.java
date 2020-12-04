package org.liujk.java.framework.base.constants;

/**
 * 分隔符常量
 */
public interface SplitConstants {

    String SEPARATOR_CHAR_COMMA = ",";
    String SEPARATOR_CHAR_SLASH = "/";
    String SEPARATOR_CHAR_HYHEN = "-";
    String SEPARATOR_CHAR_PERIOD = ".";
    String SEPARATOR_CHAR_UNDERLINE = "_";
    String SEPARATOR_CHAR_ASTERISK = "*";
    String SEPARATOR_CHAR_COLON = ":";

    //不同操作系统的换行
    String SEPARATOR_CHAR_NEWLINE_WIN = "\r\n";
    String SEPARATOR_CHAR_NEWLINE_LINUX = "\n";

    static String getNewLineSymbol() {
        return System.getProperty("line.separator", SEPARATOR_CHAR_NEWLINE_LINUX);
    }


}
