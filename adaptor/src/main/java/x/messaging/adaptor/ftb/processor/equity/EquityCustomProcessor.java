package x.messaging.adaptor.ftb.processor.equity;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.common.MessageProcessor;

@Component("equityCustomProcessor")
public class EquityCustomProcessor implements MessageProcessor, Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        body = body.replace("Equity", "Processed Equity Custom");
        exchange.getIn().setBody(body);
    }
}
