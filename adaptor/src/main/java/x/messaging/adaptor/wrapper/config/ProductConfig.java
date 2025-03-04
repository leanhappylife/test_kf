package x.messaging.adaptor.wrapper.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.wrapper.common.config.CommonTopicConfig;
import x.messaging.adaptor.wrapper.equity.config.EquityWrapperConfig;
import x.messaging.adaptor.wrapper.fund.config.FundWrapperConfig;

import java.util.Map;

/**
 * Configuration class for all product-specific wrapper settings.
 */
@Component
@ConfigurationProperties(prefix = "kafka-wrapper")
@Getter
@Setter
public class ProductConfig {
    private Map<String, CommonTopicConfig> topics; // Common topic configurations
    private EquityWrapperConfig equity;           // Equity-specific configuration
    private FundWrapperConfig fund;               // Fund-specific configuration
}