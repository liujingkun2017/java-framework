
package org.liujk.java.framework.base.utils.lang;



/**
 * 说明：
 * <p>
 * 对象参数工具。
 *
 */
public abstract class ObjectParameterUtils {

    /**
     * 将对象参数转换为{@link Class}参数形式。
     *
     * @param parameters 对象参数。
     * @return 对象参数对应的{@link Class}参数，如果 parameters 为 null 则返回 null 。
     */
    public static Class<?>[] processParameterToParameterType(Object... parameters) {
        if (parameters == null) {
            return null;
        }
        Class<?>[] parameter = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameter[i] = parameters[i].getClass();
        }
        return parameter;
    }

}