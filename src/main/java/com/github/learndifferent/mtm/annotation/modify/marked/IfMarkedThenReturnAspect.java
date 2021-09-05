package com.github.learndifferent.mtm.annotation.modify.marked;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 如果已经收藏了，就返回
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
@Order(2)
public class IfMarkedThenReturnAspect {

    private WebsiteService websiteService;

    @Autowired
    public void setWebsiteService(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint pjp, IfMarkedThenReturn annotation) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Class<?>[] parameterTypes = signature.getParameterTypes();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = pjp.getArgs();

        String urlParamName = annotation.urlParamName();
        String usernameParamName = annotation.usernameParamName();

        String url = "";
        String username = "";
        int count = 0;

        for (int i = 0; i < parameterNames.length; i++) {

            if (urlParamName.equals(parameterNames[i]) &&
                    String.class.isAssignableFrom(parameterTypes[i]) &&
                    args[i] != null) {
                url = (String) args[i];
                count++;
            }

            if (usernameParamName.equals(parameterNames[i]) &&
                    String.class.isAssignableFrom(parameterTypes[i]) &&
                    args[i] != null) {
                username = (String) args[i];
                count++;
            }

            if (count == 2) {
                break;
            }
        }

        // 先在数据库中查找是否有相同 URL 的网页数据
        List<WebsiteDTO> websInDb = websiteService.findWebsByUrl(url);

        // 如果数据库中存在该链接的网页数据
        if (!websInDb.isEmpty()) {
            // 先检查用户是否已经收藏了该网页（收藏了会报错）
            checkIfMarked(username, websInDb);
            // 如果该用户还没有收藏，就直接返回数据库中已经查找到的数据
            return DozerUtils.convert(websInDb.get(0), WebWithNoIdentityDTO.class);
        }

        // 如果数据库中没有，就按照原来的值
        return pjp.proceed();
    }

    /**
     * 根据用户名和网页列表，查看用户是否已经收藏了该网页列表内的网页。
     * <p>如果已经收藏过了，就抛出异常</p>
     *
     * @param userName 用户名
     * @param websInDb 网页数据列表
     * @throws ServiceException 如果已经收藏过了，就抛出异常
     */
    private void checkIfMarked(String userName, List<WebsiteDTO> websInDb) {
        // 查看该用户是否已经收藏过了
        WebsiteDTO webUserMarked = websInDb.stream()
                .filter(w -> w.getUserName().equals(userName))
                .findFirst().orElse(null);

        if (webUserMarked != null) {
            // 如果已经收藏过了，抛出异常
            throw new ServiceException(ResultCode.ALREADY_MARKED);
        }
    }

}
