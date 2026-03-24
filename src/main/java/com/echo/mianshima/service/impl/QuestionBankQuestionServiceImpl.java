package com.echo.mianshima.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echo.mianshima.common.ErrorCode;
import com.echo.mianshima.constant.CommonConstant;
import com.echo.mianshima.exception.BusinessException;
import com.echo.mianshima.exception.ThrowUtils;
import com.echo.mianshima.mapper.QuestionBankQuestionMapper;
import com.echo.mianshima.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.echo.mianshima.model.entity.Question;
import com.echo.mianshima.model.entity.QuestionBank;
import com.echo.mianshima.model.entity.QuestionBankQuestion;
import com.echo.mianshima.model.entity.User;
import com.echo.mianshima.model.vo.QuestionBankQuestionVO;
import com.echo.mianshima.model.vo.UserVO;
import com.echo.mianshima.service.QuestionBankQuestionService;
import com.echo.mianshima.service.QuestionBankService;
import com.echo.mianshima.service.QuestionService;
import com.echo.mianshima.service.UserService;
import com.echo.mianshima.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 题库题目关联服务实现
 *
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private QuestionService questionService;

    @Autowired
    private QuestionBankService questionBankService;

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);

        Long questionId = questionBankQuestion.getQuestionId();
        if(questionId != null){
            ThrowUtils.throwIf(questionService.getById(questionId) == null, ErrorCode.PARAMS_ERROR, "题目不存在");
        }

        Long questionBankId = questionBankQuestion.getQuestionBankId();
        if(questionBankId != null){
            ThrowUtils.throwIf(questionBankService.getById(questionBankId) == null, ErrorCode.PARAMS_ERROR, "题库不存在");
        }
        // 不需要校验
        //        // todo 从对象中取值
//        String title = questionBankQuestion.getTitle();
//        // 创建数据时，参数不能为空
//        if (add) {
//            // todo 补充校验规则
//            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
//        }
//        // 修改数据时，有参数则校验
//        // todo 补充校验规则
//        if (StringUtils.isNotBlank(title)) {
//            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
//        }
    }

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();
        Long userId = questionBankQuestionQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 模糊查询

        // JSON 数组查询

        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库题目关联封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(questionBankQuestion);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBankQuestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankQuestionVO.setUser(userVO);
        // endregion

        return questionBankQuestionVO;
    }

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(), questionBankQuestionPage.getSize(), questionBankQuestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream().map(questionBankQuestion -> {
            return QuestionBankQuestionVO.objToVo(questionBankQuestion);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankQuestionList.stream().map(QuestionBankQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionBankQuestionVOList.forEach(questionBankQuestionVO -> {
            Long userId = questionBankQuestionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankQuestionVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }

    /**
     * 批量向题库中添加题目
     *
     * @param questionIds
     * @param questionBankId
     * @return
     */
    public void batchAddQuestionsToBank(List<Long> questionIds, Long questionBankId, User loginUser){
        // 对传递来的参数进行校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIds), ErrorCode.PARAMS_ERROR, "题目列表为空");
        ThrowUtils.throwIf(questionBankId == null || questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库非法");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 判断题库 id 是否合法
        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id")
                .eq("id", questionBankId);
        QuestionBank questionBank = questionBankService.getOne(queryWrapper);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");

        // 拿到合法的题目 id 集合
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.select("id")
                .in("id", questionIds);
        List<Long> validQuestionIds = questionService.listObjs(questionQueryWrapper, o -> (Long)o);
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIds), ErrorCode.PARAMS_ERROR, "合法的题目列表为空");

        // 判断题目是否已经在题库中，若已存在，再插入会导致重复主键
        LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                .select(QuestionBankQuestion::getQuestionId)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                .in(QuestionBankQuestion::getQuestionId, validQuestionIds);
        List<QuestionBankQuestion> existQuestionBankQuestionList = this.list(lambdaQueryWrapper);
        Set<Long> existQuestionBankQuestionIdSet = existQuestionBankQuestionList.stream()
                .map(QuestionBankQuestion::getQuestionId)
                .collect(Collectors.toSet());
        // 过滤掉已经和当前题库存在关联的题目 id
        validQuestionIds = validQuestionIds.stream()
                .filter(questionId -> {
                    return !existQuestionBankQuestionIdSet.contains(questionId);
                })
                .collect(Collectors.toList());
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIds), ErrorCode.PARAMS_ERROR, "所选题目已全部位于所添加题库中，无需再次添加！");
        // 批量向题库中插入记录
        Long loginUserId = loginUser.getId();

        List<QuestionBankQuestion> questionBankQuestions = validQuestionIds.stream()
                .map(questionId -> {
                    QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                    questionBankQuestion.setQuestionId(questionId);
                    questionBankQuestion.setQuestionBankId(questionBankId);
                    questionBankQuestion.setUserId(loginUserId);
                    return questionBankQuestion;
                })
                .collect(Collectors.toList());

        // 自定义线程池
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                20,
                50,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 用于保存所有批次的 CompletableFuture
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        int batchSize = 1000;
        int totalSize = questionBankQuestions.size();
        for (int i = 0; i < totalSize; i += batchSize) {
            List<QuestionBankQuestion> currentBatch = questionBankQuestions.subList(i, Math.min((i + batchSize), totalSize));
            QuestionBankQuestionService questionBankQuestionServiceProxy = (QuestionBankQuestionService) AopContext.currentProxy();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                questionBankQuestionServiceProxy.batchAddQuestionsToBankInner(currentBatch);
            }, customExecutor);
            // questionBankQuestionServiceProxy.batchAddQuestionsToBankInner(currentBatch);
            futures.add(future);
        }

        // 等待所有任务执行完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 关闭线程池
        customExecutor.shutdown();
    }

    /**
     * 批量向题库中添加题目（仅供内部调用）
     *
     * @param questionBankQuestions
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestionsToBankInner(List<QuestionBankQuestion> questionBankQuestions) {

        try {
            boolean result = this.saveBatch(questionBankQuestions);
            if(!result){
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
            }
        } catch (DataIntegrityViolationException e) {
            log.error("数据库唯一键冲突或违反其他完整性约束，错误信息：{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目在该题库中已存在，无法重复添加！");
        } catch (DataAccessException e) {
            log.error("数据库连接问题、事务问题导致操作失败，详细信息：{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败！");
        } catch (Exception e) {
            // 捕获其他异常。通用处理
            log.error("发生未知错误，详细信息：{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败！");
        }
    }

    /**
     * 批量从题库中移除题目
     *
     * @param questionIds
     * @param questionBankId
     * @return
     */
    public void batchRemoveQuestionsFromBank(List<Long> questionIds, Long questionBankId){
        // 对传递来的参数进行校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIds), ErrorCode.PARAMS_ERROR, "未选择要删除的题目！");
        ThrowUtils.throwIf(questionBankId == null, ErrorCode.PARAMS_ERROR, "未选择要讲题目从哪个题库中删除！");

        int batchSize = 1000;
        int totalSize = questionIds.size();
        for (int i = 0; i < totalSize; i += batchSize) {
            QuestionBankQuestionService questionBankQuestionServiceProxy = (QuestionBankQuestionService) AopContext.currentProxy();
            questionBankQuestionServiceProxy.batchRemoveQuestionsFromBankInner(questionIds.subList(i, Math.min(i + batchSize, totalSize)), questionBankId);
        }
    }

    /**
     * 批量从题库中移除题目（仅供内部调用）
     *
     * @param questionIds
     * @param questionBankId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveQuestionsFromBankInner(List<Long> questionIds, Long questionBankId) {
        LambdaQueryWrapper<QuestionBankQuestion> queryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                .in(QuestionBankQuestion::getQuestionId, questionIds);
        boolean result = this.remove(queryWrapper);
        if(!result) {
            log.error("移除当前批次失败，题目id为：{}, 题库id为：{}", questionIds.toString(), questionBankId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "当前批次移除失败，请检查日志信息！");
        }
    }
}
