package com.github.learndifferent.mtm.annotation.general.page;

import com.github.learndifferent.mtm.constant.consist.PageInfoConstant;
import com.github.learndifferent.mtm.constant.enums.PageInfoMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 页面信息
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageInfo {

    /**
     * 选择什么模式来获取 Page 信息。
     * <p>PageInfoMode.CURRENT_PAGE_MODE 表示使用 currentPage，
     * PageInfoMode.FROM_MODE 表示使用 from</p>
     *
     * @return 选择的模式
     */
    PageInfoMode pageInfoMode() default PageInfoMode.CURRENT_PAGE_MODE;

    /**
     * 传入的参数的名称。
     * <p>如果是 PageInfoMode.CURRENT_PAGE_MODE 模式，就是 currentPage 属性的参数名称</p>
     * <p>如果是 PageInfoMode.FROM_MODE 模式，就是 from 属性的参数名称。</p>
     *
     * @return 传入的参数的名称
     */
    String paramName() default PageInfoConstant.CURRENT_PAGE;

    /**
     * size 属性的值，默认为 10
     *
     * @return size 属性的值
     */
    int size() default 10;
}
