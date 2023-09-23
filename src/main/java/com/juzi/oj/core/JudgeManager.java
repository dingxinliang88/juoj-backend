package com.juzi.oj.core;

import com.juzi.oj.core.codesandbox.model.JudgeInfo;
import com.juzi.oj.core.strategy.JudgeContext;
import com.juzi.oj.core.strategy.JudgeStrategy;
import com.juzi.oj.core.strategy.JudgeStrategyFactory;
import com.juzi.oj.model.enums.QuestionSubmitLanguageEnum;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 判题管理器，简化代码
 *
 * @author codejuzi
 */
@Component
public class JudgeManager {

    /**
     * 执行判题
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        String submitLanguage = judgeContext.getQuestionSubmitInfo().getSubmitLanguage();
        QuestionSubmitLanguageEnum languageEnum = Optional
                .ofNullable(QuestionSubmitLanguageEnum.getEnumByValue(submitLanguage))
                .orElse(QuestionSubmitLanguageEnum.JAVA);
        JudgeStrategy judgeStrategy = JudgeStrategyFactory.newInstance(languageEnum);
        return judgeStrategy.doJudge(judgeContext);
    }
}
