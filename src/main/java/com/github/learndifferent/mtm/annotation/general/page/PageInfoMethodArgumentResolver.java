package com.github.learndifferent.mtm.annotation.general.page;

import com.github.learndifferent.mtm.constant.enums.PageInfoMode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.utils.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 页面信息方法参数解析器
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
public class PageInfoMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PageInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        PageInfo annotation = parameter.getParameterAnnotation(PageInfo.class);

        if (annotation == null) {
            throw new ServiceException("No available annotation of Page Info.");
        }

        // 获取传入的参数名
        String paramName = annotation.paramName();
        // 获取传入的参数值（有可能为 null）
        String paramValue = webRequest.getParameter(paramName);

        // 需要的参数
        PageInfoMode mode = annotation.pageInfoMode();
        int size = annotation.size();

        // 参数的字符串值，转化为数字后的值
        int num = 0;

        // 如果有值，就转化为 int（为空或 null 或无法转化为数字的时候，默认为 0）
        num = getNumIfNotEmpty(paramValue, num);

        PageInfoDTO.PageInfoDTOBuilder infoBuilder = PageInfoDTO.builder();

        switch (mode) {
            case FROM_MODE:
                // 此时，num 表示 from
                infoBuilder.from(num).size(size);
                break;
            case CURRENT_PAGE_MODE:
            default:
                // 此时，num 表示 current page
                int currentPage = PageUtil.constrainGreaterThanZero(num);
                int from = PageUtil.getFromIndex(currentPage, size);
                infoBuilder.from(from).size(size);
        }

        return infoBuilder.build();
    }

    private int getNumIfNotEmpty(String paramValue, int num) {

        if (StringUtils.isNotEmpty(paramValue)) {
            try {
                num = Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                // 如果输入的字符串无法转化为数字，就打印日志，然后使用默认值
                e.printStackTrace();
                log.warn("输入的不是数字");
            }
        }
        return num;
    }
}
