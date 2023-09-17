package com.juzi.oj.core.codesandbox.impl;

import com.juzi.oj.core.codesandbox.CodeSandbox;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeRequest;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 *
 * @author codejuzi
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
