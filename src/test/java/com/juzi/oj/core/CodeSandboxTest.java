package com.juzi.oj.core;

import com.juzi.oj.core.codesandbox.CodeSandbox;
import com.juzi.oj.core.codesandbox.CodeSandboxFactory;
import com.juzi.oj.core.codesandbox.CodeSandboxProxy;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeRequest;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeResponse;
import com.juzi.oj.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * @author codejuzi
 */
@SpringBootTest
class CodeSandboxTest {

    @Value("codesandbox.type:example")
    private String type;

    @Test
    void execute() {
//        CodeSandbox codeSandbox = new ExampleCodeSandbox();
        CodeSandbox codeSandbox = new CodeSandboxProxy(CodeSandboxFactory.newInstance(type));
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code("int main() {}")
                .language(QuestionSubmitLanguageEnum.JAVA.getValue())
                .inputList(Arrays.asList("1 2", "3 4"))
                .build();
        ExecuteCodeResponse response = codeSandbox.execute(request);
        System.out.println(response);
    }
}