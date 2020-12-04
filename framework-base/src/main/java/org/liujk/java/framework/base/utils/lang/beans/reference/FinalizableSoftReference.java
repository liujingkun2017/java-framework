package org.liujk.java.framework.base.utils.lang.beans.reference;


import java.lang.ref.SoftReference;

public abstract class FinalizableSoftReference<T> extends SoftReference<T> implements FinalizableReference<T> {


    public FinalizableSoftReference(T referent) {
        super(referent, FinalizableReferenceQueue.getInstance());
    }

    public FinalizableSoftReference(T referent, FinalizableReferenceQueue finalizableReferenceQueue) {
        super(referent, finalizableReferenceQueue == null ? FinalizableReferenceQueue.getInstance() : finalizableReferenceQueue);
    }
}
