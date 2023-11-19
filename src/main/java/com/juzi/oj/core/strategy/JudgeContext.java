package com.juzi.oj.core.strategy;

import com.juzi.oj.core.codesandbox.model.JudgeInfo;
import com.juzi.oj.model.dto.question.JudgeCase;
import com.juzi.oj.model.entity.Question;
import com.juzi.oj.model.entity.QuestionSubmitInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 判题策略上下文
 *
 * @author codejuzi
 */
@Data
@Builder
public class JudgeContext {

    /**
     * 代码沙箱执行程序信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 题目输入
     */
    private List<String> inputList;

    /**
     * 程序执行的实际输出
     */
    private List<String> outputList;

    /**
     * 题目判题用例
     */
    private List<JudgeCase> judgeCaseList;

    /**
     * 题目信息
     */
    private Question question;

    /**
     * 题目提交信息
     */
    private QuestionSubmitInfo questionSubmitInfo;

}
