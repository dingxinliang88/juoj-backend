package com.juzi.oj.model.dto.questionsubmit;

import com.juzi.oj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 编程语言
     */
    private String submitLanguage;

    /**
     * 提交状态
     */
    private Integer submitState;


    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}