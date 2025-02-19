package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.ftb.processor.common;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.MessageProcessor;

@Component("genericProcessor")
public class GenericProcessor implements MessageProcessor, Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        body = body.replace("Generic", "Processed Generic");
        exchange.getIn().setBody(body);
    }
}
