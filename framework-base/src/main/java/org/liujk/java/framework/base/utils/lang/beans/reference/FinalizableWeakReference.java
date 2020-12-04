package org.liujk.java.framework.base.utils.lang.beans.reference;

import java.lang.ref.WeakReference;

public abstract class FinalizableWeakReference<T> extends WeakReference<T> implements FinalizableReference<T> {

    public FinalizableWeakReference(T referent) {
        super(referent, FinalizableReferenceQueue.getInstance());
    }

    public FinalizableWeakReference(T referent, FinalizableReferenceQueue finalizableReferenceQueue) {
        super(referent, finalizableReferenceQueue == null ? FinalizableReferenceQueue.getInstance() : finalizableReferenceQueue);
    }
}
