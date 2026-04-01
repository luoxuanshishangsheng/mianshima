package com.echo.mianshima.aop;

import cn.dev33.satoken.stp.StpUtil;
import com.echo.mianshima.common.ErrorCode;
import com.echo.mianshima.constant.CrawlerDetectConstant;
import com.echo.mianshima.constant.UserConstant;
import com.echo.mianshima.exception.BusinessException;
import com.echo.mianshima.manager.CounterManager;
import com.echo.mianshima.model.entity.User;
import com.echo.mianshima.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class AntiCrawlerAspect {
    @Resource
    private CounterManager counterManager;
    @Resource
    private UserService userService;

    @Pointcut("@annotation(com.echo.mianshima.annotation.CrawlerDetect)")
    public void antiCrawlerPointCut(){

    }

    @Before("antiCrawlerPointCut()")
    public void antiCrawler(JoinPoint joinPoint){
        // 未登录直接抛异常
        StpUtil.checkLogin();
        Long loginId = StpUtil.getLoginIdAsLong();
        String key = CrawlerDetectConstant.USER_ACCESS_PREFIX + loginId;
        long count = counterManager.incrAndGetCounter(key, 1, TimeUnit.MINUTES, 180);
        if(count > CrawlerDetectConstant.BAN_COUNT) {
            // 踢下线
            StpUtil.kickout(loginId);

            // 封禁账号
            User currentUser = new User();
            currentUser.setId(loginId);
            currentUser.setUserRole(UserConstant.BAN_ROLE);
            userService.updateById(currentUser);

            // 提醒用户
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "访问太过频繁，账号已被封禁！");
        } else if (count == CrawlerDetectConstant.WARN_COUNT) {
            throw new BusinessException(110, "访问过于频繁，请减缓访问频率，否则账号将被封禁！");
        }
    }
}
