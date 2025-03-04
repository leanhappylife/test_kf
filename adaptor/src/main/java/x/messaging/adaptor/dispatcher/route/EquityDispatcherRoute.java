package x.messaging.adaptor.dispatcher.route;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.dispatcher.service.MessageDispatchService;

/**
 * Dispatcher route for Equity messages from MQ to Kafka.
 */
@Component
@Slf4j
public class EquityDispatcherRoute extends RouteBuilder {

    @Value("${kafkaDispatcher.equity.broker}")
    private String broker;

    @Value("${kafkaDispatcher.equity.sourceQueue}")
    private String sourceQueue;

    @Value("${kafkaDispatcher.equity.targetTopic}")
    private String targetTopic;

    @Value("${kafkaDispatcher.equity.concurrency}")
    private Integer concurrency;

    private final MessageDispatchService messageDispatchService;

    public EquityDispatcherRoute(MessageDispatchService messageDispatchService) {
        this.messageDispatchService = messageDispatchService;
    }

    @Override
    public void configure() throws Exception {
        String kafkaUri = String.format("kafka:%s?brokers=%s", targetTopic, broker);

        onException(Exception.class)
            .maximumRedeliveries(3)
            .redeliveryDelay(2000)
            .retryAttemptedLogLevel(org.apache.camel.LoggingLevel.ERROR)
            .handled(true)
            .log(org.apache.camel.LoggingLevel.ERROR, "Dispatcher Route Exception - Exchange ID: ${exchangeId}, Error: ${exception.message}")
            .stop();

        from("jms:queue:" + sourceQueue)
            .routeId("EquityDispatcherRoute")
            .threads(concurrency != null ? concurrency : 1)
            .log("Received message from MQ: ${body}")
            .process(exchange -> messageDispatchService.dispatchMessage(exchange))
            .to(kafkaUri)
            .log("Message dispatched to Kafka: ${body}");
    }
}
