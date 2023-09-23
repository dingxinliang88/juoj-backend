package com.juzi.oj.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author codejuzi
 */
public interface UserConstant {

    // region login && register validation

    int MIN_ACC_LEN = 4;
    int MAX_ACC_LEN = 10;
    int MIN_PWD_LEN = 8;
    int SALT_LEN = 6;
    int DEFAULT_NICK_SUFFIX_LEN = 8;
    String DEFAULT_NICK_PREFIX = "OJ_";
    String ACC_PATTEN = "^[a-zA-Z0-9]+$";

    // endregion


    // region user gender and state value, admin role value
    String ADMIN_ROLE = "admin";

    Integer MAN = 1;
    Integer WOMEN = 0;
    Set<Integer> GENDER_SET = new HashSet<>(Arrays.asList(MAN, WOMEN));


    // 0-正常/1-注销/2-封号
    Integer NORMAL_STATE = 0;
    Integer LOGOUT_STATE = 1;
    Integer BAN_STATE = 2;
    Set<Integer> STATE_SET = new HashSet<>(Arrays.asList(NORMAL_STATE, LOGOUT_STATE, BAN_STATE));

    // endregion


    // region user login session
    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    // endregion


    // region user default attribution
    String DEFAULT_USER_AVATAR = "https://regengbaike.com/uploads/20230222/1bff61de34bdc7bf40c6278b2848fbcf.jpg";

    String DEFAULT_USER_PROFILE = "该用户很懒，什么都没有写";

    // endregion
}
