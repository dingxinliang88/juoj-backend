package com.juzi.oj.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author codejuzi
 */
@Data
@TableName(value = "question_submit_info")
public class QuestionSubmitInfo implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 405146976179300968L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionId;

    private Long userId;

    private String judgeInfo;

    private String submitLanguage;

    private String submitCode;

    private Integer submitState;

    private LocalDate createTime;

    @TableLogic
    private Integer isDelete;
}
