package x.messaging.adaptor.wrapper.equity.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.wrapper.common.MessageProcessor;

/**
 * Generic message processor for Equity messages.
 */
@Component("genericWrapperMessageProcessor")
@Slf4j
public class GenericWrapperMessageProcessor implements MessageProcessor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        log.info("Processing Equity message with generic processor: {}", body);
    }
}
