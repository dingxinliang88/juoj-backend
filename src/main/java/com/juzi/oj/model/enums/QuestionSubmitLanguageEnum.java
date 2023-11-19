package com.juzi.oj.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目提交编程语言枚举
 *
 * @author codejuzi
 */
@Getter
public enum QuestionSubmitLanguageEnum {
    DEFAULT("default", "java"),
    JAVA("java", "java"),
    CPLUSPLUS("cpp", "cpp"),
    C_SHAPE("csharp", "csharp"),
    GOLANG("golang", "golang"),
    PYTHON("python", "python"),
    JAVASCRIPT("javascript", "javascript");

    private final String text;

    private final String value;

    QuestionSubmitLanguageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }


    public static QuestionSubmitLanguageEnum getEnumByValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionSubmitLanguageEnum anEnum : QuestionSubmitLanguageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
