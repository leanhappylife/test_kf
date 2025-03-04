package x.messaging.adaptor.wrapper.common.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

/**
 * Default implementation for processing messages in wrapper routes.
 */
@Service("defaultMessageProcessingService")
@Slf4j
public class DefaultMessageProcessingService implements MessageProcessingService {
    @Override
    public void processMessage(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        log.info("Processing message with default service: {}", body);
    }
}
