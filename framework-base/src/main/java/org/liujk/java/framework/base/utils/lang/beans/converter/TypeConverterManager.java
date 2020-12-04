package org.liujk.java.framework.base.utils.lang.beans.converter;


import java.util.Collection;

/**
 * 类型转换管理器
 */
public interface TypeConverterManager {

    /**
     * 表示可用作所有源类型"通配符"的class
     */
    public static final Class<?> ALL_SOURCE_TYPE_CLASS = AllSourceType.class;

    /**
     * 通过需要转换到的类型得到对应的类型转换器
     * 如果不存在则返回空集合
     *
     * @param targetType
     * @param <T>
     * @return
     */
    <T> Collection<TypeConverter<T>> getTypeConverter(Class<T> targetType);


    /**
     * 通过需要转换到的类型得到对应的类型转换器
     * 如果不存在则返回空集合
     *
     * @param sourceType
     * @param targetType
     * @param <S>
     * @param <T>
     * @return
     */
    <S, T> TypeConverter<T> getTypeConverter(Class<S> sourceType, Class<T> targetType);


    /**
     * 以源类型和需要转换到的类型为key，以类型转换器为值，注册一个类型转换器到管理器中
     * 该注册会忽略转换器自身提供的源类型和转换到的目标类型
     *
     * @param sourceType
     * @param targetType
     * @param typeConverter
     * @param <S>
     * @param <T>
     */
    <S, T> void register(Class<? extends S> sourceType, Class<T> targetType, TypeConverter<? extends T> typeConverter);

    /**
     * 注册一个类型转换器
     *
     * @param typeConverter
     */
    void register(TypeConverter<?> typeConverter);

    /**
     * 以源类型和需要转换到的类型为key解除一个类型转换器的注册
     *
     * @param sourceType
     * @param targetType
     * @param <S>
     * @param <T>
     * @return
     */
    <S, T> TypeConverter<T> unregister(Class<T> sourceType, Class<T> targetType);


    /**
     * 以需要转换到的类型为key，解除对应类型转换器的所有注册
     *
     * @param targetType
     * @param <T>
     * @return
     */
    <T> Collection<TypeConverter<T>> unregister(Class<T> targetType);

    /**
     * 查看一个需要转换到的类型是否在类型转换管理器中注册了类型转换器
     *
     * @param targetType
     * @return
     */
    boolean containsType(Class<?> targetType);

    /**
     * 查看一个需要转换到的类型是否在类型转换管理器中注册了类型转换器
     *
     * @param sourceType
     * @param targetType
     * @return
     */
    boolean containsType(Class<?> sourceType, Class<?> targetType);

    /**
     * 查看一个类型转换器是否在类型转换器管理器中被注册过
     *
     * @param typeConverterClass
     * @return
     */
    boolean containsConverter(Class<TypeConverter<?>> typeConverterClass);

    /**
     * 判定该类型转换器管理器是否支持一组类型的转换
     *
     * @param sourceType
     * @param targetType
     * @return
     */
    boolean canConvert(Class<?> sourceType, Class<?> targetType);

    /**
     * 转换一个对象到目标类型
     *
     * @param source
     * @param targetType
     * @param <T>
     * @return
     */
    <T> T convert(Object source, Class<T> targetType);

    static final class AllSourceType {

    }

}
