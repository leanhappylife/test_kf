package x.messaging.adaptor.wrapper.equity.route;

import org.springframework.stereotype.Component;
import x.messaging.adaptor.wrapper.common.factory.MessageProcessorFactory;
import x.messaging.adaptor.wrapper.common.route.AbstractProductWrapperRoute;
import x.messaging.adaptor.wrapper.equity.config.EquityWrapperConfig;

/**
 * Route for Equity wrapper, consuming from Kafka and sending to MQ.
 */
@Component
public class EquityWrapperRoute extends AbstractProductWrapperRoute {

    private final EquityWrapperConfig config;

    public EquityWrapperRoute(EquityWrapperConfig config, MessageProcessorFactory processorFactory) {
        super(config.getTopic(), processorFactory);
        this.config = config;
    }

    @Override
    protected String getBroker() {
        return config.getBroker();
    }

    @Override
    protected String getRouteId() {
        return "EquityWrapperRoute";
    }

    @Override
    protected String getTargetQueue() {
        return config.getTargetQueue();
    }
}
