package x.messaging.adaptor.wrapper.equity.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.wrapper.common.MessageProcessor;

/**
 * Custom message processor for Equity-specific messages.
 */
@Component("equityCustomWrapperMessageProcessor")
@Slf4j
public class EquityCustomWrapperMessageProcessor implements MessageProcessor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        log.info("Processing Equity message with custom processor: {}", body);
        exchange.getIn().setBody("Processed: " + body);
    }
}
