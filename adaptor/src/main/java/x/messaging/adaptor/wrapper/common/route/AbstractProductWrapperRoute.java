package x.messaging.adaptor.wrapper.common.route;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import x.messaging.adaptor.common.KafkaHeaderParser;
import x.messaging.adaptor.wrapper.common.MessageProcessor;
import x.messaging.adaptor.wrapper.common.config.CommonTopicConfig;
import x.messaging.adaptor.wrapper.common.factory.MessageProcessorFactory;

/**
 * Abstract route for product-specific wrapper routes, providing common Kafka consumption and processing logic.
 */
@Slf4j
public abstract class AbstractProductWrapperRoute extends RouteBuilder {

    protected final CommonTopicConfig topicConfig;
    protected final MessageProcessorFactory processorFactory;

    public AbstractProductWrapperRoute(CommonTopicConfig topicConfig, MessageProcessorFactory processorFactory) {
        this.topicConfig = topicConfig;
        this.processorFactory = processorFactory;
    }

    @Override
    public void configure() throws Exception {
        String kafkaUri = String.format("kafka:%s?brokers=%s&groupId=%s&autoOffsetReset=%s",
            topicConfig.getName(),
            getBroker(),
            topicConfig.getConsumer().getGroupId() + (topicConfig.getConsumer().getGroupIdSuffix() != null ? topicConfig.getConsumer().getGroupIdSuffix() : ""),
            topicConfig.getConsumer().getAutoOffsetReset());

        onException(Exception.class)
            .maximumRedeliveries(3)
            .redeliveryDelay(2000)
            .retryAttemptedLogLevel(org.apache.camel.LoggingLevel.ERROR)
            .handled(true)
            .log(org.apache.camel.LoggingLevel.ERROR, "Wrapper Route Exception - Exchange ID: ${exchangeId}, Error: ${exception.message}")
            .stop();

        from(kafkaUri)
            .routeId(getRouteId())
            .threads(topicConfig.getConcurrency() != null ? topicConfig.getConcurrency() : 1)
            .log("Received message from Kafka: ${body}")
            .process(exchange -> {
                String headersStr = exchange.getIn().getHeader("kafkaHeaders", String.class);
                String eventType = KafkaHeaderParser.parseHeaders(headersStr).get("eventType");
                MessageProcessor processor = processorFactory.getProcessor(eventType, topicConfig.getEventProcessorMapping());
                processor.process(exchange);
            })
            .marshal().json(JsonLibrary.Jackson)
            .to("jms:queue:" + getTargetQueue())
            .log("Message sent to MQ: ${body}");
    }

    protected abstract String getBroker();
    protected abstract String getRouteId();
    protected abstract String getTargetQueue();
}
