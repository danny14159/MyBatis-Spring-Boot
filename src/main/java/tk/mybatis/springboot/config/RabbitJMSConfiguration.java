package tk.mybatis.springboot.config;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

/**
 * Created by Administrator on 2016/12/28.
 */
@Configuration
public class RabbitJMSConfiguration {

    //@Bean
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public ConnectionFactory jmsConnectionFactory() {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        connectionFactory.setUsername("nsc");
        connectionFactory.setPassword("newtouch");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPort(5672);
        connectionFactory.setHost("192.168.99.100");
        return connectionFactory;
    }

    @Bean
    public Destination jmsDestination() {
        RMQDestination jmsDestination = new RMQDestination();
        jmsDestination.setDestinationName("myQueue");
        jmsDestination.setAmqp(true);
        jmsDestination.setAmqpQueueName("host");
        jmsDestination.setAmqpExchangeName("ex1");
        jmsDestination.setAmqpRoutingKey("log");
        jmsDestination.setQueue(true);
        return jmsDestination;
    }
}
