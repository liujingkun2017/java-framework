
package org.liujk.java.framework.base.utils.lang.beans.property;



/**
 * 说明：
 * <p>
 * {@link PropertyWrapper} 的骨干实现，继承该类可以简化实现 {@link PropertyWrapper} 。
 *
 */
public abstract class AbstractPropertyWrapper implements PropertyWrapper {

    /**
     * 被包装的属性在 bean 的 {@link Class} 中的属性名称
     */
    protected final Object name;

    /**
     * 被包装的属性的类型信息
     */
    protected final Class<?> type;

    /**
     * 给子类实例化的构造方法。
     *
     * @param name 被包装的属性在 bean 的 {@link Class} 中的属性名称。
     * @param type 被包装的属性的类型信息。
     */
    public AbstractPropertyWrapper (Object name, Class<?> type) {
        super ();
        this.name = name;
        this.type = type;
    }

    @Override
    public Object getName () {
        return this.name;
    }

    @Override
    public Class<?> getType () {
        return this.type;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName () == null) ? 0 : getName ().hashCode ());
        result = prime * result + ((getType () == null) ? 0 : getType ().hashCode ());
        return result;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!PropertyWrapper.class.isAssignableFrom (obj.getClass ())) {
            return false;
        }
        PropertyWrapper other = (PropertyWrapper) obj;
        if (getName () == null) {
            if (other.getName () != null) {
                return false;
            }
        } else if (!getName ().equals (other.getName ())) {
            return false;
        }
        if (getType () == null) {
            if (other.getType () != null) {
                return false;
            }
        } else if (!getType ().equals (other.getType ())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString () {
        return getClass ().getSimpleName () + " [name=" + getName () + ", type=" + getType () + "]";
    }

}
