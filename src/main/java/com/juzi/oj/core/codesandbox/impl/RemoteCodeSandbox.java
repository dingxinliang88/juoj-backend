package com.juzi.oj.core.codesandbox.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.juzi.oj.common.StatusCode;
import com.juzi.oj.core.codesandbox.CodeSandbox;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeRequest;
import com.juzi.oj.core.codesandbox.model.ExecuteCodeResponse;
import com.juzi.oj.exception.BusinessException;

import static com.juzi.oj.core.auth.AuthRequest.AUTH_REQUEST_HEADER;
import static com.juzi.oj.core.auth.AuthRequest.AUTH_REQUEST_SECRET;

/**
 * 远程代码沙箱
 *
 * @author codejuzi
 */
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        String url = "http://localhost:8888/exec_code";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String respStr;
        try (HttpResponse response = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()) {
            respStr = response.body();
        }
        if (StrUtil.isBlank(respStr)) {
            throw new BusinessException(StatusCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + respStr);
        }

        return JSONUtil.toBean(respStr, ExecuteCodeResponse.class);
    }
}
