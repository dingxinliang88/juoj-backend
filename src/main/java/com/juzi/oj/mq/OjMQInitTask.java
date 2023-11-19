package com.juzi.oj.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.juzi.oj.constants.OjMQConstant.*;

/**
 * 创建测试程序用例的交换机和队列
 *
 * @author codejuzi
 */
@Component
@Slf4j
@Data
@ConfigurationProperties("spring.rabbitmq")
public class OjMQInitTask {

    private String host;

    private Integer port;

    private String username;

    private String password;

    public void doInitMQ() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        try (Connection connection = connectionFactory.newConnection()) {
            Channel channel = connection.createChannel();
            // 创建交换机
            channel.exchangeDeclare(OJ_EXCHANGE_NAME, OJ_DIRECT_EXCHANGE);

            // 创建队列，绑定死信交换机
            Map<String, Object> queueArgs = new HashMap<>();
            queueArgs.put("x-dead-letter-exchange", OJ_DLX_EXCHANGE);
            queueArgs.put("x-dead-letter-routing-key", OJ_DLX_ROUTING_KEY);
            channel.queueDeclare(OJ_QUEUE_NAME, true, false, false, queueArgs);

            // 队列绑定交换机
            channel.queueBind(OJ_QUEUE_NAME, OJ_EXCHANGE_NAME, OJ_ROUTING_KEY);

            // 创建死信交换机、死信队列，绑定二者
            channel.exchangeDeclare(OJ_DLX_EXCHANGE, OJ_DIRECT_EXCHANGE);
            channel.queueDeclare(OJ_DLX_QUEUE, true, false, false, null);
            channel.queueBind(OJ_DLX_QUEUE, OJ_DLX_EXCHANGE, OJ_DLX_ROUTING_KEY);

            log.info("MQ Init Successful!");

        } catch (Exception e) {
            log.error("MQ Init Failed!", e);
        }
    }

    public static void main(String[] args) {
        new OjMQInitTask().doInitMQ();
    }
}
