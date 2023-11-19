package com.juzi.oj.core.strategy;

import cn.hutool.json.JSONUtil;
import com.juzi.oj.core.codesandbox.model.JudgeInfo;
import com.juzi.oj.model.dto.question.JudgeCase;
import com.juzi.oj.model.dto.question.JudgeConfig;
import com.juzi.oj.model.entity.Question;

import java.util.List;
import java.util.Optional;

import static com.juzi.oj.model.enums.JudgeInfoMessageEnum.*;

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

        // 题目相关的信息：运行程序输入、运行程序输出、题目信息、判题用例
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();

        // 响应判题信息
        JudgeInfo judgeInfoResp = new JudgeInfo();
        judgeInfoResp.setMemory(executedMemory);
        judgeInfoResp.setTime(executedTime);

        // 判断题目限制
        String judgeConfigJson = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigJson, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if (executedMemory > needMemoryLimit) {
            // 超出内存限制
            judgeInfoResp.setMessage(MEMORY_LIMIT_EXCEEDED.getValue());
            return judgeInfoResp;
        }
        if (executedTime > needTimeLimit) {
            // 超出时间限制
            judgeInfoResp.setMessage(TIME_LIMIT_EXCEEDED.getValue());
            return judgeInfoResp;
        }

        // 判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (outputList.size() != inputList.size()) {
            judgeInfoResp.setMessage(WRONG_ANSWER.getValue());
            return judgeInfoResp;
        }
        // 依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoResp.setMessage(WRONG_ANSWER.getValue());
                return judgeInfoResp;
            }
        }
        // AC
        judgeInfoResp.setMessage(ACCEPTED.getValue());
        return judgeInfoResp;
    }
}
