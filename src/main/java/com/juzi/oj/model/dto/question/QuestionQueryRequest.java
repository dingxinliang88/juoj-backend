package com.juzi.oj.model.dto.question;

import com.juzi.oj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author codejuzi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -505627798230862405L;

    private Long id;

    private Long userId;

    private String title;

    /**
     * 标签 => json数组
     */
    private List<String> tags;
}
