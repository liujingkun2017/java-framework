
package org.liujk.java.framework.base.utils.spring;




import com.google.common.collect.Maps;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.Map;

/**
 * 说明：
 * <p>
 *
 */
public class PropertiesUtils {
    private volatile static Map<String, PropertySource> propertiesMap = Maps.newConcurrentMap ();

    public static String getProperty (String groupId, String artifactId, String key) throws IOException {
        String path = "classpath:/META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties";
        PropertySource properties = propertiesMap.get (path);

        if (properties == null) {
            properties = new ResourcePropertySource(path);
        }
        if (properties != null) {
            return (String) properties.getProperty (key);
        }

        return null;
    }
}
