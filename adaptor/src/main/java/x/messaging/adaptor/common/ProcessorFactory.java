package x.messaging.adaptor.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ProcessorFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public List<MessageProcessor> getProcessors(String topic, Map<String, String> mapping) {
        List<MessageProcessor> processors = new ArrayList<>();
        if (mapping != null && mapping.containsKey(topic)) {
            String processorNames = mapping.get(topic);
            String[] names = processorNames.split(",");
            for (String name : names) {
                name = name.trim();
                MessageProcessor processor = applicationContext.getBean(name, MessageProcessor.class);
                if (processor != null) {
                    processors.add(processor);
                }
            }
        }
        if (processors.isEmpty()) {
            processors.add(applicationContext.getBean("genericProcessor", MessageProcessor.class));
        }
        return processors;
    }
}
