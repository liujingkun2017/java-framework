
package org.liujk.java.framework.base.utils.lang.beans;



/**
 * 说明：
 * <p>
 * 属性拷贝转换器，所有的Converter都是单例的，需要有默认的构造器
 * <p>
 * O : 拷贝的源对象 S : 类型源属值 T : 目标属性类型
 *
 */
public interface BeanCopierConverter<O, S, T> {
    /**
     * 转换拷贝对象
     *
     * @param obj 拷贝的源对象
     * @param src 类型源属值
     *
     * @return 目标属性值
     */
    public T convert(O obj, S src);

}
