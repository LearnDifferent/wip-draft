package com.github.learndifferent.mtm.annotation.validation.marked;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.WebsiteService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 根据 username 和 url，查看用户是否已经收藏了该网页
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Aspect
@Component
public class UserAlreadyMarkedCheckAspect {

    private WebsiteService websiteService;

    @Autowired
    public void setWebsiteMapper(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    @Before("@annotation(annotation)")
    public void check(JoinPoint joinPoint, UserAlreadyMarkedCheck annotation) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Class<?>[] parameterTypes = signature.getParameterTypes();
        Object[] args = joinPoint.getArgs();

        String paramNameContainsUrl = annotation.paramNameContainsUrl();
        Class<? extends Serializable> classContainsUrl = annotation.paramClassContainsUrl();
        String urlFieldName = annotation.urlFieldName();

        String usernameParamName = annotation.usernameParamName();

        int count = 0;
        String url = "";
        String username = "";

        for (int i = 0; i < parameterNames.length; i++) {
            if (paramNameContainsUrl.equals(parameterNames[i]) &&
                    classContainsUrl.isAssignableFrom(parameterTypes[i]) &&
                    args[i] != null) {

                url = getUrl(urlFieldName, args[i]);
                count++;
            }

            if (usernameParamName.equals(parameterNames[i]) &&
                    String.class.isAssignableFrom(parameterTypes[i]) &&
                    !StringUtils.isEmpty(args[i])) {
                username = (String) args[i];
                count++;
            }

            if (count == 2) {
                break;
            }
        }

        testIfUserMarkedWeb(username, url);
    }

    private String getUrl(String urlFieldName, Object arg) {

        Class<?> clazz = arg.getClass();
        try {
            Field urlField = clazz.getDeclaredField(urlFieldName);
            urlField.setAccessible(true);
            return (String) urlField.get(arg);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            log.warn("无法获取 URL，已转换为空字符串");
            return "";
        }
    }

    /**
     * 查看用户是否已经收藏了该网站
     *
     * @param userName 需要收藏该网站的用户
     * @param url      网页链接
     * @throws ServiceException 如果已经收藏了，就抛出异常
     */
    private void testIfUserMarkedWeb(String userName, String url) {

        WebsiteDTO web = websiteService.findWebsByUrl(url).stream()
                .filter(w -> w.getUserName().equals(userName))
                .findFirst().orElse(null);

        if (web != null) {
            // 如果该用户已经收藏了该网页，就抛出异常
            throw new ServiceException(ResultCode.ALREADY_MARKED);
        }
    }

}
