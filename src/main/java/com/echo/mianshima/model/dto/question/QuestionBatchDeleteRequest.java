package com.echo.mianshima.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除题目请求
 *
 */
@Data
public class QuestionBatchDeleteRequest implements Serializable {

    /**
     * 需要删除的题目集合
     */
    private List<Long> questionIds;

    private static final long serialVersionUID = 1L;
}