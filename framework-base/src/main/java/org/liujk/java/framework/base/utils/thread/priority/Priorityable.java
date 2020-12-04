package org.liujk.java.framework.base.utils.thread.priority;


public interface Priorityable {

    Priority getPriority();

    public static enum Priority {
        HIGHEST, NORMAL, LOWEST
    }
}
