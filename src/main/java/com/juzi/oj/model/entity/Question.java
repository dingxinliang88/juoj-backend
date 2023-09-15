package com.juzi.oj.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author codejuzi
 */
@Data
@TableName(value = "question")
public class Question implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = -106286751654733865L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建题目用户id
     */
    private Long userId;

    private String title;

    private String content;

    /**
     * Json数组
     * eg:
     * ["简单", "模拟"]
     */
    private String tags;

    private String answer;

    /**
     * 题目用例（Json数组）
     * eg:
     * [
     * {
     *  "input": "1 2",
     *  "output": "3"
     * }
     * ]
     */
    private String judgeCase;

    /**
     * 判题配置（Json对象）
     * eg:
     * {
     *   "timeLimit": 1000,
     *   "memoryLimit": 1000,
     *   "stackLimit": 1000
     * }
     */
    private String judgeConfig;

    private Integer submitNum;

    private Integer acNum;

    private Integer thumbNum;

    private Integer favourNum;

    private LocalDate createTime;

    private LocalDate updateTime;

    @TableLogic
    private Integer isDelete;
}
