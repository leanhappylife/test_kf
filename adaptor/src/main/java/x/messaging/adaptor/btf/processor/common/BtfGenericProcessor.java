package x.messaging.adaptor.btf.processor.common;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import x.messaging.adaptor.common.MessageProcessor;

@Component("btfGenericProcessor")
public class BtfGenericProcessor implements MessageProcessor, Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        body = body.replace("Generic", "BTF Processed Generic");
        exchange.getIn().setBody(body);
    }
}
