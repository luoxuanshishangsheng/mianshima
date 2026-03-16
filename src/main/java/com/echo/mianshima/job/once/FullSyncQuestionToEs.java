package com.echo.mianshima.job.once;

import cn.hutool.core.collection.CollUtil;
import com.echo.mianshima.esdao.QuestionEsDao;
import com.echo.mianshima.model.dto.question.QuestionEsDto;
import com.echo.mianshima.model.entity.Question;
import com.echo.mianshima.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步题目到 es
 */
// todo 取消注释开启任务
@Component
@Slf4j
@RequiredArgsConstructor
public class FullSyncQuestionToEs implements CommandLineRunner {

    private final QuestionService questionService;
    private final QuestionEsDao questionEsDao;

    @Override
    public void run(String... args) {
        // 从数据库中读出所有题目信息
        List<Question> questionList = questionService.list();
        if(CollUtil.isEmpty(questionList))
            return;
        // 转换类型为QuestionDto
        List<QuestionEsDto> questionEsDtoList = questionList.stream().map(QuestionEsDto::objToDto).collect(Collectors.toList());
        // 分页存入elasticsearch
        int pageSize = 500;
        int total = questionEsDtoList.size();
        log.info("开始全量同步， 共：{}条数据", total);
        for (int i = 0; i < total; i+=pageSize) {
            // 注意数据边界
            int end = Math.min(i + pageSize, total);
            questionEsDao.saveAll(questionEsDtoList.subList(i, end));
        }
        log.info("全量同步结束");
    }
}
