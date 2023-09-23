package com.juzi.oj.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.oj.common.StatusCode;
import com.juzi.oj.constants.CommonConstant;
import com.juzi.oj.exception.BusinessException;
import com.juzi.oj.mapper.QuestionSubmitInfoMapper;
import com.juzi.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.juzi.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.juzi.oj.model.entity.Question;
import com.juzi.oj.model.entity.QuestionSubmitInfo;
import com.juzi.oj.model.entity.User;
import com.juzi.oj.model.enums.QuestionSubmitLanguageEnum;
import com.juzi.oj.model.enums.QuestionSubmitStatusEnum;
import com.juzi.oj.model.vo.QuestionSubmitInfoVO;
import com.juzi.oj.mq.OjMQProducer;
import com.juzi.oj.service.QuestionService;
import com.juzi.oj.service.QuestionSubmitInfoService;
import com.juzi.oj.service.UserService;
import com.juzi.oj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.juzi.oj.constants.OjMQConstant.OJ_EXCHANGE_NAME;
import static com.juzi.oj.constants.OjMQConstant.OJ_ROUTING_KEY;

/**
 * @author codejuzi
 */
@Service
public class QuestionSubmitInfoServiceImpl extends ServiceImpl<QuestionSubmitInfoMapper, QuestionSubmitInfo>
        implements QuestionSubmitInfoService {

    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    @Resource
    private OjMQProducer ojMQProducer;

    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        final User loginUser = userService.getLoginUser(request);

        Long questionId = questionSubmitAddRequest.getQuestionId();
        String submitLanguage = questionSubmitAddRequest.getSubmitLanguage();
        String submitCode = questionSubmitAddRequest.getSubmitCode();

        // 检验
        QuestionSubmitLanguageEnum submitLanguageEnum = QuestionSubmitLanguageEnum.getEnumByValue(submitLanguage);
        if (submitLanguageEnum == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "编程语言错误");
        }

        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 设置提交数
        // TODO: 2023/9/17 考虑是否需要加锁，还是允许一定的误差
        LambdaUpdateWrapper<Question> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Question::getId, questionId)
                .set(Question::getSubmitNum, question.getSubmitNum() + 1);
        boolean updateRes = questionService.update(updateWrapper);
        if (!updateRes) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "数据保存失败");
        }

        // 每个用户串行提交题目
        QuestionSubmitInfo submitInfo = new QuestionSubmitInfo();
        submitInfo.setUserId(loginUser.getId());
        submitInfo.setQuestionId(questionId);
        submitInfo.setSubmitCode(submitCode);
        submitInfo.setSubmitLanguage(submitLanguage);
        submitInfo.setSubmitState(QuestionSubmitStatusEnum.WAITING.getValue()); // 初始状态
        submitInfo.setJudgeInfo("{}");
        boolean saveRes = this.save(submitInfo);
        if (!saveRes) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "题目提交失败");
        }

        Long submitInfoId = submitInfo.getId();

        // 执行判题服务，发送MQ消息
        ojMQProducer.sendMessage(OJ_EXCHANGE_NAME, OJ_ROUTING_KEY, String.valueOf(submitInfoId));

        return submitInfoId;
    }

    @Override
    public Page<QuestionSubmitInfoVO> listQuestionSubmitInfoVOByPage(QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        Long current = questionSubmitQueryRequest.getCurrent();
        Long pageSize = questionSubmitQueryRequest.getPageSize();

        // 从数据库中查询到原始的题目提交信息
        Page<QuestionSubmitInfo> questionSubmitPage = this.page(new Page<>(current, pageSize),
                getQueryWrapper(questionSubmitQueryRequest));
        final User loginUer = userService.getLoginUser(request);
        return getQuestionSubmitVOPage(questionSubmitPage, loginUer);
    }


    // region util function

    @Override
    public QueryWrapper<QuestionSubmitInfo> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {

        QueryWrapper<QuestionSubmitInfo> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String submitLanguage = questionSubmitQueryRequest.getSubmitLanguage();
        Integer submitState = questionSubmitQueryRequest.getSubmitState();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        queryWrapper.eq(ObjectUtils.isNotEmpty(submitLanguage), "submit_language", submitLanguage)
                .eq(ObjectUtils.isNotEmpty(userId), "user_id", userId)
                .eq(ObjectUtils.isNotEmpty(questionId), "question_id", questionId)
                .eq(QuestionSubmitStatusEnum.getEnumByValue(submitState) != null, "submit_state", submitState)
                .orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitInfoVO getQuestionSubmitVO(QuestionSubmitInfo questionSubmitInfo, User loginUser) {
        QuestionSubmitInfoVO questionSubmitVO = QuestionSubmitInfoVO.objToVo(questionSubmitInfo);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        if (userId != questionSubmitInfo.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setSubmitCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitInfoVO> getQuestionSubmitVOPage(Page<QuestionSubmitInfo> questionSubmitInfoPage, User loginUser) {
        List<QuestionSubmitInfo> questionSubmitInfoList = questionSubmitInfoPage.getRecords();
        Page<QuestionSubmitInfoVO> questionSubmitVOPage = new Page<>(
                questionSubmitInfoPage.getCurrent(), questionSubmitInfoPage.getSize(), questionSubmitInfoPage.getTotal());
        if (CollectionUtil.isEmpty(questionSubmitInfoList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitInfoVO> questionSubmitVOList = questionSubmitInfoList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
    // endregion

}
