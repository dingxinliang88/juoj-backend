package com.juzi.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juzi.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.juzi.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.juzi.oj.model.entity.QuestionSubmitInfo;
import com.juzi.oj.model.entity.User;
import com.juzi.oj.model.vo.QuestionSubmitInfoVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author codejuzi
 */
public interface QuestionSubmitInfoService extends IService<QuestionSubmitInfo> {

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param request                  http request
     * @return 题目提交信息id
     */
    Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request);

    /**
     * 分页获取题目提交信息
     *
     * @param questionSubmitQueryRequest 题目提交信息查询请求
     * @param request                    http request
     * @return question submit info vo
     */
    Page<QuestionSubmitInfoVO> listQuestionSubmitInfoVOByPage(QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request);

    // region util function

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest 题目提交信息查询请求
     * @return query wrapper
     */
    QueryWrapper<QuestionSubmitInfo> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmitInfo 题目提交信息
     * @param loginUser          登录用户信息
     * @return question submit vo
     */
    QuestionSubmitInfoVO getQuestionSubmitVO(QuestionSubmitInfo questionSubmitInfo, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitInfoPage 题目提交信息分页信息
     * @param loginUser              登录用户信息
     * @return question submit info vo page
     */
    Page<QuestionSubmitInfoVO> getQuestionSubmitVOPage(Page<QuestionSubmitInfo> questionSubmitInfoPage, User loginUser);
    // region
}
