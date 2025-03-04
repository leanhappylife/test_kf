package x.messaging.adaptor.common;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to parse Kafka header strings (e.g., "key1=value1,key2=value2") into a Map.
 */
@Slf4j
public class KafkaHeaderParser {
    public static Map<String, String> parseHeaders(String headerStr) {
        Map<String, String> headers = new HashMap<>();
        if (headerStr != null && !headerStr.trim().isEmpty()) {
            String[] pairs = headerStr.split(",");
            for (String pair : pairs) {
                String[] keyVal = pair.split("=");
                if (keyVal.length == 2) {
                    headers.put(keyVal[0].trim(), keyVal[1].trim());
                } else {
                    log.warn("Invalid header format: '{}'", pair);
                }
            }
        }
        return headers;
    }
}
