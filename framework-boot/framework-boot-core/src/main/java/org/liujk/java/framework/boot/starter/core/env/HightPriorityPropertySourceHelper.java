package org.liujk.java.framework.boot.starter.core.env;

public class HightPriorityPropertySourceHelper {

    private static final HightPriorityPropertySource PROPERTY_SOURCE = new HightPriorityPropertySource();

    public static void setProperty(String name, Object value) {
        if (value != null) {
            System.setProperty(name, value.toString());
        }
        PROPERTY_SOURCE.setProperty(name, value);
    }

    static HightPriorityPropertySource getHightPriorityPropertySource() {
        return PROPERTY_SOURCE;
    }
}
