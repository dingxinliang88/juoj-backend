package com.juzi.oj.mq;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author codejuzi
 */
@Component
public class MQInitializer {

    @Resource
    private OjMQInitTask mqInitTask;

    @PostConstruct
    public void doInitMQ() {
        mqInitTask.doInitMQ();
    }

}
