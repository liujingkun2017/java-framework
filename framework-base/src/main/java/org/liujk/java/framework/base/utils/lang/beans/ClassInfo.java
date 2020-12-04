
package org.liujk.java.framework.base.utils.lang.beans;



import com.google.common.collect.Maps;
import org.liujk.java.framework.base.utils.lang.ClassUtils;

import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 说明：
 * <p>
 * class类信息
 *
 */
public class ClassInfo {
    private Class<?> clazz;
    /**
     * 属性集合
     */
    private Map<String, PropertyDescriptor> propertyMap;
    /**
     * 方法集合
     */
    private Map<String, MethodDescriptor> methodMap;
    /**
     * getter集合
     */
    private Map<String, Method> getterMap;
    /**
     * setter集合
     */
    private Map<String, Method> setterMap;

    /**
     * 字段信息
     */
    private Map<String, FieldInfo> fieldInfoMap;

    private ClassInfo (Class<?> clazz) {
        this.clazz = clazz;
    }

    public static ClassInfo analyze (Class<?> clazz) {
        ClassInfo classInfo = new ClassInfo (clazz);

        classInfo.fieldInfoMap = analyzeFieldInfo (clazz);

        classInfo.propertyMap = Maps.newLinkedHashMap ();
        classInfo.getterMap = Maps.newHashMap ();
        classInfo.setterMap = Maps.newHashMap ();

        PropertyDescriptor[] properties = ClassUtils.getPropertyDescriptors (clazz);
        for (PropertyDescriptor property : properties) {
            classInfo.propertyMap.put (property.getName (), property);

            FieldInfo fieldInfo = classInfo.fieldInfoMap.get (property.getName ());
            if (fieldInfo != null) {
                fieldInfo.setPropertyDescriptor (property);
            }

            // getter 方法集合
            if (property.getReadMethod () != null) {
                classInfo.getterMap.put (property.getReadMethod ().getName (),
                                         property.getReadMethod ());
            }

            // setter 方法集合
            if (property.getWriteMethod () != null) {
                classInfo.setterMap.put (property.getWriteMethod ().getName (),
                                         property.getWriteMethod ());
            }
        }

        // 所有的方法集合
        classInfo.methodMap = Maps.newHashMap ();
        MethodDescriptor[] methods = ClassUtils.getMethodDescriptors (clazz);
        for (MethodDescriptor method : methods) {
            classInfo.methodMap.put (method.getName (), method);
        }

        return classInfo;
    }

    public static Map<String, FieldInfo> analyzeFieldInfo (Class<?> clazz) {
        return analyzeFieldInfo (clazz, new LinkedHashMap<String, Class<?>> ());
    }

    private static Map<String, FieldInfo> analyzeFieldInfo (Class<?> clazz,
                                                            LinkedHashMap<String, Class<?>> parameterTypes) {
        if (clazz == null || Object.class.equals (clazz)) {
            return Maps.newLinkedHashMap ();
        }

        // 获取泛型的类型
        Type superClass = clazz.getGenericSuperclass ();
        if (superClass instanceof ParameterizedType) {
            LinkedHashMap<String, Class<?>> parameterTypesTmp = Maps.newLinkedHashMap ();

            TypeVariable<Class>[] typeVariables = (TypeVariable[]) clazz.getSuperclass ()
                    .getTypeParameters ();
            for (TypeVariable<Class> typeVariable : typeVariables) {
                parameterTypesTmp.put (typeVariable.getName (), null);
            }

            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments ();

            Iterator<Map.Entry<String, Class<?>>> iterator = parameterTypesTmp.entrySet ()
                    .iterator ();
            for (Type type : actualTypeArguments) {
                Map.Entry<String, Class<?>> parameterType = iterator.next ();
                Class<?> orgType = parameterTypes.get (getTypeName (type));
                parameterType.setValue (orgType != null ? orgType
                                                : (type instanceof Class ? (Class<?>) type : Object.class));
            }

            parameterTypes.putAll (parameterTypesTmp);
        }

        // 获取字段信息
        Map<String, FieldInfo> fieldInfoMapTmp = Maps.newLinkedHashMap ();
        Field[] fields = clazz.getDeclaredFields ();
        for (Field field : fields) {
            FieldInfo fieldInfo = new FieldInfo (field);

            String fieldFullTypeName = analyzeFieldFullType (fieldInfo, field.getGenericType (),
                                                             parameterTypes);
            fieldInfo.setFullTypeName (fieldFullTypeName);
            fieldInfoMapTmp.put (field.getName (), fieldInfo);
        }
        // 先解析父类，子类覆盖父类
        Map<String, FieldInfo> fieldInfoMap = analyzeFieldInfo (clazz.getSuperclass (),
                                                                parameterTypes);
        fieldInfoMap.putAll (fieldInfoMapTmp);
        return fieldInfoMap;
    }

    private static String analyzeFieldFullType (FieldInfo fieldInfo, Type genericType,
                                                LinkedHashMap<String, Class<?>> parameterTypes) {
        Class<?> fieldType = parameterTypes.get (getTypeName (genericType));
        String fieldTypeName = (fieldType == null ? getTypeName (genericType) : fieldType.getName ());

        if (fieldTypeName == null) {
            return "";
        }

        if (fieldTypeName.indexOf ("<") > 0) {
            fieldTypeName = fieldTypeName.substring (0, fieldTypeName.indexOf ("<"));
        }
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = ((ParameterizedType) genericType);
            if (parameterizedType != null
                    && parameterizedType.getActualTypeArguments ().length > 0) {
                if (fieldInfo != null) {
                    fieldInfo.setTypeArguments (
                            new Class<?>[parameterizedType.getActualTypeArguments ().length]);
                }

                fieldTypeName += "<";
                for (int i = 0, len = parameterizedType
                        .getActualTypeArguments ().length; i < len; i++) {
                    if (i > 0) {
                        fieldTypeName += ", ";
                    }
                    Type actualTypeArgument = parameterizedType.getActualTypeArguments ()[i];
                    fieldType = parameterTypes.get (getTypeName (actualTypeArgument));
                    if (fieldInfo != null) {
                        fieldInfo.getTypeArguments ()[i] = fieldType == null
                                ? ((actualTypeArgument instanceof Class)
                                ? (Class<?>) actualTypeArgument
                                : null)
                                : fieldType;
                    }
                    if (fieldType == null) {
                        fieldTypeName += analyzeFieldFullType (null, actualTypeArgument,
                                                               parameterTypes);
                        // System.out.print(actualTypeArgument.getTypeName());
                    } else {
                        fieldTypeName += fieldType.getName ();
                    }
                }
                fieldTypeName += ">";
            }
        }
        return fieldTypeName;
    }

    private static String getTypeName (Type type) {
        if (type instanceof Class) {
            return ((Class) type).getName ();
        } else if (type instanceof TypeVariable) {
            return ((TypeVariable) type).getName ();
        } else if (type instanceof ParameterizedType) {
            return ((Class) ((ParameterizedType) type).getRawType ()).getName ();
        }
        return null;
    }

    public Class<?> getClazz () {
        return clazz;
    }

    public Map<String, PropertyDescriptor> getPropertyMap () {
        return propertyMap;
    }

    public Map<String, MethodDescriptor> getMethodMap () {
        return methodMap;
    }

    public Map<String, Method> getGetterMap () {
        return getterMap;
    }

    public Map<String, Method> getSetterMap () {
        return setterMap;
    }

    public Map<String, FieldInfo> getFieldInfoMap () {
        return fieldInfoMap;
    }
}
