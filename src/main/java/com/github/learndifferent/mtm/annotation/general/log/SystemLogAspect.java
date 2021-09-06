package com.github.learndifferent.mtm.annotation.general.log;

import com.github.learndifferent.mtm.constant.enums.LogStatus;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.manager.AsyncLogManager;
import com.github.learndifferent.mtm.vo.SysLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
@Aspect
@Component
public class SystemLogAspect {

    private final AsyncLogManager asyncLogManager;

    @Autowired
    public SystemLogAspect(AsyncLogManager asyncLogManager) {
        this.asyncLogManager = asyncLogManager;
    }

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint pjp, SystemLog annotation) {

        String title = annotation.title();
        OptsType optsType = annotation.optsType();

        SysLog.SysLogBuilder sysLog = SysLog.builder();
        sysLog.title(title).optType(optsType.value()).optTime(new Date());

        MethodSignature signature = (MethodSignature) pjp.getSignature();

        Method method = signature.getMethod();
        String methodName = method.getName();
        String className = signature.getClass().getName();
        sysLog.method(className + "." + methodName + "()");

        sysLog.status(LogStatus.NORMAL.status())
                .msg(LogStatus.NORMAL.name());

        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            sysLog.status(LogStatus.ERROR.status())
                    .msg(throwable.getMessage());
        }
        asyncLogManager.saveSysLog(sysLog.build());
        return result;
    }
}
