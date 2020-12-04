
package org.liujk.java.framework.base.utils.lang.beans.reference;



import org.liujk.java.framework.base.exceptions.RunException;

/**
 * 说明：
 * <p>
 * 生成类的实例时出现错误则抛出该异常。
 *
 */
public class InstantiationRuntimeException extends RunException {

    public InstantiationRuntimeException () {
        super ();
    }

    public InstantiationRuntimeException (String msg) {
        super (msg);
    }

    public InstantiationRuntimeException (Throwable e) {
        super (e);
    }

    public InstantiationRuntimeException (String msg, Throwable e) {
        super (msg, e);
    }
}
