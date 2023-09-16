package com.juzi.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juzi.oj.common.DeleteRequest;
import com.juzi.oj.model.dto.question.QuestionAddRequest;
import com.juzi.oj.model.dto.question.QuestionQueryRequest;
import com.juzi.oj.model.dto.question.QuestionUpdateRequest;
import com.juzi.oj.model.entity.Question;
import com.juzi.oj.model.entity.User;
import com.juzi.oj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author codejuzi
 */
public interface QuestionService extends IService<Question> {

    /**
     * 添加题目
     *
     * @param questionAddRequest 题目添加请求
     * @param request            http request
     * @return new  question id
     */
    Long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request);

    /**
     * 删除题目
     *
     * @param deleteRequest 删除请求
     * @param request       http request
     * @return true - 删除成功
     */
    Boolean deleteQuestion(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 管理员修改题目信息
     *
     * @param questionUpdateRequest 修改题目请求信息
     * @return true - 修改成功
     */
    Boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest);

    /**
     * 获取修改包装类
     *
     * @param questionUpdateRequest 题目修改请求
     * @return update wrapper
     */
    LambdaUpdateWrapper<Question> getUpdateWrapper(QuestionUpdateRequest questionUpdateRequest);

    /**
     * 根据id获取题目VO信息
     *
     * @param questionId 题目id
     * @param request    http request
     * @return question vo
     */
    QuestionVO getQuestionVOById(Long questionId, HttpServletRequest request);

    /**
     * 根据id获取题目信息
     *
     * @param questionId 题目id
     * @param request    http request
     * @return question info
     */
    Question getQuestionById(Long questionId, HttpServletRequest request);

    /**
     * 分页获取题目VO信息
     *
     * @param questionQueryRequest 题目查询请求
     * @param request              http request
     * @return question vo page info
     */
    Page<QuestionVO> listQuestionVOByPage(QuestionQueryRequest questionQueryRequest, HttpServletRequest request);

    /**
     * 分页获取题目信息
     *
     * @param questionQueryRequest 题目查询请求
     * @return question page info
     */
    Page<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest);

    /**
     * 分页获取用户自己创建的题目信息
     *
     * @param questionQueryRequest 题目查询请求
     * @param request              http request
     * @return question vo page info
     */
    Page<QuestionVO> listSelfQuestionVOByPage(QuestionQueryRequest questionQueryRequest, HttpServletRequest request);

    /**
     * 用户修改题目信息
     *
     * @param questionUpdateRequest 题目修改请求
     * @param request               http request
     * @return true - 修改成功
     */
    Boolean editQuestion(QuestionUpdateRequest questionUpdateRequest, HttpServletRequest request);

    // region util function

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest 题目查询请求封装
     * @return query wrapper
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param question 题目信息
     * @param request  http request
     * @return question info vo
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage 原始题目分页信息
     * @param request      http request
     * @return question vo page
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 分页获取题目封装类信息
     *
     * @param questionPage 原始题目分页信息
     * @param loginUser    登录用户信息
     * @return question vo page
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, User loginUser);
    // endregion
}
