
package org.liujk.java.framework.base.utils.lang.beans.reference;


import java.lang.reflect.Member;

/**
 * 说明：
 * <p>
 * 字段类型扫描器提供者，用于创建字段类型扫描器的实例。
 *
 */
public interface FieldTypeScannerProvider {
    /**
     * 创建一个字段类型扫描器。
     *
     * @param beanClass    需要扫描的类的 {@link Class} 对象。
     * @param mode         使用的模式。
     * @param member       为扫描器给定的需要扫描字段的信息。
     * @param defaultClass 为扫描器给定的如果没有扫描到类型信息则使用的默认类型。
     *
     * @return 合适的字段类型扫描器。
     */
    FieldTypeScanner newFieldTypeScanner(Class<?> beanClass, FieldTypeScanner.Mode mode,
                                         Member member, Class<?> defaultClass);
}
