package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.ftb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.AbstractProcessorMappingProperties;

@Component
@ConfigurationProperties(prefix = "kafka.ftb.processor")
public class FtbProcessorMappingProperties extends AbstractProcessorMappingProperties {
    // 直接使用公共逻辑
}
