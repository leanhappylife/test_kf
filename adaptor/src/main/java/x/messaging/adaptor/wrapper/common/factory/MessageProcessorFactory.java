package x.messaging.adaptor.wrapper.common.factory;

import org.springframework.stereotype.Component;
import x.messaging.adaptor.wrapper.common.MessageProcessor;

import java.util.Map;

/**
 * Factory to instantiate message processors based on event type.
 */
@Component
public class MessageProcessorFactory {

    private final Map<String, MessageProcessor> processorBeans;

    public MessageProcessorFactory(Map<String, MessageProcessor> processorBeans) {
        this.processorBeans = processorBeans;
    }

    public MessageProcessor getProcessor(String eventType, Map<String, String> eventProcessorMapping) {
        String processorBeanName = eventProcessorMapping.getOrDefault(eventType, "defaultMessageProcessor");
        return processorBeans.getOrDefault(processorBeanName, processorBeans.get("defaultMessageProcessor"));
    }
}
