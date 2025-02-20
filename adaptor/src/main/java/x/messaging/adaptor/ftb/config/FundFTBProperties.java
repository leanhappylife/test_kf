package x.messaging.adaptor.ftb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.common.AbstractFtbProductProperties;

@Component
@ConfigurationProperties(prefix = "kafka.ftb.fund")
public class FundFTBProperties extends AbstractFtbProductProperties {
    // Fund 特有属性可在此添加
}
