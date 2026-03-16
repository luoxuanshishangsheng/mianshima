package com.echo.mianshima.model.dto.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.echo.mianshima.model.entity.Question;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(indexName = "question")
@Data
public class QuestionEsDto implements Serializable {
    private static final String DATA_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
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
     * 创建时间
     */
    @Field(type = FieldType.Date, format = {}, pattern = DATA_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date, format = {}, pattern = DATA_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    public static QuestionEsDto objToDto(Question question){
        if(question == null)
            return null;
        QuestionEsDto questionEsDao = BeanUtil.copyProperties(question, QuestionEsDto.class);
        String tagsStr = question.getTags();
        if(tagsStr != null){
            questionEsDao.setTags(JSONUtil.toList(tagsStr, String.class));
        }
        return questionEsDao;
    }

    public static Question dtoToObj(QuestionEsDto questionEsDao){
        if(questionEsDao == null)
            return null;
        Question question = BeanUtil.copyProperties(questionEsDao, Question.class);
        List<String> tags = questionEsDao.getTags();
        if(tags != null && !tags.isEmpty()){
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        return question;
    }
}
