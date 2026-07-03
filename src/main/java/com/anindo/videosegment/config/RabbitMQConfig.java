package com.anindo.videosegment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "video-processing-queue";
    public static final String EXCHANGE_NAME = "video-exchange";
    public static final String ROUTING_KEY = "video.routing.key";

    @Bean
    public Queue videoQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange videoExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue videoQueue, DirectExchange videoExchange) {
        return BindingBuilder.bind(videoQueue).to(videoExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new JacksonJsonMessageConverter(String.valueOf(objectMapper));
    }
}