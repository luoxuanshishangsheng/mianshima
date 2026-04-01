package com.echo.mianshima.sentinel;

import com.alibaba.csp.sentinel.Tracer;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echo.mianshima.common.BaseResponse;
import com.echo.mianshima.common.ResultUtils;
import com.echo.mianshima.model.dto.question.QuestionQueryRequest;
import com.echo.mianshima.model.vo.QuestionVO;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public class ListQuestionVOByPageHandle {
    public static BaseResponse<Page<QuestionVO>> handleFallback(QuestionQueryRequest questionQueryRequest,
                                                                HttpServletRequest request, Throwable t) {
        return ResultUtils.success(null);
    }
}
