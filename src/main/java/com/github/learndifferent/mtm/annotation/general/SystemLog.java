package com.github.learndifferent.mtm.annotation.general;

import com.github.learndifferent.mtm.constant.enums.OptsType;

import java.lang.annotation.*;

/**
 * 系统日志
 *todo 测试
 * @author zhou
 * @date 2021/09/05
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLog {

    String title() default "";

    OptsType optsType() default OptsType.OTHERS;
}