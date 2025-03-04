package x.messaging.adaptor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.wrapper.common.config.ThreadPoolConfig;

/**
 * Configuration class for default thread pool settings, mapped from application.yml under "kafka-wrapper.default-thread-pool".
 */
@Component
@ConfigurationProperties(prefix = "kafka-wrapper.default-thread-pool")
public class DefaultThreadPoolConfig extends ThreadPoolConfig {
}