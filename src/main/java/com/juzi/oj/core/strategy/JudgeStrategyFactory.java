package com.juzi.oj.core.strategy;

import com.juzi.oj.model.enums.QuestionSubmitLanguageEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.juzi.oj.model.enums.QuestionSubmitLanguageEnum.*;

/**
 * 判题策略工厂
 *
 * @author codejuzi
 */
public class JudgeStrategyFactory {
    private JudgeStrategyFactory() {
    }

    private static final Map<Enum<QuestionSubmitLanguageEnum>, JudgeStrategy> JUDGE_STRATEGY_MAP;
    private static final DefaultJudgeStrategy DEFAULT_JUDGE_STRATEGY = new DefaultJudgeStrategy();

    static {
        JavaLanguageJudgeStrategy javaLanguageJudgeStrategy = new JavaLanguageJudgeStrategy();
        JUDGE_STRATEGY_MAP = new ConcurrentHashMap<>();
        JUDGE_STRATEGY_MAP.put(DEFAULT, DEFAULT_JUDGE_STRATEGY);
        JUDGE_STRATEGY_MAP.put(JAVA, javaLanguageJudgeStrategy);
        // TODO 实现不同语言具体的判题逻辑
        JUDGE_STRATEGY_MAP.put(CPLUSPLUS, DEFAULT_JUDGE_STRATEGY);
        JUDGE_STRATEGY_MAP.put(C_SHAPE, DEFAULT_JUDGE_STRATEGY);
        JUDGE_STRATEGY_MAP.put(GOLANG, DEFAULT_JUDGE_STRATEGY);
        JUDGE_STRATEGY_MAP.put(PYTHON, DEFAULT_JUDGE_STRATEGY);
        JUDGE_STRATEGY_MAP.put(JAVASCRIPT, DEFAULT_JUDGE_STRATEGY);
    }

    public static JudgeStrategy newInstance(QuestionSubmitLanguageEnum languageEnum) {
        assert languageEnum != null;
        return JUDGE_STRATEGY_MAP.getOrDefault(languageEnum, DEFAULT_JUDGE_STRATEGY);
    }
}
