package br.edu.infnet.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    @Value("${rabbit.queue.name}")
    private String queueName;

    @Value("${rabbit.exchange.name}")
    private String exchange;

    @Value("${rabbit.routing.key}")
    private String routingKey;

    @Bean
    public Queue notifyOrderQueue() {
        return new Queue(this.queueName);
    }

    @Bean
    public TopicExchange notifyOrderTopicExchange() {
        return new TopicExchange(this.exchange);
    }

    @Bean
    public Binding notifyOrderBinding() {
        return BindingBuilder.bind(notifyOrderQueue())
                .to(notifyOrderTopicExchange())
                .with(this.routingKey);
    }

}
