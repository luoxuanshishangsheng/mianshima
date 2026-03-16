package com.echo.mianshima.esdao;

import com.echo.mianshima.model.dto.question.QuestionEsDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface QuestionEsDao extends ElasticsearchRepository<QuestionEsDto, Long> {
}
