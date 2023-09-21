package com.juzi.oj.common;

/**
 * 状态码
 *
 * @author codejuzi
 */
public enum StatusCode {
    SUCCESS(0, "success"),
    CREATED(20100, "已创建"),
    ACCEPTED(20200, "请求已接受，正在处理"),
    DUPLICATE_DATA(41000, "重复数据"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    NOT_LOGIN_ERROR(40500, "未登录"),
    PARAMS_ERROR(40000, "请求参数不合法"),
    FORBIDDEN(40300, "拒绝请求"),
    NO_AUTH_ERROR(40100, "无权访问"),
    UNAUTHORIZED(40100, "无权访问"),
    METHOD_NOT_ALLOWED(40500, "不允许使用该方法请求"),
    CONFLICT(40900, "请求冲突"),
    TOO_MANY_REQUEST(42900, "请求频繁"),
    SYSTEM_ERROR(50000, "系统内部错误"),
    OPERATION_ERROR(50100, "操作错误"),
    API_REQUEST_ERROR(50010, "接口调用错误"),
    BAD_GATEWAY(50200, "网关错误"),
    SERVICE_UNAVAILABLE(50300, "服务不可用");

    private final int code;
    private final String message;


    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
