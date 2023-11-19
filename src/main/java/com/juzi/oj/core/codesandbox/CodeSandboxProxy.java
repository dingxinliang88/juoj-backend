package com.juzi.oj.core.codesandbox;

import com.juzi.oj.core.codesandbox.model.ExecuteCodeRequest;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱代理类，增强能力（增加日志功能）
 *
 * @author codejuzi
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息: {}", executeCodeRequest);
        ExecuteCodeResponse response = codeSandbox.execute(executeCodeRequest);
        log.info("代码沙箱响应信息: {}", response);
        return response;
    }
}
