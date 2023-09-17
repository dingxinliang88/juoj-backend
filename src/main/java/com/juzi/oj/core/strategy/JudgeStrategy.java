package com.juzi.oj.core.strategy;

import com.juzi.oj.core.codesandbox.model.JudgeInfo;

/**
 * 判题策略
 *
 * @author codejuzi
 */
public interface JudgeStrategy {

    /**
     * 执行判题
     *
     * @param judgeContext 判题上下文对象
     * @return 判题信息
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
