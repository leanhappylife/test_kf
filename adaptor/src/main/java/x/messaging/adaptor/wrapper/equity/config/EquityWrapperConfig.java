package x.messaging.adaptor.wrapper.equity.config;

import lombok.Getter;
import lombok.Setter;
import x.messaging.adaptor.wrapper.common.config.CommonTopicConfig;

/**
 * Configuration class for Equity wrapper settings.
 */
@Getter
@Setter
public class EquityWrapperConfig {
    private String broker;          // Kafka broker
    private String targetQueue;     // Target MQ queue
    private CommonTopicConfig topic; // Topic configuration
}
