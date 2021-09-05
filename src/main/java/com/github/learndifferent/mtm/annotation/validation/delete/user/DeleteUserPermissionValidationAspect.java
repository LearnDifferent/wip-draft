package com.github.learndifferent.mtm.annotation.validation.delete.user;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 是否有删除用户的权限
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
public class DeleteUserPermissionValidationAspect {

    @Pointcut("@annotation(com.github.learndifferent.mtm.annotation.validation.delete.user.DeleteUserPermissionValidation)")
    public void pointcuts() {
    }

    @Before(value = "pointcuts()")
    public void check(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?>[] parameterTypes = signature.getParameterTypes();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Method method = signature.getMethod();

        DeleteUserPermissionValidation annotation = method
                .getAnnotation(DeleteUserPermissionValidation.class);

        String usernameParamName = annotation.usernameParamName();
        String userNameValue = "";

        for (int i = 0; i < parameterTypes.length; i++) {
            if (usernameParamName.equalsIgnoreCase(parameterNames[i])
                    && String.class.isAssignableFrom(parameterTypes[i]) &&
                    !StringUtils.isEmpty(args[i])) {

                userNameValue = (String) args[i];
                break;
            }
        }

        // 如果没有删除权限，就抛出异常
        checkDeletePermission(userNameValue);
    }

    /**
     * 检查删除用户的权限：只有 admin 和该用户有删除该用户的权限
     *
     * @param userName 传入的用户
     */
    private void checkDeletePermission(String userName) {

        String currentUsername = (String) StpUtil.getLoginId();

        if (!StpUtil.hasRole(RoleType.ADMIN.role())
                && !currentUsername.equals(userName)) {
            // 如果不是管理员，且要删除的用户不是当前用户，就抛出异常
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }
}
