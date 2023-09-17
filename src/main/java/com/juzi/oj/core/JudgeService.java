package com.juzi.oj.core;

import com.juzi.oj.model.entity.QuestionSubmitInfo;

/**
 * @author codejuzi
 */
public interface JudgeService {

    /**
     * 判题
     *
     * @param questionSubmitId 题目提交信息id
     * @return 题目提交信息汇总
     */
    @SuppressWarnings("UnusedReturnValue")
    QuestionSubmitInfo doJudge(Long questionSubmitId);
}
