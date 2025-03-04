package x.messaging.adaptor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for IBM MQ, mapped from application.yml under "ibm.mq".
 */
@Component
@ConfigurationProperties(prefix = "ibm.mq")
@Getter
@Setter
public class IbmMqProperties {
    private String queueManager; // Queue manager name
    private String channel;      // Channel name
    private String connName;     // Connection name
    private String user;         // Username
    private String password;     // Password
    private String queue;        // Default queue name
}
