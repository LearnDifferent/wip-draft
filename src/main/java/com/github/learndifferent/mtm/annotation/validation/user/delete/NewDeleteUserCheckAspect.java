package com.github.learndifferent.mtm.annotation.validation.user.delete;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Aspect
@Component
public class NewDeleteUserCheckAspect {

    private final UserService userService;

    @Autowired
    public NewDeleteUserCheckAspect(UserService userService) {
        this.userService = userService;
    }

    @Before("@annotation(annotation)")
    public void check(JoinPoint joinPoint, DeleteUserCheck annotation) {

        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            throw new ServiceException("No Request Attributes Available.");
        }

        ContentCachingRequestWrapper request =
                (ContentCachingRequestWrapper) requestAttributes.getRequest();

        // 获取参数名
        String usernameParamName = annotation.usernameParamName();
        String passwordParamName = annotation.passwordParamName();

        // 从 request 中获取值
        String username = getValueFromRequest(request, usernameParamName);
        String password = getValueFromRequest(request, passwordParamName);

        checkUserExists(username, password);
        checkDeletePermission(username);
    }

    private String getValueFromRequest(ContentCachingRequestWrapper request,
                                       String parameterName) {
        return request.getParameter(parameterName);
    }

    /**
     * 检查用户是否存在
     *
     * @param username 用户名
     * @param password 密码
     * @throws ServiceException 用户不存在异常 ResultCode.USER_NOT_EXIST
     */
    private void checkUserExists(String username, String password) {
        UserDTO user = userService.getUserByNameAndPwd(username, password);
        if (user == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
    }

    /**
     * 检查删除用户的权限：只有该用户有删除该用户的权限
     *
     * @param userName 用户名
     * @throws ServiceException 没有权限异常：ResultCode.PERMISSION_DENIED
     */
    private void checkDeletePermission(String userName) {

        String currentUsername = (String) StpUtil.getLoginId();

        if (ReverseUtils.compareStringNotEquals(currentUsername, userName)) {
            // 如果不是当前用户删除自己的帐号，就抛出异常
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }
}
