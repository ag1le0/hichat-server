package com.foxconn.fii.rabbitmq.service;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;

import java.util.Properties;

public interface RabbitmqService {

    void createNotificationSubQueue(String routingKey);

    Properties getQueueProperties(String queueName);

    Queue buildQueue(String queueName);

    Queue createQueue(String queueName);

    Queue createQueue(String queueName, String subQueueName);

    void deleteQueue(String queueName);


    Exchange buildExchange(String exchangeName);

    Exchange createExchange(String exchangeName);

    void deleteExchange(String exchangeName);


    void binding(String exchangeName, String queueName, String routingKey);

    void binding(String exchangeName, Queue queue, String routingKey);

    void binding(Exchange exchange, Queue queue, String routingKey);

    void binding(Exchange exchange, String queueName, String routingKey);

    void unbinding(String exchangeName, String queueName, String routingKey);

    void unbinding(Exchange exchangeName, Queue queue, String routingKey);


    void publish(String exchange, Object data);

    void publish(String exchange, String routingKey, Object data);
}
