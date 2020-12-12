package org.liujk.java.framework.boot.starter.core.listener;

import org.liujk.java.framework.base.api.SerializableObject;

public class ApplicationRunListenerState extends SerializableObject {

    /**
     * 程序初始化状态
     */
    private boolean inited = false;

    public boolean isInited() {
        return inited;
    }

    public void setInited(boolean inited) {
        this.inited = inited;
    }
}
