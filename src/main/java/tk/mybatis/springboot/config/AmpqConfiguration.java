package tk.mybatis.springboot.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SerializationUtils;
import tk.mybatis.springboot.ampq.BaseReceiveMessage;
import tk.mybatis.springboot.ampq.MessageEntity;

import java.io.BufferedReader;
import java.util.HashMap;


/**
 * Created by Administrator on 2016/12/23.
 */
@Configuration
@EnableRabbit
public class AmpqConfiguration implements RabbitListenerConfigurer{

    @Bean
    Queue queue(){
        return new Queue("host",true);
    }


    @Bean
    public DirectExchange marketDataExchange() {
        return new DirectExchange("host");
    }
/*
    @Bean
    public Binding marketDataBinding() {
        return BindingBuilder.bind(
                queue()).to(marketDataExchange()).with("*");
    }
*/

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(
                "host"
        );
        container.setMessageConverter(jackson2JsonMessageConverter());
        container.setMessageListener(listenerAdapter());
        return container;
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
        DefaultJackson2JavaTypeMapper defaultJackson2JavaTypeMapper = new DefaultJackson2JavaTypeMapper();

        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setClassMapper(defaultJackson2JavaTypeMapper);
        return jackson2JsonMessageConverter;
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());

        return rabbitTemplate;
    }


    @Bean
    MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println(new String(message.toString()));
            }
        });
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
    }
}
