package org.liujk.java.framework.base.utils.lang.beans.reference;

import java.lang.ref.PhantomReference;

public abstract class FinalizablePhantomReference<T> extends PhantomReference<T>
        implements FinalizableReference<T> {

    public FinalizablePhantomReference(T referent) {
        super(referent, FinalizableReferenceQueue.getInstance());
    }

    public FinalizablePhantomReference(T referent, FinalizableReferenceQueue finalizableReferenceQueue) {
        super(referent, finalizableReferenceQueue == null ? FinalizableReferenceQueue.getInstance() : finalizableReferenceQueue);
    }
}
