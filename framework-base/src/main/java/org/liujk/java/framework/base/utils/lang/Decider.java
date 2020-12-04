
package org.liujk.java.framework.base.utils.lang;



/**
 * 说明：
 * <p>
 * 一个判定器， {@link #decide(Object)} 方法用作判定是否符合条件。
 *
 * @param <T> 需要参与判定的对象的类型
 *
 */
public interface Decider<T> {

    /**
     * 判定 t 是否符合条件。
     *
     * @param t 用做判定的对象。
     *
     * @return 如果 t 符合判定条件返回 true ，否则返回 false 。
     */
    boolean decide(T t);
}
