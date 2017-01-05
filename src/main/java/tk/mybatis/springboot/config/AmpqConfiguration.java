package tk.mybatis.springboot.config;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.*;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SerializationUtils;
import tk.mybatis.springboot.ampq.BaseReceiveMessage;
import tk.mybatis.springboot.ampq.MessageEntity;

import java.io.BufferedReader;
import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * Created by Administrator on 2016/12/23.
 */
@Configuration
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
       /* container.setAdviceChain(new Advice[]{
                (MethodBeforeAdvice) (method, args, target) ->

                    System.out.println(method.getName())

        });*/
        container.setMessageConverter(jackson2JsonMessageConverter());
        //container.setMessageListener(listenerAdapter());
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
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,Jackson2JsonMessageConverter jackson2JsonMessageConverter){

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        rabbitTemplate.setBeforePublishPostProcessors(new MessagePostProcessor(){
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                tk.mybatis.springboot.ampq.data.BaseReceiveMessage o = (tk.mybatis.springboot.ampq.data.BaseReceiveMessage)jackson2JsonMessageConverter.fromMessage(message);

                System.out.println("before:"+o.getData().getMessage());
                return message;
            }
        });
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
