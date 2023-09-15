package com.juzi.oj.controller;

import com.juzi.oj.service.QuestionSubmitInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author codejuzi
 */
@Slf4j
@RestController
@RequestMapping("/question_submit_info")
public class QuestionSubmitInfoController {

    @Resource
    private QuestionSubmitInfoService questionSubmitInfoService;
}
