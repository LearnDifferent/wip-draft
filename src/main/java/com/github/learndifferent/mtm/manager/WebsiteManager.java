package com.github.learndifferent.mtm.manager;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.constant.enums.ShowPattern;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.dto.WebsitePatternDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithCountDTO;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 操作网页数据
 *
 * @author zhou
 * @date 2021/09/05
 */
@Component
public class WebsiteManager {

    private final WebsiteService websiteService;

    @Autowired
    public WebsiteManager(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    /**
     * 获取该 pattern 下，分页后需要的网页数据和总页数
     *
     * @param pattern  默认模式为查询所有，如果有指定的模式，就按照指定模式获取
     * @param pageInfo 分页相关信息
     * @param username 如果需要用户名，就传入用户名；如果不需要，就传入空字符串；
     * @return 该 pattern 下，分页后需要的网页数据和总页数
     */
    @EmptyStringCheck
    public WebsitePatternDTO getWebsiteByPattern(@EmptyStringCheck.DefaultValueIfEmpty String pattern,
                                                 PageInfoDTO pageInfo,
                                                 @EmptyStringCheck.DefaultValueIfEmpty String username) {

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        WebsitePatternDTO.WebsitePatternDTOBuilder builder = WebsitePatternDTO.builder();

        ShowPattern showPattern = castPatternStringToPatternEnum(pattern);

        switch (showPattern) {
            case MARKED:
                // 最多收藏的情况
                List<WebsiteWithCountDTO> markedWebs = websiteService.showMostMarked(from, size);
                int markedTotalCount = websiteService.countDistinctUrl();
                int markedTotalPage = PageUtil.getAllPages(markedTotalCount, size);
                builder.webs(markedWebs).totalPage(markedTotalPage);
                break;
            case USER_PAGE:
                // 查看某个用户所有收藏的情况
                List<WebsiteDTO> userPageWebs = websiteService.findWebsByUserPaging(username, from, size);
                int userPageTotalCount = websiteService.countUserPost(username);
                int userPageTotalPage = PageUtil.getAllPages(userPageTotalCount, size);
                builder.webs(userPageWebs).totalPage(userPageTotalPage);
                break;
            case WITHOUT_USER_PAGE:
                // 查看除去某个用户的所有收藏的情况
                List<WebsiteDTO> withoutUserPageWebs = websiteService.findWebsWithoutUserPaging(username, from, size);
                int withoutUserPageTotalCount = websiteService.countWithoutUserPost(username);
                int withoutUserPageTotalPage = PageUtil.getAllPages(withoutUserPageTotalCount, size);
                builder.webs(withoutUserPageWebs).totalPage(withoutUserPageTotalPage);
                break;
            case DEFAULT:
            default:
                // 默认查看全部的情况（如果 pattern 不是以上的情况，也是按照默认情况处理）
                List<WebsiteDTO> webs = websiteService.showAllWebDesc(from, size);
                int totalCount = websiteService.countAll();
                int totalPage = PageUtil.getAllPages(totalCount, size);
                builder.webs(webs).totalPage(totalPage);
        }

        return builder.build();
    }

    private ShowPattern castPatternStringToPatternEnum(String pattern) {

        pattern = camelToSnake(pattern).toUpperCase();

        try {
            return ShowPattern.valueOf(pattern);
        } catch (IllegalArgumentException | NullPointerException e) {
            // 找不到的时候，返回默认值
            return ShowPattern.DEFAULT;
        }
    }

    private String camelToSnake(String value) {
        return new PropertyNamingStrategy.SnakeCaseStrategy().translate(value);
    }
}
