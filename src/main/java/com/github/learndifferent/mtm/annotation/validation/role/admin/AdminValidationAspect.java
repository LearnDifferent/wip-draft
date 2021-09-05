package com.github.learndifferent.mtm.annotation.validation.role.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 检查是否为管理员，如果不是管理员账户，就抛出异常
 *
 * @author zhou
 */
@Aspect
@Component
public class AdminValidationAspect {

    @Pointcut("@annotation(com.github.learndifferent.mtm.annotation.validation.role.admin.AdminValidation)")
    public void pointcuts() {
    }

    @Before(value = "pointcuts()")
    public void checkAdmin() {
        if (!StpUtil.hasRole(RoleType.ADMIN.role())) {
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }
}
