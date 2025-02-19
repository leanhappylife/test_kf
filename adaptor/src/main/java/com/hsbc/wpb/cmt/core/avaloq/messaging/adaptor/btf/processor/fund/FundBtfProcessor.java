package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.processor.fund;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.MessageProcessor;

@Component("fundBtfProcessor")
public class FundBtfProcessor implements MessageProcessor, Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        body = body.replace("Fund", "BTF Processed Fund");
        exchange.getIn().setBody(body);
    }
}
