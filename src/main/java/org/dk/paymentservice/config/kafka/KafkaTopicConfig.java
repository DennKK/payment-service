package org.dk.paymentservice.config.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String COMMAND_TOPIC = "payment-command";
    private static final String RESULT_TOPIC = "payment-command-result";

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        return new KafkaAdmin(configs);
    }

    @Bean
    public List<NewTopic> createTopics() {
        return List.of(
            new NewTopic(COMMAND_TOPIC, 1, (short) 1),
            new NewTopic(RESULT_TOPIC, 1, (short) 1)
        );
    }
}
