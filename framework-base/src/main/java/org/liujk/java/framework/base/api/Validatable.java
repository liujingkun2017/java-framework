package org.liujk.java.framework.base.api;

/**
 * 可以通过jsr303规范的注解来校验参数
 */
public interface Validatable {

    /**
     * 通过jsr303规范注解来校验参数
     *
     * @param groups
     */
    void validate(Class<?>... groups);

}
