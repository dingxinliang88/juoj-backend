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
     * 获取判题策略，执行判题
     *
     * @param judgeContext 判题信息上下文
     * @return 判题结果
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        String submitLanguage = judgeContext.getQuestionSubmitInfo().getSubmitLanguage();
        QuestionSubmitLanguageEnum languageEnum = Optional
                .ofNullable(QuestionSubmitLanguageEnum.getEnumByValue(submitLanguage))
                .orElse(QuestionSubmitLanguageEnum.DEFAULT);
        JudgeStrategy judgeStrategy = JudgeStrategyFactory.newInstance(languageEnum);
        return judgeStrategy.doJudge(judgeContext);
    }
}
