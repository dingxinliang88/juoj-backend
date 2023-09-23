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
 * Java语言判题策略实现
 *
 * @author codejuzi
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long executedMemory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long executedTime = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);

        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();

        JudgeInfoMessageEnum judgeInfoMsgEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResp = new JudgeInfo();
        judgeInfoResp.setMemory(executedMemory);
        judgeInfoResp.setTime(executedTime);

        // 判断题目限制
        String judgeConfigJson = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigJson, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if (executedMemory > needMemoryLimit) {
            judgeInfoMsgEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResp.setMessage(judgeInfoMsgEnum.getValue());
            return judgeInfoResp;
        }
        // Java 程序本身需要额外执行 5 秒钟（算是给的特权吧，哈哈哈）
        long JAVA_PROGRAM_TIME_COST = 5000L;
        if ((executedTime - JAVA_PROGRAM_TIME_COST) > needTimeLimit) {
            judgeInfoMsgEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResp.setMessage(judgeInfoMsgEnum.getValue());
            return judgeInfoResp;
        }

        // 判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (outputList.size() != inputList.size()) {
            judgeInfoMsgEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResp.setMessage(judgeInfoMsgEnum.getValue());
            return judgeInfoResp;
        }
        // 依次判断每一项输出和预期输出是否相等
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
