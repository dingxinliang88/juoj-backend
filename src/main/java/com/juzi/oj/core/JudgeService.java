package com.juzi.oj.core;

import com.juzi.oj.model.entity.QuestionSubmitInfo;

/**
 * 判题服务
 *
 * @author codejuzi
 */
public interface JudgeService {

    /**
     * 执行判题
     *
     * @param questionSubmitId 题目提交信息id
     * @return 题目提交信息汇总
     */
    @SuppressWarnings("UnusedReturnValue")
    QuestionSubmitInfo doJudge(Long questionSubmitId);
}
