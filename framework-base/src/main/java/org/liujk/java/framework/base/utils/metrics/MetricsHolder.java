
package org.liujk.java.framework.base.utils.metrics;



import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.SharedHealthCheckRegistries;

/**
 * 说明：
 * <p>
 * Metrics单例holder，重金所所有使用到Metrics的功能都应该注册到此类
 *
 */
public class MetricsHolder {
    private static MetricRegistry metricRegistry = SharedMetricRegistries
            .getOrCreate ("metricRegistry");
    private static HealthCheckRegistry healthCheckRegistry = SharedHealthCheckRegistries
            .getOrCreate ("healthCheckRegistry");

    public static MetricRegistry metricRegistry () {
        return metricRegistry;
    }

    public static HealthCheckRegistry healthCheckRegistry () {
        return healthCheckRegistry;
    }
}
