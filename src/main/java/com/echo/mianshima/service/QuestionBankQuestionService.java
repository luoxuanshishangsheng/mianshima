package com.echo.mianshima.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echo.mianshima.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.echo.mianshima.model.entity.QuestionBankQuestion;
import com.echo.mianshima.model.entity.User;
import com.echo.mianshima.model.vo.QuestionBankQuestionVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题库题目关联服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add 对创建的数据进行校验
     */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);
    
    /**
     * 获取题库题目关联封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request);

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request);

    /**
     * 批量向题库中添加题目
     *
     * @param questionIds
     * @param questionBankId
     * @return
     */
    void batchAddQuestionsToBank(List<Long> questionIds, Long questionBankId, User loginUser);

    /**
     * 批量向题库中添加题目（仅供内部调用）
     *
     * @param questionBankQuestions
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    void batchAddQuestionsToBankInner(List<QuestionBankQuestion> questionBankQuestions);

    /**
     * 批量从题库中移除题目
     *
     * @param questionIds
     * @param questionBankId
     * @return
     */
    public void batchRemoveQuestionsFromBank(List<Long> questionIds, Long questionBankId);

    /**
     * 批量从题库中移除题目（仅供内部调用）
     *
     * @param questionIds
     * @param questionBankId
     * @return
     */
    public void batchRemoveQuestionsFromBankInner(List<Long> questionIds, Long questionBankId);
}
