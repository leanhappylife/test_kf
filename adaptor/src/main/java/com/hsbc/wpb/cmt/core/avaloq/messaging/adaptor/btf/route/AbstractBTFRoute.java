package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.route;

import org.apache.camel.builder.RouteBuilder;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.config.TopicMappingProperties;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.config.BtfKafkaHeaderConfig;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.config.MQMessage;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.MessageProcessor;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.ProcessorFactory;
//import org.apache.camel.model.dataformat.JsonLibrary;
import java.util.List;

public abstract class AbstractBTFRoute extends RouteBuilder {

    // 子类提供 MQ 队列名称（MQ→Kafka）
    protected abstract String getMqQueue();
    // 子类提供 Kafka Broker 地址（BTF方向）
    protected abstract String getKafkaBrokers();
    // 子类提供 TopicMappingProperties 实例
    protected abstract TopicMappingProperties getTopicMappingProperties();
    // 子类提供 ProcessorFactory 实例
    protected abstract ProcessorFactory getProcessorFactory();
    // 子类提供 BtfKafkaHeaderConfig 实例
    protected abstract BtfKafkaHeaderConfig getKafkaHeaderConfig();
    // 子类提供 BTF 方向 processor 映射配置（Map 类型）
    protected abstract java.util.Map<String, String> getBtfMapping();

    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .log("BTF Route Exception (${routeId}): ${exception.message}")
                .to("jms:queue:errorQueue")
                .stop();

        from("jms:queue:" + getMqQueue())
                .routeId(getRouteId())
                .log("BTF Received MQ message: ${body}")
//                .unmarshal().jaxb(com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.config.MQMessage.class)
                .log("BTF Unmarshalled MQ message: ${body}")
                .process(exchange -> {
                    MQMessage message = exchange.getIn().getBody(MQMessage.class);
                    String productType = message.getProductType();
                    if (productType == null || productType.isEmpty()) {
                        throw new IllegalArgumentException("productType is missing in the MQ message");
                    }
                    String targetTopic = getTopicMappingProperties().getMapping().get(productType);
                    if (targetTopic == null || targetTopic.isEmpty()) {
                        throw new IllegalArgumentException("No Kafka topic mapping found for product type: " + productType);
                    }
                    exchange.getIn().setHeader("targetTopic", targetTopic);
                })
                .log("BTF Dynamic target Kafka topic: ${header.targetTopic}")
                .process(exchange -> {
                    String topic = exchange.getIn().getHeader("targetTopic", String.class);
                    java.util.Map<String, String> headers = getKafkaHeaderConfig().getHeadersForTopic(topic);
                    for (java.util.Map.Entry<String, String> entry : headers.entrySet()) {
                        exchange.getIn().setHeader(entry.getKey(), entry.getValue());
                    }
                })
                .process(exchange -> {
                    MQMessage message = exchange.getIn().getBody(MQMessage.class);
                    List<MessageProcessor> processors = getProcessorFactory().getProcessors(message.getProductType(), getBtfMapping());
                    for (MessageProcessor processor : processors) {
                        processor.process(exchange);
                    }
                })
                .log("BTF After processor chain, MQ message: ${body}")
//                .marshal().jacksonjson()  // 转换为 JSON 格式
                .toD("kafka:${header.targetTopic}?brokers=" + getKafkaBrokers())
                .log("BTF Sent message to Kafka topic ${header.targetTopic}: ${body}");
    }

    protected String getRouteId() {
        return this.getClass().getSimpleName();
    }
}
