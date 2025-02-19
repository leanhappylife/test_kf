package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.processor.equity;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.MessageProcessor;

@Component("equityBtfProcessor")
public class EquityBtfProcessor implements MessageProcessor, Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        body = body.replace("Equity", "BTF Processed Equity");
        exchange.getIn().setBody(body);
    }
}
