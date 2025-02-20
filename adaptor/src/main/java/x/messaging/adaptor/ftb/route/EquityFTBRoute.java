package x.messaging.adaptor.ftb.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.common.ProcessorFactory;
import x.messaging.adaptor.ftb.config.EquityFTBProperties;
import x.messaging.adaptor.ftb.config.FtbKafkaHeaderConfig;
import x.messaging.adaptor.ftb.config.FtbProcessorMappingProperties;

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
