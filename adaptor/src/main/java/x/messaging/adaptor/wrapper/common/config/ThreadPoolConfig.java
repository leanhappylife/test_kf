package x.messaging.adaptor.wrapper.common.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration class for thread pool settings.
 */
@Getter
@Setter
public class ThreadPoolConfig {
    private Integer coreSize;       // Core thread pool size
    private Integer maxSize;        // Maximum thread pool size
    private Long keepAliveTime;     // Keep-alive time for idle threads (in seconds)
    private Integer queueCapacity;  // Capacity of the task queue
}
