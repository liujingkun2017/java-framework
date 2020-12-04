
package org.liujk.java.framework.base.utils.lang.beans;



import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

/**
 * 说明：
 * <p>
 * 转换器工厂，让所有的转换器都是单例
 *
 */
public class BeanCopierConverterFactory {
    private static volatile ConcurrentMap<Class, BeanCopierConverter> converterMap = Maps
            .newConcurrentMap ();

    /**
     * 注册
     *
     * @param clazz
     */
    public static void registry (Class<? extends BeanCopierConverter> clazz) {
        if (!converterMap.containsKey (clazz)) {
            try {
                converterMap.putIfAbsent (clazz, clazz.newInstance ());
            } catch (Exception e) {
            }
        }
    }

    /**
     * 获取Converter
     *
     * @param clazz
     *
     * @return
     */
    public static BeanCopierConverter getConverter (Class<? extends BeanCopierConverter> clazz) {
        BeanCopierConverter converter = converterMap.get (clazz);
        if (converter == null) {
            registry (clazz);
            converter = converterMap.get (clazz);
        }
        return converter;
    }
}
