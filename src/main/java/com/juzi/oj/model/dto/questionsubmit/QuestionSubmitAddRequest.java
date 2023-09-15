package com.juzi.oj.model.dto.questionsubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 编程语言
     */
    private String submitLanguage;

    /**
     * 用户提交代码
     */
    private String submitCode;

    private static final long serialVersionUID = 1L;
}