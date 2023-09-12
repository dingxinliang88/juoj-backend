package com.juzi.oj.utils;


import com.juzi.oj.common.StatusCode;
import com.juzi.oj.exception.BusinessException;

/**
 * 抛出异常工具类
 *
 * @author codejuzi
 */
public class ThrowUtils {

    /**
     * 条件成立，抛出异常
     *
     * @param condition        条件
     * @param runtimeException 异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition  条件
     * @param statusCode 错误码
     */
    public static void throwIf(boolean condition, StatusCode statusCode) {
        throwIf(condition, new BusinessException(statusCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition  条件
     * @param statusCode 错误码
     * @param message    错误信息
     */
    public static void throwIf(boolean condition, StatusCode statusCode, String message) {
        throwIf(condition, new BusinessException(statusCode, message));
    }

}
