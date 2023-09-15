package com.juzi.oj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.juzi.oj.constants.UserConstant.*;

/**
 * 用户状态枚举
 *
 * @author codejuzi
 */
public enum UserStateEnum {

    NORMAL("正常", NORMAL_STATE),
    LOGOUT("注销", LOGOUT_STATE),
    BAN("封号", BAN_STATE);

    private final String text;

    private final Integer value;

    UserStateEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public static UserStateEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (UserStateEnum anEnum : UserStateEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
