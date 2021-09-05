package com.github.learndifferent.mtm.annotation.modify.string;

import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.DefaultValueIfEmpty;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 空字符串检查
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
public class EmptyStringCheckAspect {

    @Pointcut("@annotation(com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck)")
    public void pointcuts() {
    }

    @Around("pointcuts()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Object[] args = joinPoint.getArgs();

        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();

        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {

                if (annotation instanceof DefaultValueIfEmpty &&
                        String.class.isAssignableFrom(parameterTypes[i])) {

                    // 检查是否为空字符串或 null，并返回处理过的值
                    args[i] = replaceIfEmpty(args[i], (DefaultValueIfEmpty) annotation);
                    break;
                }

                if (annotation instanceof ExceptionIfEmpty &&
                        String.class.isAssignableFrom(parameterTypes[i])) {
                    throwExceptionIfEmpty(args[i], (ExceptionIfEmpty) annotation);
                    break;
                }
            }
        }

        return joinPoint.proceed(args);
    }

    private Object replaceIfEmpty(Object arg, DefaultValueIfEmpty defaultValue) {

        if (ObjectUtils.isNotEmpty(arg)) {
            // 不为空字符串或 null 的时候，不需要进行处理，直接返回即可
            return arg;
        }

        return defaultValue.value();
    }

    private void throwExceptionIfEmpty(Object arg, ExceptionIfEmpty exceptionInfo) {

        if (ObjectUtils.isNotEmpty(arg)) {
            // 不为空字符串或 null 的时候，不需要进行处理
            return;
        }

        ResultCode resultCode = exceptionInfo.resultCode();
        String errorMessage = exceptionInfo.errorMessage();
        throwExceptionWithInfo(resultCode, errorMessage);
    }

    private void throwExceptionWithInfo(ResultCode resultCode,
                                        String errorMessage) {

        if (StringUtils.isNotEmpty(errorMessage)) {
            throw new ServiceException(resultCode, errorMessage);
        }

        throw new ServiceException(resultCode);
    }
}
