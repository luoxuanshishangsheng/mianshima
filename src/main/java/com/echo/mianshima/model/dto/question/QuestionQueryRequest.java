package com.echo.mianshima.model.dto.question;

import com.echo.mianshima.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询题目请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 推荐答案
     */
    private String answer;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 题库 id
     */
    private Long questionBankId;

//    /**
//     * 页码
//     */
//    private int current;
//
//    /**
//     * 页大小
//     */
//    private int pageSize;
//
//    /**
//     * 排序字段
//     */
//    private String sortField;
//
//    /**
//     * 排序方式
//     */
//    private String sortOrder;

    private static final long serialVersionUID = 1L;
}