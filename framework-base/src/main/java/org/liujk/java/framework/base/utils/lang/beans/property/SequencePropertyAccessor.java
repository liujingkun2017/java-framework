
package org.liujk.java.framework.base.utils.lang.beans.property;



import org.liujk.java.framework.base.utils.lang.StringUtils;

/**
 * 说明：
 * <p>
 * 属性所属 bean实例 为 可按照序列访问对象 的属性访问器。
 *
 * @param <T> 访问属性的目标 bean 对象。
 *
 */
public abstract class SequencePropertyAccessor<T> extends AbstractPropertyAccessor<T> {

    /**
     * 访问元素的下标
     */
    protected final int index;

    /**
     * 给子类实例化的构造方法。
     *
     * @param index  访问元素的下标。
     * @param target 访问属性的目标 bean 对象。
     */
    public SequencePropertyAccessor (Object index, T target) {
        super (index, target);
        this.index = Integer.parseInt (StringUtils.toString (index));
    }

}
