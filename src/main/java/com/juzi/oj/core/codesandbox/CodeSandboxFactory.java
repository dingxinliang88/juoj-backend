package com.juzi.oj.core.codesandbox;

import com.juzi.oj.core.codesandbox.impl.ExampleCodeSandbox;
import com.juzi.oj.core.codesandbox.impl.RemoteCodeSandbox;
import com.juzi.oj.core.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * @author codejuzi
 */
public class CodeSandboxFactory {

    private CodeSandboxFactory() {
    }


    /**
     * 创建代码沙箱
     *
     * @param type 代码沙箱类别
     * @return code sandbox
     */
    public static CodeSandbox newInstance(final String type) {
        switch (type) {
            case "remote":
                return new RemoteCodeSandbox();
            case "third-party":
                return new ThirdPartyCodeSandbox();
            default:
                // example
                return new ExampleCodeSandbox();
        }
    }

}
