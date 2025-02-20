package x.messaging.adaptor.btf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "kafka.btf.topicmapping")
public class TopicMappingProperties {
    private Map<String, String> mapping;

    public Map<String, String> getMapping() {
        return mapping;
    }
    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }
    public String getKafkaTopic(String productType) {
        return mapping.get(productType);
    }
}
