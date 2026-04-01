package com.echo.mianshima.sentinel;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;

@Component
public class SentinelRuleConfig {
    @PostConstruct
    public void initRules(){
        initFlowRules();
        initDegradeRules();
    }

    // 初始化限流规则
    public void initFlowRules(){
        ParamFlowRule rule = new ParamFlowRule("listQuestionVOByPage")
                .setParamIdx(0)
                .setCount(60)
                .setDurationInSec(60);
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
    }

    // 初始化熔断降级规则
    public void initDegradeRules(){
        DegradeRule slowCallRule = new DegradeRule("listQuestionVOByPage")
                .setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType())
                .setCount(0.2)
                .setTimeWindow(60)
                .setStatIntervalMs(30 * 1000)
                .setMinRequestAmount(10)
                .setSlowRatioThreshold(3);

        DegradeRule errorRateRule = new DegradeRule("listQuestionVOByPage")
                .setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType())
                .setCount(0.1)
                .setTimeWindow(60)
                .setStatIntervalMs(30 * 1000)
                .setMinRequestAmount(10);

        DegradeRuleManager.loadRules(Arrays.asList(slowCallRule, errorRateRule));
    }
}
