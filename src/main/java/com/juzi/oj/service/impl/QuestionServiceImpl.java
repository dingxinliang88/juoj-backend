package com.juzi.oj.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.oj.common.DeleteRequest;
import com.juzi.oj.common.StatusCode;
import com.juzi.oj.constants.CommonConstant;
import com.juzi.oj.exception.BusinessException;
import com.juzi.oj.mapper.QuestionMapper;
import com.juzi.oj.model.dto.question.*;
import com.juzi.oj.model.entity.Question;
import com.juzi.oj.model.entity.User;
import com.juzi.oj.model.vo.QuestionVO;
import com.juzi.oj.model.vo.UserVO;
import com.juzi.oj.service.QuestionService;
import com.juzi.oj.service.UserService;
import com.juzi.oj.utils.SqlUtils;
import com.juzi.oj.utils.ThrowUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.juzi.oj.constants.CommonConstant.MAX_FETCH_SIZE;

/**
 * @author codejuzi
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    @Resource
    private UserService userService;

    @Override
    public Long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        // json处理
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCases();
        if (judgeCase != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCase));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }

        validQuestion(question);

        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        boolean result = this.save(question);
        ThrowUtils.throwIf(!result, StatusCode.SYSTEM_ERROR);

        return question.getId();
    }

    @Override
    public Boolean deleteQuestion(DeleteRequest deleteRequest, HttpServletRequest request) {

        User user = userService.getLoginUser(request);
        Long id = deleteRequest.getId();
        // 判断题目是否存在
        Question oldQuestion = this.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, StatusCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        boolean inPerson = oldQuestion.getUserId().equals(user.getId());
        boolean isAdmin = userService.isAdmin(user);
        if (!inPerson && !isAdmin) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR);
        }
        boolean b = this.removeById(id);

        ThrowUtils.throwIf(!b, StatusCode.SYSTEM_ERROR);
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest) {
        // 判断题目是否存在
        Long id = questionUpdateRequest.getId();
        Question oldQuestion = this.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, StatusCode.NOT_FOUND_ERROR);

        LambdaUpdateWrapper<Question> updateWrapper = getUpdateWrapper(questionUpdateRequest);

        boolean result = this.update(updateWrapper);

        ThrowUtils.throwIf(!result, StatusCode.SYSTEM_ERROR);

        return Boolean.TRUE;
    }

    @Override
    public LambdaUpdateWrapper<Question> getUpdateWrapper(QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        Long id = questionUpdateRequest.getId();

        if (id == null || id <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        String title = questionUpdateRequest.getTitle();
        String content = questionUpdateRequest.getContent();
        List<String> tags = questionUpdateRequest.getTags();
        String answer = questionUpdateRequest.getAnswer();
        List<JudgeCase> judgeCases = questionUpdateRequest.getJudgeCases();
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();


        LambdaUpdateWrapper<Question> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper.eq(Question::getId, id)
                .set(StringUtils.isNotBlank(title) && title.length() < 64, Question::getTitle, title)
                .set(StringUtils.isNotBlank(content) && content.length() < 8192, Question::getContent, content)
                .set(StringUtils.isNotBlank(answer) && content.length() < 8192, Question::getAnswer, answer)
                .set(CollectionUtil.isNotEmpty(tags), Question::getTags, JSONUtil.toJsonStr(tags));

        String judgeConfigJson = JSONUtil.toJsonStr(judgeConfig);
        String judgeCaseJson = JSONUtil.toJsonStr(judgeCases);
        updateWrapper
                .set(CollectionUtil.isNotEmpty(judgeCases) && judgeCaseJson.length() <= 8192, Question::getJudgeCase, judgeCaseJson)
                .set(ObjectUtils.isNotEmpty(judgeConfig) && judgeConfigJson.length() <= 512, Question::getJudgeConfig, judgeConfigJson);

        return updateWrapper;
    }

    @Override
    public QuestionVO getQuestionVOById(Long questionId, HttpServletRequest request) {
        if (questionId == null || questionId <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        Question question = this.getById(questionId);
        if (question == null) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        return getQuestionVO(question, request);
    }

    @Override
    public Question getQuestionById(Long questionId, HttpServletRequest request) {
        Question question = this.getById(questionId);
        if (question == null) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        // 非本人或者管理员，不能获取到题目所有信息
        User loginUser = userService.getLoginUser(request);
        boolean inPerson = question.getUserId().equals(loginUser.getId());
        boolean isAdmin = userService.isAdmin(loginUser);
        if (!inPerson && !isAdmin) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR);
        }
        return question;
    }

    @Override
    public Page<QuestionVO> listQuestionVOByPage(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {

        Long current = questionQueryRequest.getCurrent();
        Long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > MAX_FETCH_SIZE, StatusCode.PARAMS_ERROR);
        Page<Question> questionPage = this.page(new Page<>(current, size),
                getQueryWrapper(questionQueryRequest));

        return getQuestionVOPage(questionPage, request);
    }

    @Override
    public Page<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest) {
        Long current = questionQueryRequest.getCurrent();
        Long size = questionQueryRequest.getPageSize();
        return this.page(new Page<>(current, size), getQueryWrapper(questionQueryRequest));
    }

    @Override
    public Page<QuestionVO> listSelfQuestionVOByPage(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        Long current = questionQueryRequest.getCurrent();
        Long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > MAX_FETCH_SIZE, StatusCode.PARAMS_ERROR);
        Page<Question> questionPage = this.page(new Page<>(current, size),
                getQueryWrapper(questionQueryRequest));

        return getQuestionVOPage(questionPage, loginUser);
    }

    @Override
    public Boolean editQuestion(QuestionUpdateRequest questionUpdateRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);

        // 判断题目是否存在
        Long id = questionUpdateRequest.getId();
        Question oldQuestion = this.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, StatusCode.NOT_FOUND_ERROR);
        // 仅本人编辑
        boolean inPerson = oldQuestion.getUserId().equals(loginUser.getId());
        if (!inPerson) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR);
        }

        LambdaUpdateWrapper<Question> updateWrapper = getUpdateWrapper(questionUpdateRequest);
        boolean result = this.update(updateWrapper);

        ThrowUtils.throwIf(!result, StatusCode.SYSTEM_ERROR);

        return Boolean.TRUE;
    }


    // region util function

    private void validQuestion(Question question) {
        if (question == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags, answer, judgeCase, judgeConfig), StatusCode.PARAMS_ERROR);
        if (StringUtils.isNotBlank(title) && title.length() > 64) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 512) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "判题配置过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "判题用例过长");
        }
    }

    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {

        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        Long userId = questionQueryRequest.getUserId();
        String title = questionQueryRequest.getTitle();
        List<String> tags = questionQueryRequest.getTags();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        if (CollectionUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtil.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userService.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, User loginUser) {
        UserVO userVO = userService.getUserVO(loginUser);

        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());

        List<QuestionVO> questionVOList = questionPage.getRecords().stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            questionVO.setUserVO(userVO);
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }
    // endregion

}
