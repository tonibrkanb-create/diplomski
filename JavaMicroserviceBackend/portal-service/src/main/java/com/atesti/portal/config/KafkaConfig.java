package com.atesti.portal.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic ponudaEventsTopic() {
        return TopicBuilder.name("ponuda-events").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic recenzijaEventsTopic() {
        return TopicBuilder.name("recenzija-events").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic obavijestEventsTopic() {
        return TopicBuilder.name("obavijest-events").partitions(3).replicas(1).build();
    }
}
