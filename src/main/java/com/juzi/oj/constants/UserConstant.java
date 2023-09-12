package com.juzi.oj.constants;

/**
 * @author codejuzi
 */
public interface UserConstant {

    int MIN_ACC_LEN = 4;
    int MAX_ACC_LEN = 10;
    int MIN_PWD_LEN = 8;
    String ACC_PATTEN = "^[a-zA-Z0-9]+$";

    int SALT_LEN = 6;

    int DEFAULT_NICK_SUFFIX_LEN = 8;


    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";


    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    String USER_DEFAULT_AVATAR_URL = "https://regengbaike.com/uploads/20230222/1bff61de34bdc7bf40c6278b2848fbcf.jpg";
}
