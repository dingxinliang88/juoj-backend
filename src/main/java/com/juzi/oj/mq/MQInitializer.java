package com.juzi.oj.mq;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author codejuzi
 */
@Component
public class MQInitializer {

    @PostConstruct
    public void doInitMQ() {
        OjMQInitTask.doInitMQ();
    }

}
