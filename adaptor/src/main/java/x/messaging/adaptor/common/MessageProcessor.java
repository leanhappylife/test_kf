package x.messaging.adaptor.common;

import org.apache.camel.Exchange;

public interface MessageProcessor {
    void process(Exchange exchange) throws Exception;
}
