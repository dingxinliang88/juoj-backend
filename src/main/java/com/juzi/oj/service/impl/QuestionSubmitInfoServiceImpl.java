package com.juzi.oj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.oj.mapper.QuestionSubmitInfoMapper;
import com.juzi.oj.model.entity.QuestionSubmitInfo;
import com.juzi.oj.service.QuestionSubmitInfoService;
import org.springframework.stereotype.Service;

/**
 * @author codejuzi
 */
@Service
public class QuestionSubmitInfoServiceImpl extends ServiceImpl<QuestionSubmitInfoMapper, QuestionSubmitInfo>
        implements QuestionSubmitInfoService {
}
