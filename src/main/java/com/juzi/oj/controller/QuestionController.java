package com.juzi.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.oj.aspect.AuthCheck;
import com.juzi.oj.common.BaseResponse;
import com.juzi.oj.common.DeleteRequest;
import com.juzi.oj.common.ResultUtils;
import com.juzi.oj.common.StatusCode;
import com.juzi.oj.constants.UserConstant;
import com.juzi.oj.exception.BusinessException;
import com.juzi.oj.model.dto.question.QuestionAddRequest;
import com.juzi.oj.model.dto.question.QuestionQueryRequest;
import com.juzi.oj.model.dto.question.QuestionUpdateRequest;
import com.juzi.oj.model.entity.Question;
import com.juzi.oj.model.vo.QuestionVO;
import com.juzi.oj.service.QuestionService;
import com.juzi.oj.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author codejuzi
 */
@Slf4j
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    @ApiOperation(value = "添加题目")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        return ResultUtils.success(questionService.addQuestion(questionAddRequest, request));
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除题目")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        return ResultUtils.success(questionService.deleteQuestion(deleteRequest, request));
    }

    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "管理员更新题目信息")
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        return ResultUtils.success(questionService.updateQuestion(questionUpdateRequest));
    }

    @GetMapping("/get/vo")
    @ApiOperation(value = "获取题目VO信息")
    public BaseResponse<QuestionVO> getQuestionVoById(@RequestParam(value = "id") Long questionId, HttpServletRequest request) {
        if (questionId <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        return ResultUtils.success(questionService.getQuestionVOById(questionId, request));
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取题目信息")
    public BaseResponse<Question> getQuestionById(@RequestParam(value = "id") Long questionId, HttpServletRequest request) {
        if (questionId <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        return ResultUtils.success(questionService.getQuestionById(questionId, request));
    }

    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取题目VO列表")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        return ResultUtils.success(questionService.listQuestionVOByPage(questionQueryRequest, request));
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "管理员分页获取题目列表（所有信息）")
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                           HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        return ResultUtils.success(questionService.listQuestionByPage(questionQueryRequest));
    }

    @PostMapping("/list/my/page/vo")
    @ApiOperation(value = "分页获取当前用户创建的题目列表")
    public BaseResponse<Page<QuestionVO>> listSelfQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                   HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        return ResultUtils.success(questionService.listSelfQuestionVOByPage(questionQueryRequest, request));
    }

    @PostMapping("/edit")
    @ApiOperation(value = "用户编辑题目")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest, HttpServletRequest request) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        return ResultUtils.success(questionService.editQuestion(questionUpdateRequest, request));
    }
}
