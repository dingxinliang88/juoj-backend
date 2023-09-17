package com.juzi.oj.core;

import com.juzi.oj.core.codesandbox.model.JudgeInfo;
import com.juzi.oj.core.strategy.DefaultJudgeStrategy;
import com.juzi.oj.core.strategy.JavaLanguageJudgeStrategy;
import com.juzi.oj.core.strategy.JudgeContext;
import com.juzi.oj.core.strategy.JudgeStrategy;
import com.juzi.oj.model.enums.QuestionSubmitLanguageEnum;
import org.springframework.stereotype.Component;

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
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (QuestionSubmitLanguageEnum.JAVA.getValue().equals(submitLanguage)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
