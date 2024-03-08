package br.edu.infnet.notification.config;

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

    @Value("${rabbit.recover_password.queue.name}")
    private String recoverPasswordQueue;

    @Value("${rabbit.recover_password.exchange.name}")
    private String recoverPasswordExchange;

    @Value("${rabbit.recover_password.routing.key}")
    private String recoverPasswordRoutingKey;

    @Value("${rabbit.notify_order.queue.name}")
    private String notifyOrderQueue;

    @Value("${rabbit.notify_order.exchange.name}")
    private String notifyOrderExchange;

    @Value("${rabbit.notify_order.routing.key}")
    private String notifyOrderRoutingKey;

    @Bean
    public Queue recoverPasswordQueue() {
        return new Queue(this.recoverPasswordQueue);
    }

    @Bean
    public TopicExchange recoverPasswordTopicExchange() {
        return new TopicExchange(this.recoverPasswordExchange);
    }

    @Bean
    public Binding recoverPasswordBinding() {
        return BindingBuilder.bind(recoverPasswordQueue())
                .to(recoverPasswordTopicExchange())
                .with(this.recoverPasswordRoutingKey);
    }

    @Bean
    public Queue notifyOrderQueue() {
        return new Queue(this.notifyOrderQueue);
    }

    @Bean
    public TopicExchange notifyOrderTopicExchange() {
        return new TopicExchange(this.notifyOrderExchange);
    }

    @Bean
    public Binding notifyOrderBinding() {
        return BindingBuilder.bind(notifyOrderQueue())
                .to(notifyOrderTopicExchange())
                .with(this.notifyOrderRoutingKey);
    }

}
