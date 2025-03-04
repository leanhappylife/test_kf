package x.messaging.adaptor.dispatcher.service;

import org.apache.camel.Exchange;

/**
 * Service interface for dispatching messages from MQ to Kafka.
 */
public interface MessageDispatchService {
    void dispatchMessage(Exchange exchange) throws Exception;
}
