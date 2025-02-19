package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.AbstractProcessorMappingProperties;

@Component
@ConfigurationProperties(prefix = "kafka.btf.processor")
public class BtfProcessorMappingProperties extends AbstractProcessorMappingProperties {
    // 直接使用公共逻辑
}
