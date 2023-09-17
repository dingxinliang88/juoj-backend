package com.juzi.oj.core.codesandbox.impl;

import com.juzi.oj.core.codesandbox.CodeSandbox;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeRequest;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeResponse;

/**
 * 远程代码沙箱
 *
 * @author codejuzi
 */
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        return null;
    }
}
