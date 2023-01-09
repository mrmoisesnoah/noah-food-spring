package br.com.alurafood.avaliacao.avaliacao.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvaliacaoAMQPConfiguration {
    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return  new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return  rabbitTemplate;
    }

    @Bean
    public Queue queueDetailsEvaluation() {
        return QueueBuilder
                .nonDurable("payment.details-evaluation")
                .deadLetterExchange("payments.dlx")
                .build();
    }

    @Bean
    public Queue queueDlQdetailsEvaluation() {
        return QueueBuilder
                .nonDurable("payment.details-evaluation-dlq")
                .build();
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return ExchangeBuilder
                .fanoutExchange("payment.ex")
                .build();
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return ExchangeBuilder
                .fanoutExchange("payment.dlx")
                .build();
    }

    @Bean
    public Binding bindPaymentRequest() {
        return BindingBuilder
                .bind(queueDetailsEvaluation())
                .to(fanoutExchange());
    }

    @Bean
    public Binding bindDlXpaymentRequest() {
        return BindingBuilder
                .bind(queueDlQdetailsEvaluation())
                .to(deadLetterExchange());
    }

    @Bean
    public RabbitAdmin createRabbitAdmin(ConnectionFactory conn) {
        return new RabbitAdmin(conn);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> adminInit(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

}
