package com.juzi.oj.mq;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.juzi.oj.common.StatusCode;
import com.juzi.oj.exception.BusinessException;
import com.juzi.oj.model.entity.QuestionSubmitInfo;
import com.juzi.oj.model.enums.QuestionSubmitStatusEnum;
import com.juzi.oj.service.QuestionSubmitInfoService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.juzi.oj.constants.OjMQConstant.OJ_DLX_QUEUE;

/**
 * 死信队列消费消息
 *
 * @author codejuzi
 */
@Slf4j
@Component
public class OjMQDlxConsumer {

    @Resource
    private QuestionSubmitInfoService questionSubmitInfoService;

    @SneakyThrows
    @RabbitListener(
            queues = {OJ_DLX_QUEUE},
            ackMode = "MANUAL"
    )
    public void receiveMessage(String message, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {
        log.info("Dlx Queue Receive Message: {}", message);

        Long questionSubmitInfoId = Long.parseLong(message);
        QuestionSubmitInfo submitInfo = questionSubmitInfoService.getById(questionSubmitInfoId);
        if (submitInfo == null) {
            channel.basicAck(deliveryTag, false);
            throw new BusinessException(StatusCode.PARAMS_ERROR, "提交的题目信息不存在");
        }

        LambdaUpdateWrapper<QuestionSubmitInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionSubmitInfo::getId, questionSubmitInfoId)
                .set(QuestionSubmitInfo::getSubmitState, QuestionSubmitStatusEnum.FAILED.getValue());
        boolean updateRes = questionSubmitInfoService.update(updateWrapper);
        if (!updateRes) {
            log.info("Dlx Consumer Message {} Failed!", questionSubmitInfoId);
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "Error");
        }
        // 手动确认消息
        channel.basicAck(deliveryTag, false);
    }

}
