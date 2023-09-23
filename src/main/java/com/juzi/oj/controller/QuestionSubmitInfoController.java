package com.juzi.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.oj.common.BaseResponse;
import com.juzi.oj.common.ResultUtils;
import com.juzi.oj.common.StatusCode;
import com.juzi.oj.exception.BusinessException;
import com.juzi.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.juzi.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.juzi.oj.model.vo.QuestionSubmitInfoVO;
import com.juzi.oj.service.QuestionSubmitInfoService;
import com.juzi.oj.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author codejuzi
 */
@Slf4j
@RestController
@RequestMapping("/question_submit_info")
public class QuestionSubmitInfoController {

    @Resource
    private QuestionSubmitInfoService questionSubmitInfoService;

    @Resource
    private UserService userService;

    @PostMapping("/submit")
    @ApiOperation(value = "题目提交")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        return ResultUtils.success(questionSubmitInfoService.doQuestionSubmit(questionSubmitAddRequest, request));
    }


    @PostMapping("/list/page")
    @ApiOperation(value = "分页获取题目提交信息列表")
    public BaseResponse<Page<QuestionSubmitInfoVO>> listQuestionSubmitVOByPage(
            @RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
            HttpServletRequest request
    ) {
        if (questionSubmitQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        // 返回脱敏信息
        return ResultUtils.success(
                questionSubmitInfoService.listQuestionSubmitInfoVOByPage(questionSubmitQueryRequest, request));
    }
}
