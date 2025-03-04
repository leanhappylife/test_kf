package x.messaging.adaptor.wrapper.fund.route;

import org.springframework.stereotype.Component;
import x.messaging.adaptor.wrapper.common.factory.MessageProcessorFactory;
import x.messaging.adaptor.wrapper.common.route.AbstractProductWrapperRoute;
import x.messaging.adaptor.wrapper.fund.config.FundWrapperConfig;

/**
 * Route for Fund wrapper, consuming from Kafka and sending to MQ.
 */
@Component
public class FundWrapperRoute extends AbstractProductWrapperRoute {

    private final FundWrapperConfig config;

    public FundWrapperRoute(FundWrapperConfig config, MessageProcessorFactory processorFactory) {
        super(config.getTopic(), processorFactory);
        this.config = config;
    }

    @Override
    protected String getBroker() {
        return config.getBroker();
    }

    @Override
    protected String getRouteId() {
        return "FundWrapperRoute";
    }

    @Override
    protected String getTargetQueue() {
        return config.getTargetQueue();
    }
}
