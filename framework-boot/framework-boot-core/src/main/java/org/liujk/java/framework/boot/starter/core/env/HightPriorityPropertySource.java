package org.liujk.java.framework.boot.starter.core.env;

import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class HightPriorityPropertySource extends PropertiesPropertySource{

    public static final String HIGHT_PRIORITY_PROPERTIES_PROPERTY_SOURCE_NAME = "hightPriorityProperties";

    public HightPriorityPropertySource (){
        this (new Properties ());
    }

    public HightPriorityPropertySource (Properties properties) {
        this (HIGHT_PRIORITY_PROPERTIES_PROPERTY_SOURCE_NAME, properties);
    }

    public HightPriorityPropertySource(String name, Properties source) {
        super(name, source);
    }

    public void setProperty (String name, Object value) {
        if (value == null) {
            this.source.remove (name);
        } else {
            this.source.put (name, value);
        }
    }

    public HightPriorityPropertySource withProperty (String name, Object value) {
        this.setProperty (name, value);
        return this;
    }
}
