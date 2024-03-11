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

    @Value("${rabbit.email_notification.queue.name}")
    private String emailNotificationQueue;

    @Value("${rabbit.email_notification.exchange.name}")
    private String emailNotificationExchange;

    @Value("${rabbit.email_notification.routing.key}")
    private String emailNotificationRoutingKey;

    @Bean
    public Queue recoverPasswordQueue() {
        return new Queue(this.emailNotificationQueue);
    }

    @Bean
    public TopicExchange recoverPasswordTopicExchange() {
        return new TopicExchange(this.emailNotificationExchange);
    }

    @Bean
    public Binding recoverPasswordBinding() {
        return BindingBuilder.bind(recoverPasswordQueue())
                .to(recoverPasswordTopicExchange())
                .with(this.emailNotificationRoutingKey);
    }

}
