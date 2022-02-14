package com.foxconn.fii.rabbitmq.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    @Bean
    public AmqpAdmin AmqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("notify.dead.message");
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("notify");
    }

    @Bean
    public TopicExchange channelExchange() {
        return new TopicExchange("channel.exchange");
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable("notify.dead.message")
                .build();
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable("notify")
                .withArgument("x-dead-letter-exchange", "notify.dead.message")
                .withArgument("x-dead-letter-routing-key", "notify.dead.message")
                .build();
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlq()).to(deadLetterExchange()).with("notify.dead.message");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("");
    }

}
