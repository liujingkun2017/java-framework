package org.liujk.java.framework.boot.starter.core.listener;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.liujk.java.framework.boot.starter.core.FrameworkBootApplication;
import org.liujk.java.framework.boot.starter.core.env.HightPriorityPropertySourceHelper;
import org.liujk.java.framework.boot.starter.core.exception.AppConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.util.List;

public class ApplicationRunListener implements SpringApplicationRunListener, PriorityOrdered {

    private volatile static ApplicationRunListenerState state = new ApplicationRunListenerState();

    public ApplicationRunListener(SpringApplication application, String[] args) {
        // 对于未初始化的应用执行如下的验证操作
        if (!state.isInited()) {
            application.setRegisterShutdownHook(false);

            // 根据实际需要，对于启动的相关环境、代码、版本等一些内容进行验证

            // 获取启动类注解
            FrameworkBootApplication frameworkBootApplication = findFrameworkBootApplication(application);

            // 初始化环境变量
            initEnvVars(frameworkBootApplication);

            // 设置线程名称
            setThreadName();

            // 设置banner
        }
        // 验证操作通过后，将初始化装填修改成已初始化
        state.setInited(true);
    }

    /**
     * 获取启动类注解
     *
     * @param application
     * @return
     */
    private FrameworkBootApplication findFrameworkBootApplication(SpringApplication application) {
        Class<?> mainClass = application.getMainApplicationClass();
        FrameworkBootApplication frameworkBootApplication = mainClass.getAnnotation(FrameworkBootApplication.class);
        if (frameworkBootApplication == null) {
            // 如果跑的是测试用例，应当从source中获取main class
            return (FrameworkBootApplication) application.getAllSources().stream()
                    .map(o1 -> (Class) o1)
                    .filter(o1 -> o1.isAnnotationPresent(FrameworkBootApplication.class)).findFirst()
                    .orElseThrow(
                            () -> new AppConfigException(
                                    "应用项目的启动类必须标注" + FrameworkBootApplication.class.getSimpleName()))
                    .getAnnotation(FrameworkBootApplication.class);
        }
        return frameworkBootApplication;
    }

    /**
     * 初始化环境变量¬
     *
     * @param frameworkBootApplication
     */
    private void initEnvVars(FrameworkBootApplication frameworkBootApplication) {

        String appName = frameworkBootApplication.appName();
        HightPriorityPropertySourceHelper.setProperty("appName", appName);
        //ref ContextIdApplicationContextInitializer
        HightPriorityPropertySourceHelper.setProperty("spring.application.name", appName);
        //set servlet container display name
        HightPriorityPropertySourceHelper.setProperty("server.display-name", appName);

        int httpPort = frameworkBootApplication.httpPort();
        System.setProperty("app.httpport", String.valueOf(httpPort));


    }

    /**
     * 设置当前线程名称
     */
    private void setThreadName() {
        Thread.currentThread().setName("Main");
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }

    public static class AppBanner implements Banner {
        private static List<String> infos = Lists.newArrayList();
        private FrameworkBootApplication application;

        public AppBanner(FrameworkBootApplication application) {
            this.application = application;
        }

        public static List<String> getInfos() {
            return infos;
        }

        @Override
        public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
            printAppInfo();
        }

        private void printAppInfo() {
            //don't init log system in object create phase
            Logger logger = LoggerFactory.getLogger(AppBanner.class);
            logger.info("************************************");
            logger.info("************************************");
            if (infos != null) {
                infos.forEach(logger::info);
                infos.clear();
            }
        }
    }

}
