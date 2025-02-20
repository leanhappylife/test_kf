package x.messaging.adaptor.ftb.processor.fund;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.common.MessageProcessor;

@Component("fundCustomProcessor")
public class FundCustomProcessor implements MessageProcessor, Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        body = body.replace("Fund", "Processed Fund Custom");
        exchange.getIn().setBody(body);
    }
}
