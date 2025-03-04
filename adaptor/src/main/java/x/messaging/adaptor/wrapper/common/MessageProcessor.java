package x.messaging.adaptor.wrapper.common;

import org.apache.camel.Exchange;

/**
 * Interface for message processors used in wrapper routes.
 */
public interface MessageProcessor {
    void process(Exchange exchange) throws Exception;
}
