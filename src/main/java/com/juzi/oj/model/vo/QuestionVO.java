package com.juzi.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.juzi.oj.model.dto.question.JudgeConfig;
import com.juzi.oj.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 题目信息封装
 *
 * @author codejuzi
 */
@Data
public class QuestionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String title;

    private String content;

    private List<String> tags;

    private JudgeConfig judgeConfig;

    private Integer submitNum;

    private Integer acNum;

    private Integer thumbNum;

    private Integer favourNum;

    private LocalDate createTime;

    private LocalDate updateTime;

    /**
     * 用户信息封装类
     */
    private UserVO userVO;

    /**
     * 包装类转对象
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);

        // 设置tags, judgeConfig Json对象
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig voJudgeConfig = questionVO.getJudgeConfig();
        if (voJudgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        return question;
    }

    /**
     * 对象转包装类
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        // 转换tags, judgeConfig
        List<String> tagList = JSONUtil.toList(question.getTags(), String.class);
        questionVO.setTags(tagList);
        String judgeConfig = question.getJudgeConfig();
        questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfig, JudgeConfig.class));
        return questionVO;
    }
}