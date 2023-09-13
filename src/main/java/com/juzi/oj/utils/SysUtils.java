package com.juzi.oj.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static com.juzi.oj.constants.UserConstant.*;

/**
 * 系统工具类
 *
 * @author codejuzi
 */
public class SysUtils {

    // region user

    public static String genSalt() {
        return RandomStringUtils.randomAlphabetic(SALT_LEN);
    }

    public static String encryptPassword(String salt, String originPassword) {
        return DigestUtils.md5DigestAsHex((salt + originPassword).getBytes(StandardCharsets.UTF_8));
    }

    public static String genDefaultNickname() {
        return DEFAULT_NICK_PREFIX + RandomStringUtils.randomAlphabetic(DEFAULT_NICK_SUFFIX_LEN);
    }


    // endregion
}
