package com.github.learndifferent.mtm.annotation.modify.url;

import com.github.learndifferent.mtm.utils.CleanUrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 用于固定 URL 格式
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class UrlCleanAspect {

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint pjp, UrlClean annotation) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();

        Class<?>[] parameterTypes = signature.getParameterTypes();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        String urlParamName = annotation.urlParamName();

        // 是否找到 url 参数
        boolean findUrl = false;

        for (int i = 0; i < parameterNames.length; i++) {
            if (urlParamName.equalsIgnoreCase(parameterNames[i]) &&
                    String.class.isAssignableFrom(parameterTypes[i]) &&
                    !StringUtils.isEmpty(args[i])) {

                args[i] = CleanUrlUtil.cleanup((String) args[i]);
                findUrl = true;
                break;
            }
        }

        if (!findUrl) {
            log.info("没有找到 URL，请检查参数名称是否正确");
        }

        return pjp.proceed(args);
    }
}
