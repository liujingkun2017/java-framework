
package org.liujk.java.framework.base.utils.lang.beans.reference;



import java.io.IOException;

/**
 * 说明：
 * <p>
 * 在加载服务提供者的过程中出错时抛出的错误。
 * <p>
 * <p>
 * 以下情况将抛出此错误：
 * <p>
 * <ul>
 * <p>
 * <li>提供者配置文件的格式违背规范。</li>
 * <p>
 * <li>读取提供者配置文件时发生 {@link IOException}。</li>
 * <p>
 * <li>无法找到在提供者配置文件中指定的具体提供者类。</li>
 * <p>
 * <li>具体提供者类不是服务类的子类。</li>
 * <p>
 * <li>具体提供者类无法被实例化或发生某些其他种类的错误。</li>
 * <p>
 * </ul>
 *
 */
public class ServiceConfigurationError extends Error {
    public ServiceConfigurationError (String msg) {
        super (msg);
    }

    public ServiceConfigurationError (String msg, Throwable cause) {
        super (msg, cause);
    }
}
