package org.liujk.java.framework.base.utils.lang.beans.reference;

public interface FinalizableReference<T> {

    /**
     * 清除动作的回调方法
     * 在该类实现类包装的引用对象被垃圾回收器回收时调用该方法
     */
    void finalizeReferent();

}
