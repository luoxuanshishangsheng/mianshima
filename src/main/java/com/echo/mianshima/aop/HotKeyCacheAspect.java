package com.echo.mianshima.aop;

import com.echo.mianshima.annotation.HotKeyCache;
import com.echo.mianshima.common.BaseResponse;
import com.echo.mianshima.common.ResultUtils;
import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class HotKeyCacheAspect {

    @Pointcut("@annotation(com.echo.mianshima.annotation.HotKeyCache)")
    public void hotKeyCachePointcut(){

    }

    @Around("hotKeyCachePointcut()")
    public Object hotKeyCache(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 拿到方法
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();

            // 拿到注解中的前缀
            HotKeyCache hotKeyCache = method.getDeclaredAnnotation(HotKeyCache.class);
            String prefix = hotKeyCache.prefix();

            // 从方法参数中取出 id 值
            Object arg = joinPoint.getArgs()[0];
            if(arg == null){
                return joinPoint.proceed();
            }
            Long id;
            if(arg instanceof Number){
                id = (Long) arg;
            } else {
                Method getIdMethod = arg.getClass().getMethod("getId");
                id = (Long) getIdMethod.invoke(arg);
            }
            String key = prefix + id;
            if(JdHotKeyStore.isHotKey(key)){
                Object cached = JdHotKeyStore.getValue(key);
                if(cached != null){
                    return ResultUtils.success(cached);
                }
            }
            BaseResponse<?> result = (BaseResponse<?>) joinPoint.proceed();
            JdHotKeyStore.smartSet(key, result.getData());
            return result;
        } catch (Throwable e) {
            log.error("JdHotKey出现异常，降级查询数据库！error：{}", e.getMessage());
            return joinPoint.proceed();
        }
    }
}
