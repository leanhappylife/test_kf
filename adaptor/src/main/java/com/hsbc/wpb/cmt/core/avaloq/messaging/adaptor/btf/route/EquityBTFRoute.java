package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.config.TopicMappingProperties;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.config.BtfKafkaHeaderConfig;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.config.BtfProcessorMappingProperties;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.ProcessorFactory;
import java.util.Map;

@Component
public class EquityBTFRoute extends AbstractBTFRoute {

    @Value("${mq.queue}")
    private String mqQueue;

    @Value("${kafka.btf.equity.broker}")
    private String equityBtfBroker;

    @Autowired
    private TopicMappingProperties topicMappingProperties;

    @Autowired
    private ProcessorFactory processorFactory;

    @Autowired
    private BtfKafkaHeaderConfig kafkaHeaderConfig;

    @Autowired
    private BtfProcessorMappingProperties processorMappingProperties;

    @Override
    protected String getMqQueue() {
        return mqQueue;
    }

    @Override
    protected String getKafkaBrokers() {
        return equityBtfBroker;
    }

    @Override
    protected TopicMappingProperties getTopicMappingProperties() {
        return topicMappingProperties;
    }

    @Override
    protected ProcessorFactory getProcessorFactory() {
        return processorFactory;
    }

    @Override
    protected BtfKafkaHeaderConfig getKafkaHeaderConfig() {
        return kafkaHeaderConfig;
    }

    @Override
    protected Map<String, String> getBtfMapping() {
        return processorMappingProperties.getMapping();
    }

    @Override
    protected String getRouteId() {
        return "EquityBTFRoute";
    }
}
