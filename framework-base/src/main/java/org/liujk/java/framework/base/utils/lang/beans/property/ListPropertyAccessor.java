
package org.liujk.java.framework.base.utils.lang.beans.property;



import org.liujk.java.framework.base.utils.lang.CollectionUtils;

import java.util.List;

/**
 * 说明：
 * <p>
 * 属性所属 bean实例 为 {@link List} 的属性访问器。
 *
 */
public class ListPropertyAccessor extends SequencePropertyAccessor<List<Object>>
        implements PropertyAccessor {

    private final Class<?> type;

    /**
     * 使用访问的 list 的下标和目标 list 对象构造一个 ListPropertyAccessor 。
     *
     * @param name   访问的 list 的下标。
     * @param target 目标 list 对象实例。
     */
    public ListPropertyAccessor (Object name, List<Object> target) {
        this (name, target, null);
    }

    /**
     * 使用访问的 list 的下标和目标 list 对象以及 list 的元素类型信息构造一个 ListPropertyAccessor 。
     *
     * @param name   访问的 list 的下标。
     * @param target 目标 list 对象实例。
     * @param type   list 的元素类型信息。
     */
    public ListPropertyAccessor (Object name, List<Object> target, Class<?> type) {
        super (name, target);
        this.type = type == null ? Object.class : type;
    }

    @Override
    public Class<?> getType () {
        return this.type;
    }

    @Override
    public Object get () throws Exception {
        if (CollectionUtils.isEmpty (this.target) || this.index >= this.target.size ()) {
            return null;
        }
        return this.target.get (this.index);
    }

    @Override
    public void set (Object value) throws Exception {
        if (this.index >= this.target.size ()) {
            this.target.add (this.index, value);
        } else {
            this.target.set (this.index, value);
        }
    }

}