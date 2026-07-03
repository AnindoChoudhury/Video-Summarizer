package com.anindo.videosegment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public MessageConverter jsonMessageConverter() {
        // No ObjectMapper parameters! It creates its own isolated, safe environment.
        return new Jackson2JsonMessageConverter();
    }
}