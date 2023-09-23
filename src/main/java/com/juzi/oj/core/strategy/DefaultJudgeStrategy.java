package com.juzi.oj.core.strategy;

import cn.hutool.json.JSONUtil;
import com.juzi.oj.core.codesandbox.model.JudgeInfo;
import com.juzi.oj.model.dto.question.JudgeCase;
import com.juzi.oj.model.dto.question.JudgeConfig;
import com.juzi.oj.model.entity.Question;
import com.juzi.oj.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;

/**
 * 默认判题策略实现
 *
 * @author codejuzi
 */
public class DefaultJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long executedMemory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long executedTime = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);

        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();

        // 返回结果
        JudgeInfoMessageEnum judgeInfoMsgEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResp = new JudgeInfo();
        judgeInfoResp.setMemory(executedMemory);
        judgeInfoResp.setTime(executedTime);

        // 1、判题题目的限制是否符合要求, 获取题目配置（时间、内存等）
        String judgeConfigJson = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigJson, JudgeConfig.class);
        Long needTimeLimit = judgeConfig.getTimeLimit();
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        if (executedMemory > needMemoryLimit) {
            judgeInfoMsgEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResp.setMessage(judgeInfoMsgEnum.getValue());
            return judgeInfoResp;
        }
        if (executedTime > needTimeLimit) {
            judgeInfoMsgEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResp.setMessage(judgeInfoMsgEnum.getValue());
            return judgeInfoResp;
        }

        // 2、判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (outputList.size() != inputList.size()) {
            judgeInfoMsgEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResp.setMessage(judgeInfoMsgEnum.getValue());
            return judgeInfoResp;
        }

        // 3、依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoMsgEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResp.setMessage(judgeInfoMsgEnum.getValue());
                return judgeInfoResp;
            }
        }

        judgeInfoResp.setMessage(judgeInfoMsgEnum.getValue());
        return judgeInfoResp;
    }
}
