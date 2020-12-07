package org.liujk.java.framework.boot.starter.core.listener;

import lombok.Data;
import org.liujk.java.framework.base.api.SerializableObject;

@Data
public class ApplicationRunListenerState extends SerializableObject {

    private boolean inited = false;

    private boolean starting = false;

    private boolean environmentPrepared = false;

    private boolean contextPrepared = false;

    private boolean contextLoaded = false;

    private boolean finished = false;

}
