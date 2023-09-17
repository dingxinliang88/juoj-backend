package com.juzi.oj.core.strategy;

import com.juzi.oj.core.codesandbox.model.JudgeInfo;
import com.juzi.oj.model.dto.question.JudgeCase;
import com.juzi.oj.model.entity.Question;
import com.juzi.oj.model.entity.QuestionSubmitInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author codejuzi
 */
@Data
@Builder
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmitInfo questionSubmitInfo;

}
