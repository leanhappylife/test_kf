package x.messaging.adaptor.ftb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.common.AbstractProcessorMappingProperties;

@Component
@ConfigurationProperties(prefix = "kafka.ftb.processor")
public class FtbProcessorMappingProperties extends AbstractProcessorMappingProperties {
    // 直接使用公共逻辑
}
