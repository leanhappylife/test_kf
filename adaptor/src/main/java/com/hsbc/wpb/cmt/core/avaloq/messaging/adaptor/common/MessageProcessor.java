package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common;

import org.apache.camel.Exchange;

public interface MessageProcessor {
    void process(Exchange exchange) throws Exception;
}
