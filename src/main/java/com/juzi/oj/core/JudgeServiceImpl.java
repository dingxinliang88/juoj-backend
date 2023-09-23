package com.juzi.oj.core;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.juzi.oj.common.StatusCode;
import com.juzi.oj.core.codesandbox.CodeSandbox;
import com.juzi.oj.core.codesandbox.CodeSandboxFactory;
import com.juzi.oj.core.codesandbox.CodeSandboxProxy;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeRequest;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeResponse;
import com.juzi.oj.core.codesandbox.model.JudgeInfo;
import com.juzi.oj.core.strategy.JudgeContext;
import com.juzi.oj.exception.BusinessException;
import com.juzi.oj.model.dto.question.JudgeCase;
import com.juzi.oj.model.entity.Question;
import com.juzi.oj.model.entity.QuestionSubmitInfo;
import com.juzi.oj.model.enums.QuestionSubmitStatusEnum;
import com.juzi.oj.service.QuestionService;
import com.juzi.oj.service.QuestionSubmitInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author codejuzi
 */
@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitInfoService questionSubmitInfoService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String judgeType;

    @Override
    public QuestionSubmitInfo doJudge(Long questionSubmitId) {
        // 1、得到题目提交信息
        QuestionSubmitInfo submitInfo = questionSubmitInfoService.getById(questionSubmitId);
        if (submitInfo == null) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR, "还未提交");
        }
        // 获取题目的信息
        Long questionId = submitInfo.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 2、题目提交状态不在等待中
        if (!QuestionSubmitStatusEnum.WAITING.getValue().equals(submitInfo.getSubmitState())) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "题目正在判题中");
        }

        // 3、更改题目状态为 判题中
        LambdaUpdateWrapper<QuestionSubmitInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionSubmitInfo::getId, questionSubmitId)
                .set(QuestionSubmitInfo::getSubmitState, QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean updateRes = questionSubmitInfoService.update(updateWrapper);
        if (!updateRes) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "题目提交信息状态修改失败");
        }

        // 4、调用沙箱，得到执行结果
        List<JudgeCase> judgeCaseList = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(submitInfo.getSubmitCode())
                .language(submitInfo.getSubmitLanguage())
                .inputList(inputList)
                .build();
        CodeSandbox codeSandbox = new CodeSandboxProxy(CodeSandboxFactory.newInstance(judgeType));
        ExecuteCodeResponse executeCodeResponse = codeSandbox.execute(executeCodeRequest);

        // 5、根据沙箱的执行状态，设置题目的判题状态和信息
        List<String> outputList = executeCodeResponse.getOutputList();
        JudgeContext judgeContext = JudgeContext.builder()
                .judgeInfo(executeCodeResponse.getJudgeInfo())
                .inputList(inputList)
                .outputList(outputList)
                .judgeCaseList(judgeCaseList)
                .question(question)
                .questionSubmitInfo(submitInfo)
                .build();

        // 6、执行判题
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 7、修改判题结果
        updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionSubmitInfo::getId, questionSubmitId)
                .set(QuestionSubmitInfo::getSubmitState, QuestionSubmitStatusEnum.SUCCEED.getValue())
                .set(QuestionSubmitInfo::getJudgeInfo, JSONUtil.toJsonStr(judgeInfo));
        updateRes = questionSubmitInfoService.update(updateWrapper);
        if (!updateRes) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "题目提交信息状态修改失败");
        }

        return questionSubmitInfoService.getById(questionId);
    }
}
