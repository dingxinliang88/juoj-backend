package com.juzi.oj.core.codesandbox.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 执行代码请求
 *
 * @author codejuzi
 */
@Data
@Builder
public class ExecuteCodeRequest implements Serializable {

    private static final long serialVersionUID = -1729942560382446421L;

    private List<String> inputList;

    private String code;

    private String language;

}
