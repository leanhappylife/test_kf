package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.ftb.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.ProcessorFactory;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.ftb.config.EquityFTBProperties;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.ftb.config.FtbKafkaHeaderConfig;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.ftb.config.FtbProcessorMappingProperties;

@Component
public class EquityFTBRoute extends AbstractFTBRoute {

    @Autowired
    private EquityFTBProperties equityFTBProperties;

    @Autowired
    private ProcessorFactory processorFactory;

    @Autowired
    private FtbKafkaHeaderConfig kafkaHeaderConfig;

    @Autowired
    private FtbProcessorMappingProperties processorMappingProperties;

    @Override
    protected String getKafkaBrokers() {
        return equityFTBProperties.getBroker();
    }

    @Override
    protected String getTopics() {
        return equityFTBProperties.getTopics().keySet().iterator().next();
    }

    @Override
    protected String getDestinationQueue() {
        return "commonQueue";
    }

    @Override
    protected ProcessorFactory getProcessorFactory() {
        return processorFactory;
    }

    @Override
    protected FtbKafkaHeaderConfig getKafkaHeaderConfig() {
        return kafkaHeaderConfig;
    }

    @Override
    protected FtbProcessorMappingProperties getFtbMappingProperties() {
        return processorMappingProperties;
    }

    @Override
    protected String getRouteId() {
        return "EquityFTBRoute";
    }

    @Override
    protected Integer getTopicConcurrency() {
        String topic = getTopics();
        EquityFTBProperties.TopicConfig config = equityFTBProperties.getTopics().get(topic);
        return config != null ? config.getConcurrency() : null;
    }
}
