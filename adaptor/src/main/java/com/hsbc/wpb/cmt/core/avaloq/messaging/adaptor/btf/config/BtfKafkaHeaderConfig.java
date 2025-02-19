package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.AbstractKafkaHeaderConfig;

@Component
@ConfigurationProperties(prefix = "kafka.btf.headers")
public class BtfKafkaHeaderConfig extends AbstractKafkaHeaderConfig {
    // 直接继承公共逻辑
}
