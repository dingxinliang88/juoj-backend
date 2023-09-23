package com.juzi.oj.core.strategy;

import com.juzi.oj.model.enums.QuestionSubmitLanguageEnum;

/**
 * 判题策略工厂
 *
 * @author codejuzi
 */
public class JudgeStrategyFactory {
    private JudgeStrategyFactory() {
    }

    public static JudgeStrategy newInstance(QuestionSubmitLanguageEnum languageEnum) {
        JudgeStrategy judgeStrategy;
        switch (languageEnum) {
            case JAVA:
                judgeStrategy = new JavaLanguageJudgeStrategy();
                break;
            // TODO 实现不同语言的判题模块，生成对应的策略
            case CPLUSPLUS:
            case C_SHAPE:
            case GOLANG:
            case PYTHON:
            case JAVASCRIPT:
            default:
                judgeStrategy = new DefaultJudgeStrategy();
        }
        return judgeStrategy;
    }
}
