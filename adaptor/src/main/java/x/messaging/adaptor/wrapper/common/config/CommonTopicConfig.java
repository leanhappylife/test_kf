package x.messaging.adaptor.wrapper.common.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Common topic configuration used by all product wrapper routes, including thread pool settings.
 */
@Getter
@Setter
public class CommonTopicConfig {
    private String name;                    // Topic name
    private Integer concurrency;            // Concurrency level
    private ConsumerConfig consumer;        // Consumer configuration
    private ThreadPoolConfig threadPool;    // Topic-level thread pool configuration
    private Map<String, String> eventProcessorMapping; // Event type to processor mapping

    /**
     * Nested configuration class for consumer settings.
     */
    @Getter
    @Setter
    public static class ConsumerConfig {
        private String groupId;         // Consumer group ID
        private String autoOffsetReset; // Auto offset reset policy
        private String groupIdSuffix;   // Suffix for dynamic group ID
    }
}
