package x.messaging.adaptor.wrapper.common.service;

import org.apache.camel.Exchange;

/**
 * Service interface for processing messages in wrapper routes.
 */
public interface MessageProcessingService {
    void processMessage(Exchange exchange) throws Exception;
}
