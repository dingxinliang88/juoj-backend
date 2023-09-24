package com.juzi.oj.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.juzi.oj.constants.OjMQConstant.*;

/**
 * 创建测试程序用例的交换机和队列
 *
 * @author codejuzi
 */
@Slf4j
public class OjMQInitTask {

    public static void doInitMQ() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // TODO 配置远程服务MQ信息
//        connectionFactory.setHost("localhost");
        connectionFactory.setHost("8.130.102.239");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
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
            log.error("MQ Init Failed!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        doInitMQ();
    }
}
