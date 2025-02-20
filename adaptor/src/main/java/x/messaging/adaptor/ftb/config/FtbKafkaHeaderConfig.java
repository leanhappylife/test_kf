package x.messaging.adaptor.ftb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.common.AbstractKafkaHeaderConfig;

@Component
@ConfigurationProperties(prefix = "kafka.ftb.headers")
public class FtbKafkaHeaderConfig extends AbstractKafkaHeaderConfig {
    // 直接使用公共逻辑
}
