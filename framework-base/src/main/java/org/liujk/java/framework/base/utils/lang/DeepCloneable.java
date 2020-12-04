
package org.liujk.java.framework.base.utils.lang;


/**
 * 说明：
 * <p>
 * 指示 {@link #deepClone()} 方法可以合法地对该类实例进行按字段<strong>深度复制</strong>。
 *
 */
public interface DeepCloneable {
    /**
     * 创建并返回此对象的一个副本。该副本是此对象的深度克隆副本。
     *
     * @return 此对象的深度克隆副本。
     */
    Object deepClone();
}
