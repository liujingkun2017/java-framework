package org.liujk.java.framework.base.exceptions;

/**
 * 文件操作异常
 */
public class FileOperateException extends RunException {

    public FileOperateException() {
        super();
    }

    public FileOperateException(String message) {
        super(message);
    }

    public FileOperateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileOperateException(Throwable cause) {
        super(cause);
    }
}
