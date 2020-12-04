
package org.liujk.java.framework.base.utils.spring;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 说明：
 * <p>
 * spring ApplicationContext holder，把此类配置到spring 容器中，可以通过在非spring容器的代码中获取ApplicationContext
 *
 */
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    private static void setContext (ApplicationContext context) {
        ApplicationContextHolder.CONTEXT = context;
    }

    public static ApplicationContext get () {
        return CONTEXT;
    }

    @Override
    public void setApplicationContext (ApplicationContext context) throws BeansException {
        if (CONTEXT != null) {
            if (context.getParent () == CONTEXT) {
                setContext (context);
            }
        } else {
            setContext (context);
        }
    }

}
