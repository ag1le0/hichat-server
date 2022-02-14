package com.foxconn.fii.rabbitmq.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.rabbitmq.service.RabbitmqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service
public class RabbitmqServiceImpl implements RabbitmqService {

    @Autowired
    @Qualifier("exchange")
    private DirectExchange exchange;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void createNotificationSubQueue(String routingKey) {
        Queue queue = QueueBuilder.durable("notification." + routingKey)
                .withArgument("x-dead-letter-exchange", "notification.dead.message")
                .withArgument("x-dead-letter-routing-key", "notification.dead.message")
                .build();

        amqpAdmin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);

        amqpAdmin.declareBinding(binding);
    }

    @Override
    public Properties getQueueProperties(String queueName) {
        return amqpAdmin.getQueueProperties(queueName);
    }

    @Override
    public Queue createQueue(String queueName) {
        Queue queue = buildQueue(queueName);

        amqpAdmin.declareQueue(queue);
        return queue;
    }

    @Override
    public Queue buildQueue(String queueName) {
        return QueueBuilder
                .durable(queueName)
//                .autoDelete()
                .build();
    }

    @Override
    public Queue createQueue(String queueName, String subQueueName) {
        String name = String.format("notification.%s.%s", queueName, subQueueName);
        return createQueue(name);
    }

    @Override
    public void deleteQueue(String queueName) {
        amqpAdmin.deleteQueue(queueName);
    }

    @Override
    public Exchange buildExchange(String exchangeName) {
        return ExchangeBuilder.topicExchange(exchangeName).build();
    }

    @Override
    public Exchange createExchange(String exchangeName) {
        Exchange exchange = buildExchange(exchangeName);
        amqpAdmin.declareExchange(exchange);
        return exchange;
    }

    @Override
    public void deleteExchange(String exchangeName) {
        amqpAdmin.deleteExchange(exchangeName);
    }

    @Override
    public void binding(String exchangeName, String queueName, String routingKey) {
        Exchange exchange = ExchangeBuilder.topicExchange(exchangeName).build();
        Queue queue = buildQueue(queueName);
        binding(exchange, queue, routingKey);
    }

    @Override
    public void binding(String exchangeName, Queue queue, String routingKey) {
        Exchange exchange = ExchangeBuilder.topicExchange(exchangeName).build();
        binding(exchange, queue, routingKey);
    }

    @Override
    public void binding(Exchange exchange, String queueName, String routingKey) {
        Queue queue = buildQueue(queueName);
        binding(exchange, queue, routingKey);
    }

    @Override
    public void binding(Exchange exchange, Queue queue, String routingKey) {
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
        amqpAdmin.declareBinding(binding);
    }

    @Override
    public void unbinding(String exchangeName, String queueName, String routingKey) {
        Exchange exchange = buildExchange(exchangeName);
        Queue queue = buildQueue(queueName);
        unbinding(exchange, queue, routingKey);
    }

    @Override
    public void unbinding(Exchange exchange, Queue queue, String routingKey) {
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
        amqpAdmin.removeBinding(binding);
    }

    @Override
    public void publish(String exchange, String routingKey, Object data) {
        try {
            log.info("### publish {} to {} with {}", data, exchange, routingKey);
            amqpTemplate.convertAndSend(exchange, routingKey, objectMapper.writeValueAsString(data));
        } catch (Exception e) {
            log.error("### send error", e);
        }
    }

    @Override
    public void publish(String exchange, Object data) {
        try {
            log.info("### publish {} to {}", data, exchange);
            amqpTemplate.convertAndSend(exchange, "", objectMapper.writeValueAsString(data));
        } catch (Exception e) {
            log.error("### publish error", e);
        }
    }
}
