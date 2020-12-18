package org.liujk.java.framework.boot.starter.core.listener;

import lombok.Data;
import org.liujk.java.framework.base.api.SerializableObject;

@Data
public class ApplicationRunListenerState extends SerializableObject {

    /**
     * 程序初始化状态
     */
    private boolean inited = false;

}
