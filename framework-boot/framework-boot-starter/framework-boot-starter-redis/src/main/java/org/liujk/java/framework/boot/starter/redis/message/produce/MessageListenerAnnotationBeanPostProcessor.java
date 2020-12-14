package org.liujk.java.framework.boot.starter.redis.message.produce;

import com.google.common.collect.Lists;
import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.liujk.java.framework.base.utils.thread.MonitoredThreadPool;
import org.liujk.java.framework.boot.starter.core.ApplicationInfo;
import org.liujk.java.framework.boot.starter.redis.config.RedisProperties;
import org.liujk.java.framework.boot.starter.redis.message.RedisMessage;
import org.liujk.java.framework.boot.starter.redis.message.subscribe.RedisMessageListener;
import org.liujk.java.framework.boot.starter.redis.message.subscribe.RedisMessageSubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MessageListenerAnnotationBeanPostProcessor
        implements ApplicationContextAware, InitializingBean, BeanPostProcessor, Ordered, BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(MessageListenerAnnotationBeanPostProcessor.class);
    private static final String OR = " || ";
    private final MessageListenerAdapter messageListenerMethodFactory
            = new MessageListenerAdapter();
    private final AtomicInteger counter = new AtomicInteger();
    private String namespace;
    private BeanFactory beanFactory;
    private BeanExpressionResolver resolver = new StandardBeanExpressionResolver();

    private BeanExpressionContext expressionContext;
    private RedisConnectionFactory connectionFactory;

    private ApplicationContext applicationContext;
    private MonitoredThreadPool monitoredThreadPool;


    public MessageListenerAnnotationBeanPostProcessor(String namespace, RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.namespace = namespace;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.resolver = ((ConfigurableListableBeanFactory) beanFactory).getBeanExpressionResolver();
            this.expressionContext = new BeanExpressionContext((ConfigurableListableBeanFactory) beanFactory, null);
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (!isMatchPackage(targetClass)) {
            return bean;
        }
        ReflectionUtils.doWithMethods(targetClass, (Method method) -> {
            RedisMessageSubscribe messageListener = AnnotationUtils.getAnnotation(method, RedisMessageSubscribe.class);
            if (messageListener != null) {
                logger.info("{}-{}找到RedisMessageListener: {}", beanName, method.getName(),
                        messageListener.toString());
                processMessageListener(messageListener, method, bean, beanName);
            }
        });
        return bean;
    }


    protected void processMessageListener(RedisMessageSubscribe messageSubscribe, Method method, Object bean,
                                          String beanName) {
        monitoredThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                checkMethodParameter(method);
                connectionFactory.getConnection()
                        .subscribe(new RedisMessageListener(new HandlerMethod(bean, method)),
                                resolveChannels(messageSubscribe));
                logger.info("增加MessageListener:{}#{}", bean.getClass().getName(), method.getName());
            }
        });
    }


    protected void checkMethodParameter(Method method) {
        if (method.getParameterCount() != 1 || !method.getParameterTypes()[0].equals(RedisMessage.class)) {
            throw new BeanInitializationException("消息监听方法，只能有一个参数，参数类型为 com.cqfae.pmo.boot.redis.RedisMessage");
        }
    }

    private boolean isMatchPackage(Class clazz) {
        return clazz.getName().startsWith(ApplicationInfo.getBasePackage());
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    private byte[][] resolveChannels(RedisMessageSubscribe messageSubscribe) {
        String[] channeles = messageSubscribe.channel();
        List<byte[]> result = Lists.newArrayList();
        for (String channel : channeles) {
            result.add((namespace + ":" + channel).getBytes());
        }
        return result.toArray(new byte[result.size()][]);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RedisProperties redisProperties = applicationContext.getBean(RedisProperties.class);
        try {
            String beanName = redisProperties.getThreadPoolBeanName();
            monitoredThreadPool = StringUtils.isEmpty(beanName)
                    ? applicationContext.getBean(MonitoredThreadPool.class)
                    : applicationContext.getBean(beanName, MonitoredThreadPool.class);
        } catch (NoSuchBeanDefinitionException e) {
            //如果不存在线程池定义，那么就自行创建，并委托给容器管理
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory) applicationContext
                    .getAutowireCapableBeanFactory();

            RootBeanDefinition db = new RootBeanDefinition(MonitoredThreadPool.class);
            db.setScope(BeanDefinition.SCOPE_SINGLETON);

            MutablePropertyValues threadPoolPropertyValues = new MutablePropertyValues();
            db.setPropertyValues(threadPoolPropertyValues);

            threadPoolPropertyValues.add("corePoolSize", 2);
            threadPoolPropertyValues.add("keepAliveSeconds", 300);
            threadPoolPropertyValues.add("maxPoolSize", 10);
            threadPoolPropertyValues.add("queueCapacity", 2000);

            threadPoolPropertyValues.add("threadNamePrefix", "ADK-EVENT");
            threadPoolPropertyValues.add("enableGaugeMetric", true);
            threadPoolPropertyValues.add("enableTimerMetric", true);

            factory.registerBeanDefinition("com.cqfae.pmo.boot.redis.threadpool", db);
            this.monitoredThreadPool = factory.getBean("com.cqfae.pmo.boot.redis.threadpool",
                    MonitoredThreadPool.class);
        }
    }
}
