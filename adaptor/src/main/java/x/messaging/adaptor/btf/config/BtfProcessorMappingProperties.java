package x.messaging.adaptor.btf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.common.AbstractProcessorMappingProperties;

@Component
@ConfigurationProperties(prefix = "kafka.btf.processor")
public class BtfProcessorMappingProperties extends AbstractProcessorMappingProperties {
    // 直接使用公共逻辑
}
