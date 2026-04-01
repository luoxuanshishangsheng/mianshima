package com.echo.mianshima.sentinel;

import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echo.mianshima.common.BaseResponse;
import com.echo.mianshima.common.ErrorCode;
import com.echo.mianshima.common.ResultUtils;
import com.echo.mianshima.model.dto.questionBank.QuestionBankQueryRequest;
import com.echo.mianshima.model.vo.QuestionBankVO;

import javax.servlet.http.HttpServletRequest;

public class ListQuestionBankVOByPageHandle {

    /**
     *
     * @param questionBankQueryRequest 原始参数
     * @param request 原始参数
     * @param be 额外接受一个异常
     * @return 返回值需和原方法一致
     */
    public static BaseResponse<Page<QuestionBankVO>> handleBlockException(QuestionBankQueryRequest questionBankQueryRequest,
                                                                              HttpServletRequest request, BlockException be){
        if(be instanceof DegradeException){
            return ListQuestionBankVOByPageHandle.handleFallback(questionBankQueryRequest, request, be);
        }

        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统压力过大，请稍后再试！");
    }

    public static BaseResponse<Page<QuestionBankVO>> handleFallback(QuestionBankQueryRequest questionBankQueryRequest,
                                                                                         HttpServletRequest request, Throwable t){
        Tracer.trace(t);
        return ResultUtils.success(null);
    }
}
