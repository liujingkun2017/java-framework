
package org.liujk.java.framework.base.utils.lang.beans;


import org.liujk.java.framework.base.utils.lang.ToString;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * 说明：
 * <p>
 * 类的字段信息，主要是泛型信息
 *
 */
public class FieldInfo {
    @ToString.Invisible
    private Field field;

    private Class<?>[] typeArguments;

    private String fullTypeName;

    private PropertyDescriptor propertyDescriptor;

    public FieldInfo (Field field) {
        this.field = field;
    }

    public Field getField () {
        return field;
    }

    public int getModifiers () {
        return field.getModifiers ();
    }

    public String getName () {
        return field.getName ();
    }

    public Class<?> getType () {
        return field.getType ();
    }

    public String getTypeName () {
        return field.getType ().getName ();
    }

    public Class<?>[] getTypeArguments () {
        return typeArguments;
    }

    public void setTypeArguments (Class<?>[] typeArguments) {
        this.typeArguments = typeArguments;
    }

    public String getFullTypeName () {
        return fullTypeName;
    }

    public void setFullTypeName (String fullTypeName) {
        this.fullTypeName = fullTypeName;
    }

    public PropertyDescriptor getPropertyDescriptor () {
        return propertyDescriptor;
    }

    public void setPropertyDescriptor (PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
    }

    @Override
    public String toString () {
        return ToString.toString (this);
    }
}
