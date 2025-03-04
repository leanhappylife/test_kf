package x.messaging.adaptor.dispatcher.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

/**
 * Default implementation for dispatching messages from MQ to Kafka.
 */
@Service
@Slf4j
public class DefaultMessageDispatchService implements MessageDispatchService {
    @Override
    public void dispatchMessage(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        log.info("Dispatching message: {}", body);
    }
}
