
package org.liujk.java.framework.base.utils.lang.beans.reference;



import java.lang.reflect.Member;

/**
 * 说明：
 * <p>
 * {@link FieldTypeScannerProvider} 的默认实现，使用 {@link FieldTypeScanner} 作为扫描器实现。
 *
 */
public class SimpleFieldTypeScannerProvider implements FieldTypeScannerProvider {

    @Override
    public FieldTypeScanner newFieldTypeScanner (Class<?> beanClass, FieldTypeScanner.Mode mode,
                                                 Member member, Class<?> defaultClass) {
        return new FieldTypeScanner (mode, member, defaultClass);
    }

}
