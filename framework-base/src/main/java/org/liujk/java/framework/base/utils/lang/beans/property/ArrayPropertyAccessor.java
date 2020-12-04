
package org.liujk.java.framework.base.utils.lang.beans.property;



import java.lang.reflect.Array;

/**
 * 说明：
 * <p>
 * 属性所属 bean实例 为数组的属性访问器。
 *
 */
public class ArrayPropertyAccessor extends SequencePropertyAccessor<Object>
        implements PropertyAccessor {

    /**
     * 构造一个ArrayPropertyAccessor。
     *
     * @param index  访问元素的下标。
     * @param target 访问属性的目标数组实例。
     */
    public ArrayPropertyAccessor (Object index, Object target) {
        super (index, target);
    }

    @Override
    public Class<?> getType () {
        return this.target.getClass ().getComponentType ();
    }

    @Override
    public Object get () throws Exception {
        if (this.target == null || this.index >= Array.getLength (this.target)) {
            return null;
        }
        return Array.get (this.target, this.index);
    }

    @Override
    public void set (Object value) throws Exception {
        Array.set (this.target, this.index, value);
    }

}