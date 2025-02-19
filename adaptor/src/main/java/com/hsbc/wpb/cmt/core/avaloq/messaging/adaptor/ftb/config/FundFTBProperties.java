package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.ftb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.AbstractFtbProductProperties;

@Component
@ConfigurationProperties(prefix = "kafka.ftb.fund")
public class FundFTBProperties extends AbstractFtbProductProperties {
    // Fund 特有属性可在此添加
}
