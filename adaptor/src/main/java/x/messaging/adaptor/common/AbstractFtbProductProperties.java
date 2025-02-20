package x.messaging.adaptor.common;

import java.util.Map;

public abstract class AbstractFtbProductProperties {
    // Kafka broker 地址（产品通用）
    private String broker;

    // 产品对应的主题配置，key 为主题名称，
    // value 为包含 headers、processor 与 concurrency 配置的对象
    private Map<String, TopicConfig> topics;

    public static class TopicConfig {
        private String headers;
        private String processor;
        private Integer concurrency;  // 并发消费者数

        public String getHeaders() {
            return headers;
        }
        public void setHeaders(String headers) {
            this.headers = headers;
        }
        public String getProcessor() {
            return processor;
        }
        public void setProcessor(String processor) {
            this.processor = processor;
        }
        public Integer getConcurrency() {
            return concurrency;
        }
        public void setConcurrency(Integer concurrency) {
            this.concurrency = concurrency;
        }
    }

    public String getBroker() {
        return broker;
    }
    public void setBroker(String broker) {
        this.broker = broker;
    }
    public Map<String, TopicConfig> getTopics() {
        return topics;
    }
    public void setTopics(Map<String, TopicConfig> topics) {
        this.topics = topics;
    }
}
