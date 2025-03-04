package x.messaging.adaptor.wrapper.fund.config;

import lombok.Getter;
import lombok.Setter;
import x.messaging.adaptor.wrapper.common.config.CommonTopicConfig;

/**
 * Configuration class for Fund wrapper settings.
 */
@Getter
@Setter
public class FundWrapperConfig {
    private String broker;          // Kafka broker
    private String targetQueue;     // Target MQ queue
    private CommonTopicConfig topic; // Topic configuration
}
