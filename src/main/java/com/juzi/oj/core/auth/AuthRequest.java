package com.juzi.oj.core.auth;

/**
 * 定义鉴权请求头和密钥
 *
 * @author codejuzi
 */
public interface AuthRequest {
    // 请求头
    String AUTH_REQUEST_HEADER = "auth";

    // 密钥
    String AUTH_REQUEST_SECRET = "secret_key_code_sandbox";
}