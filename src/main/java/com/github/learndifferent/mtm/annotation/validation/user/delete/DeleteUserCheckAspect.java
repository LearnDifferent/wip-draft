package com.github.learndifferent.mtm.annotation.validation.user.delete;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 删除用户前查看是否可以删除
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
public class DeleteUserCheckAspect {

    private final UserService userService;

    @Autowired
    public DeleteUserCheckAspect(UserService userService) {
        this.userService = userService;
    }

    @Pointcut("@annotation(com.github.learndifferent.mtm.annotation.validation.user.delete.DeleteUserCheck)")
    public void pointcuts() {
    }

    @Before(value = "pointcuts()")
    public void check(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Method method = signature.getMethod();

        DeleteUserCheck annotation =
                method.getAnnotation(DeleteUserCheck.class);

        // 获取参数及其值
        String usernameParamName = annotation.usernameParamName();
        String passwordParamName = annotation.passwordParamName();
        String userNameValue = "";
        String passwordValue = "";

        int count = 0;

        for (int i = 0; i < parameterNames.length; i++) {
            if (usernameParamName.equalsIgnoreCase(parameterNames[i])
                    && ObjectUtils.isNotEmpty(args[i])
                    && String.class.isAssignableFrom(args[i].getClass())) {
                userNameValue = (String) args[i];
                count++;
            }

            if (passwordParamName.equalsIgnoreCase(parameterNames[i])
                    && ObjectUtils.isNotEmpty(args[i])
                    && String.class.isAssignableFrom(args[i].getClass())) {
                passwordValue = (String) args[i];
                count++;
            }

            if (count == 2) {
                break;
            }
        }

        checkUserExists(userNameValue, passwordValue);
        checkDeletePermission(userNameValue);
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
