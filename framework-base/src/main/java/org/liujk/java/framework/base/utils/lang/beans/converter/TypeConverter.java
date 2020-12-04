package org.liujk.java.framework.base.utils.lang.beans.converter;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Map;

/**
 * 类型转换器，可以通过实现该接口完成特定对象的类型转换
 * 该接口的实现通常被设计成单例模式，并且由typeConverterManager进行管理
 * @param <T>
 */
public interface TypeConverter<T> {

    /**
     * 得到转换的目标类型
     *
     * @return
     */
    Class<T> getTargetType();

    /**
     * 得到支持转换的类型列表
     *
     * @return
     */
    List<Class<?>> getSupportedSourceTypes();

    /**
     * 讲给定的值转换为给定的类型
     *
     * @param paramterMap 在转换过程中需要传递的参数
     * @param m           在转换过程中需要传入的构造方法、或者字段
     * @param value       需要转换的对象
     * @param toType      需要转换到对象的类型
     * @param <M>
     * @return
     */
    <M extends AccessibleObject & Member> T convert(
            Map<? extends Object, ? extends Object> paramterMap, M m, Object value, Class<? extends T> toType);


    /**
     * 将给定的值转换成给定的类型
     *
     * @param parameterMap 在转换过程中需要传递的参数
     * @param value        需要转换的对象
     * @param toType       需要转换到对象的类型
     * @return
     */
    T convert(Map<? extends Object, ? extends Object> parameterMap, Object value,
              Class<? extends T> toType);


    /**
     * 将给定的值转换为给定的类型
     *
     * @param value
     * @param toType
     * @return
     */
    T convert(Object value, Class<? extends T> toType);
}
