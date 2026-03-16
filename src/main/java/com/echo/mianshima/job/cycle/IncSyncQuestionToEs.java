package com.echo.mianshima.job.cycle;

import cn.hutool.core.collection.CollUtil;
import com.echo.mianshima.esdao.QuestionEsDao;
import com.echo.mianshima.mapper.QuestionMapper;
import com.echo.mianshima.model.dto.question.QuestionEsDto;
import com.echo.mianshima.model.entity.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步题目到 es
 */
// todo 取消注释开启任务
@Component
@Slf4j
@RequiredArgsConstructor
public class IncSyncQuestionToEs {

    private final QuestionMapper questionMapper;
    private final QuestionEsDao questionEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        long PAST_TIME = 5L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastTime = now.minusMinutes(PAST_TIME);
        List<Question> questionList = questionMapper.listQuestionWithDeletedByUpdateTime(pastTime);

        if(CollUtil.isEmpty(questionList)){
            log.info("没有需要增量同步的数据");
            return;
        }
        // 转换类型为QuestionDto
        List<QuestionEsDto> questionEsDtoList = questionList
                .stream()
                .map(QuestionEsDto::objToDto)
                .collect(Collectors.toList());
        // 分页存入elasticsearch
        int pageSize = 500;
        int total = questionEsDtoList.size();
        log.info("开始增量同步， 共：{}条数据", total);
        for (int i = 0; i < total; i+=pageSize) {
            // 注意数据边界
            int end = Math.min(i + pageSize, total);
            questionEsDao.saveAll(questionEsDtoList.subList(i, end));
        }
        log.info("增量同步结束");
    }
}
