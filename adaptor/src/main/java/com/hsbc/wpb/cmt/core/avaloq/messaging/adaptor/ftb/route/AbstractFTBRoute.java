package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.ftb.route;

import org.apache.camel.builder.RouteBuilder;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.ftb.config.FtbKafkaHeaderConfig;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.MessageProcessor;
import com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.common.ProcessorFactory;
import java.util.List;
import java.util.Map;

public abstract class AbstractFTBRoute extends RouteBuilder {

    // 子类需提供：
    protected abstract String getTopics();
    protected abstract String getKafkaBrokers();
    protected abstract String getDestinationQueue();
    protected abstract ProcessorFactory getProcessorFactory();
    protected abstract FtbKafkaHeaderConfig getKafkaHeaderConfig();
    protected abstract com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.ftb.config.FtbProcessorMappingProperties getFtbMappingProperties();

    // 如果子类需要指定并发消费者数，则重写此方法，返回非 null 值；默认返回 null（使用 1 作为默认线程数）
    protected Integer getTopicConcurrency() {
        return null;
    }

    @Override
    public void configure() throws Exception {
        // 构造 Kafka URI，不包含并发参数（因为 Camel Kafka 4.4 中不支持）
        String kafkaUri = "kafka:" + getTopics() + "?brokers=" + getKafkaBrokers();

        // 从配置中读取并发数，如果未配置则默认使用 1
        Integer concurrency = getTopicConcurrency();
        if (concurrency == null) {
            concurrency = 1;
        }

        onException(Exception.class)
                .maximumRedeliveries(3)
                .redeliveryDelay(2000)
                .retryAttemptedLogLevel(org.apache.camel.LoggingLevel.ERROR)
                .handled(true)
                .log("FTB Route Exception (${routeId}): ${exception.message}")
                .to("jms:queue:errorQueue")
                .stop();

        from(kafkaUri)
                .routeId(getRouteId())
                // 使用 threads() DSL 实现并发处理
                .threads(concurrency)
                .log("FTB Received Kafka message (Topic: ${header.kafka.TOPIC}): ${body}")
                .process(exchange -> {
                    // 获取当前消息所属的主题
                    String topic = exchange.getIn().getHeader("kafka.TOPIC", String.class);
                    // 设置 header：从配置中获取该主题对应的 header 信息
                    Map<String, String> headers = getKafkaHeaderConfig().getHeadersForTopic(topic);
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        exchange.getIn().setHeader(entry.getKey(), entry.getValue());
                    }
                    // 调用 processor 链处理消息
                    List<MessageProcessor> processors = getProcessorFactory().getProcessors(topic, getFtbMappingProperties().getMapping());
                    for (MessageProcessor processor : processors) {
                        processor.process(exchange);
                    }
                })
                // 将处理后的消息转换为 XML
//                .marshal().jacksonxml()
                // 发送到 MQ 队列
                .to("jms:queue:" + getDestinationQueue())
                .log("FTB Sent message to MQ (Queue: " + getDestinationQueue() + "): ${body}");
    }

    protected String getRouteId() {
        return this.getClass().getSimpleName();
    }
}
