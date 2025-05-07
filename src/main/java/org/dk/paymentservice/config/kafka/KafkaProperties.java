package org.dk.paymentservice.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {
    private List<TopicConfig> topics;
    private List<String> bootstrapServers;
    private Map<String, String> adminProperties;

    @Data
    public static class TopicConfig {
        private String name;
        private int partitions;
        private short replicationFactor;
    }
}
