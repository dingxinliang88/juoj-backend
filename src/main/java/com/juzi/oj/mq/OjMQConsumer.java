package com.juzi.oj.mq;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.juzi.oj.common.StatusCode;
import com.juzi.oj.core.JudgeService;
import com.juzi.oj.core.codesandbox.model.JudgeInfo;
import com.juzi.oj.exception.BusinessException;
import com.juzi.oj.model.entity.Question;
import com.juzi.oj.model.entity.QuestionSubmitInfo;
import com.juzi.oj.model.enums.JudgeInfoMessageEnum;
import com.juzi.oj.service.QuestionService;
import com.juzi.oj.service.QuestionSubmitInfoService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.io.IOException;

import static com.juzi.oj.constants.OjMQConstant.OJ_QUEUE_NAME;

/**
 * @author codejuzi
 */
@Slf4j
@Component
public class OjMQConsumer {

    @Resource
    private JudgeService judgeService;

    @Resource
    private QuestionSubmitInfoService questionSubmitInfoService;

    @Resource
    private QuestionService questionService;

    /**
     * 消费者接收并消费消息，如果消费失败，进入死信队列
     *
     * @param message     消息体
     * @param channel     channel
     * @param deliveryTag 消息Tag（ID）
     */
    @SneakyThrows
    @RabbitListener(
            queues = {OJ_QUEUE_NAME},
            ackMode = "MANUAL"
    )
    public void receiveMessage(String message, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {

        try {
            log.info("MQ Consumer Receive Message: {}", message);

            if (message == null) {
                // 消息为空，报错
                channel.basicAck(deliveryTag, false);
                throw new BusinessException(StatusCode.PARAMS_ERROR, "消息为空");
            }

            Long questionSubmitInfoId = Long.parseLong(message);
            // 判题服务
            judgeService.doJudge(questionSubmitInfoId);
            // 获取判题结果
            QuestionSubmitInfo submitInfo = questionSubmitInfoService.getById(questionSubmitInfoId);
            String judgeInfoJson = submitInfo.getJudgeInfo();
            JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoJson, JudgeInfo.class);

            String judgeInfoMessage = judgeInfo.getMessage();
            if (!JudgeInfoMessageEnum.ACCEPTED.getValue().equals(judgeInfoMessage)) {
                // 说明提交的代码有误
                channel.basicAck(deliveryTag, false);
                return;
            }

            Long questionId = submitInfo.getQuestionId();
            Question question = questionService.getById(questionId);

            // TODO: 2023/9/22 考虑是否需要加重量级锁，还是允许一定的误差
            LambdaUpdateWrapper<Question> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Question::getId, questionId)
                    .set(Question::getAcNum, question.getAcNum() + 1);
            boolean updateRes = questionService.update(updateWrapper);
            if (!updateRes) {
                throw new BusinessException(StatusCode.SYSTEM_ERROR, "数据保存失败");
            }

            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            // 消息接收失败，发送到死信队列
            channel.basicNack(deliveryTag, false, false);
        }

    }
}
