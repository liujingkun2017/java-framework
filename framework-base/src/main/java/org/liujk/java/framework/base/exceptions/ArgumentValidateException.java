package org.liujk.java.framework.base.exceptions;

import org.liujk.java.framework.base.constants.SplitConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数非法异常
 */
public class ArgumentValidateException extends BizException {

    private Map<String, String> errorMap = new HashMap<>();

    private String message;

    public Map<String, String> getErrorMap() {
        return errorMap;
    }

    /**
     * 增加错误参数信息
     *
     * @param paramter
     * @param message
     */
    public void addError(String paramter, String message) {
        this.errorMap.put(paramter, message);
        this.message = null;
    }

    @Override
    public String getMessage() {
        if (message == null) {
            if (errorMap.isEmpty()) {
                message = "";
            } else {
                StringBuilder sb = new StringBuilder(errorMap.size() * 15);
                for (Map.Entry entry : errorMap.entrySet()) {
                    sb.append(entry.getKey()).append(SplitConstants.SEPARATOR_CHAR_COLON)
                            .append(entry.getValue()).append(SplitConstants.SEPARATOR_CHAR_COMMA);
                }
                sb.deleteCharAt(sb.length() - 1);
                message = sb.toString();
            }
        }
        return message;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
