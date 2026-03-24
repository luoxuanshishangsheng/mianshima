package com.echo.mianshima.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 向题库中批量添加题目
 *
 */
@Data
public class QuestionBankQuestionBatchRemoveRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id 集合
     */
    private List<Long> questionIds;

    private static final long serialVersionUID = 1L;
}