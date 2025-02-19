package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.ftb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.AbstractKafkaHeaderConfig;

@Component
@ConfigurationProperties(prefix = "kafka.ftb.headers")
public class FtbKafkaHeaderConfig extends AbstractKafkaHeaderConfig {
    // 直接使用公共逻辑
}
