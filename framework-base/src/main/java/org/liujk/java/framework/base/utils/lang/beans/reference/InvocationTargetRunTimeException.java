
package org.liujk.java.framework.base.utils.lang.beans.reference;



import org.liujk.java.framework.base.exceptions.RunException;

import java.lang.reflect.InvocationTargetException;

/**
 * 说明：
 * <p>
 * InvocationTargetRunTimeException 是一种包装由调用方法或构造方法所抛出异常的不检查的异常。
 *
 */
public class InvocationTargetRunTimeException extends RunException {

    /**
     * 版本号
     */
    private static final long serialVersionUID = -3397928836420398271L;

    private InvocationTargetException invocationTargetException;

    /**
     * 构造以 null 作为目标异常的 InvocationTargetRunTimeException。
     */
    public InvocationTargetRunTimeException () {
        super ();
    }

    /**
     * 构造带目标异常的 InvocationTargetRunTimeException。
     *
     * @param target 目标异常
     */
    public InvocationTargetRunTimeException (Throwable target) {
        super (target);
        if (target instanceof InvocationTargetException) {
            this.invocationTargetException = (InvocationTargetException) target;
        }
    }

    /**
     * 构造带目标异常和详细消息的 InvocationTargetRunTimeException。
     *
     * @param target 目标异常
     * @param s      详细消息
     */
    public InvocationTargetRunTimeException (Throwable target, String s) {
        super (s, target);
    }

    /**
     * 获得抛出的目标异常。
     *
     * @return 抛出的目标异常（此异常的原因）。
     */
    public Throwable getTargetException () {
        return getCause ();
    }

    /**
     * 获得抛出的目标异常。
     *
     * @return 抛出的目标异常（此异常的原因）。
     */
    @Override
    public Throwable getCause () {
        if (this.invocationTargetException != null) {
            return this.invocationTargetException.getTargetException ();
        } else {
            return super.getCause ();
        }
    }
}
