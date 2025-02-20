package x.messaging.adaptor.common;

import java.util.Map;

public abstract class AbstractProcessorMappingProperties {
    private Map<String, String> mapping;

    public Map<String, String> getMapping() {
        return mapping;
    }
    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }
}
