package com.echo.mianshima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echo.mianshima.model.entity.Question;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author 20656
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2026-03-14 09:33:39
* @Entity generator.domain.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    @Select("select * from mianshima.question where updateTime >= #{pastTime}")
    List<Question> listQuestionWithDeletedByUpdateTime(LocalDateTime pastTime);
}




