package com.juzi.oj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.oj.mapper.QuestionMapper;
import com.juzi.oj.model.entity.Question;
import com.juzi.oj.service.QuestionService;
import org.springframework.stereotype.Service;

/**
 * @author codejuzi
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
}
