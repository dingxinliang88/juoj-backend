package com.juzi.oj.core.codesandbox;

import com.juzi.oj.core.codesandbox.model.ExecuteCodeRequest;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeResponse;

/**
 * @author codejuzi
 */
public interface CodeSandbox {

    /**
     * 代码沙箱执行接口
     *
     * @param executeCodeRequest 执行代码请求
     * @return 执行代码响应
     */
    ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest);
}
