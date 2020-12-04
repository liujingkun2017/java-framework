package org.liujk.java.framework.base.utils.lang.beans.converter;


import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.Map;

public abstract class AbstractTypeConverter<T> implements TypeConverter<T> {


    /**
     * 将给定的值转换成给定的类型
     *
     * @param paramterMap 在转换过程中需要传递的参数
     * @param m           在转换过程中需要传入的构造方法、或者字段
     * @param value       需要转换的对象
     * @param toType      需要转换到对象的类型
     * @param <M>
     * @return
     */
    @Override
    public <M extends AccessibleObject & Member> T convert(Map<?, ?> paramterMap, M m, Object value, Class<? extends T> toType) {
        return convert(paramterMap, value, toType);
    }

    /**
     * 将给定的值转换成给定的类型
     *
     * @param parameterMap 在转换过程中需要传递的参数
     * @param value        需要转换的对象
     * @param toType       需要转换到对象的类型
     * @return
     */
    @Override
    public T convert(Map<?, ?> parameterMap, Object value, Class<? extends T> toType) {
        return convert(value, toType);
    }

    /**
     * 将给定的值转换成给定的类型
     *
     * @param value
     * @param toType
     * @return
     */
    @Override
    public abstract T convert(Object value, Class<? extends T> toType);
}
