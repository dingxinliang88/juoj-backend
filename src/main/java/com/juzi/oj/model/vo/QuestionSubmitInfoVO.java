package com.juzi.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.juzi.oj.model.dto.questionsubmit.JudgeInfo;
import com.juzi.oj.model.entity.QuestionSubmitInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 题目提交信息VO
 *
 * @author codejuzi
 */
@Data
public class QuestionSubmitInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long questionId;

    private Long userId;

    private JudgeInfo judgeInfo;

    private String submitLanguage;

    private String submitCode;

    private Integer submitState;

    private LocalDate createTime;

    private UserVO userVO;

    /**
     * 包装类转对象
     */
    public static QuestionSubmitInfo voToObj(QuestionSubmitInfoVO questionSubmitInfoVO) {
        if (questionSubmitInfoVO == null) {
            return null;
        }
        QuestionSubmitInfo questionSubmit = new QuestionSubmitInfo();
        BeanUtils.copyProperties(questionSubmitInfoVO, questionSubmit);
        // 封装判题信息（Json格式）
        JudgeInfo judgeInfoObj = questionSubmitInfoVO.getJudgeInfo();
        if (judgeInfoObj != null) {
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoObj));
        }
        return questionSubmit;
    }

    /**
     * 对象转包装类
     */
    public static QuestionSubmitInfoVO objToVo(QuestionSubmitInfo questionSubmitInfo) {
        if (questionSubmitInfo == null) {
            return null;
        }
        QuestionSubmitInfoVO questionSubmitVO = new QuestionSubmitInfoVO();
        BeanUtils.copyProperties(questionSubmitInfo, questionSubmitVO);
        String judgeInfoStr = questionSubmitInfo.getJudgeInfo();
        // 转换成包装类
        questionSubmitVO.setJudgeInfo(JSONUtil.toBean(judgeInfoStr, JudgeInfo.class));
        return questionSubmitVO;
    }
}