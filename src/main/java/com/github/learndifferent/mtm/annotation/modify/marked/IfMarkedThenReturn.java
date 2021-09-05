package com.github.learndifferent.mtm.annotation.modify.marked;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果已经收藏了，就返回
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IfMarkedThenReturn {

    String urlParamName();

    String usernameParamName();
}
