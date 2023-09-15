package com.juzi.oj.controller;

import com.juzi.oj.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author codejuzi
 */
@Slf4j
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;
}
