package com.juzi.oj.core.codesandbox.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 执行代码response
 *
 * @author codejuzi
 */
@Data
public class ExecuteCodeResponse implements Serializable {

    private List<String> outputList;

    private JudgeInfo judgeInfo;

    private String message;

    private Integer status;
}
