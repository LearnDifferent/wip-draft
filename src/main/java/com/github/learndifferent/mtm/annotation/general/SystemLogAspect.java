package com.github.learndifferent.mtm.annotation.general;


import com.github.learndifferent.mtm.manager.AsyncLogManager;
import com.github.learndifferent.mtm.vo.SysLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 系统日志
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
@Slf4j
public class SystemLogAspect {

    @Autowired
    private AsyncLogManager asyncLogService;

    @Pointcut("@annotation(com.github.learndifferent.mtm.annotation.general.SystemLog)")
    public void logPointCut() {
    }

    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }


    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {

            SystemLog controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) {
                return;
            }

            SysLog.SysLogBuilder sysLogBuilder = SysLog.builder();

            sysLogBuilder.status(0);

            if (e != null) {
                sysLogBuilder
                        .status(1)
                        .errorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }

            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            sysLogBuilder.method(className + "." + methodName + "()");

            SysLog sysLog = sysLogBuilder.build();
            getControllerMethodDescription(joinPoint, controllerLog, sysLog);

            asyncLogService.saveSysLog(sysLog);
        } catch (Exception exp) {
            log.error("==前置通知异常==");
            log.error("日志异常信息 {}", exp);
        }
    }

    public void getControllerMethodDescription(JoinPoint joinPoint, SystemLog log, SysLog sysLog) {

        sysLog.setOptType(log.optsType().ordinal());

        sysLog.setTitle(log.title());
    }

    private SystemLog getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(SystemLog.class);
        }
        return null;
    }
}
