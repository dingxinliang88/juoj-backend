package com.juzi.oj.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author codejuzi
 */
@Data
public class QuestionAddRequest implements Serializable {

    private static final long serialVersionUID = 8305283741997336442L;

    private String title;

    private String content;

    /**
     * 标签 => json数组
     */
    private List<String> tags;

    private String answer;

    /**
     * 题目用例（Json数组）
     * eg:
     * [
     * {
     * "input": "1 2",
     * "output": "3"
     * }
     * ]
     */
    private List<String> judgeCase;

    /**
     * 判题配置（Json对象）
     * eg:
     * {
     * "timeLimit": 1000,
     * "memoryLimit": 1000,
     * "stackLimit": 1000
     * }
     */
    private String judgeConfig;
}
