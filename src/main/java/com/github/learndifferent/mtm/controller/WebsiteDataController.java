package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.validation.delete.website.DeleteWebsitePermission;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.vo.WebUrlAndNameVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 获取和存储网页相关
 *
 * @author zhou
 * @date 2021/09/05
 */

@RestController
@RequestMapping("/web")
public class WebsiteDataController {

    private final WebsiteService websiteService;

    @Autowired
    public WebsiteDataController(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    /**
     * 根据 ID 删除网页
     *
     * @param webId ID
     * @return 返回是否成功的消息（如果不是收藏该网页的用户请求删除，则返回无权限的消息）
     * @throws ServiceException 如果没有删除的权限，会抛出异常
     */
    @DeleteMapping
    @DeleteWebsitePermission(webIdParamName = "webId", usernameParamName = "userName")
    public ResultVO<?> deleteWebsiteData(@RequestParam("webId") int webId) {

        boolean success = websiteService.delWebsiteDataById(webId);
        return success ? ResultCreator.result(ResultCode.DELETE_SUCCESS)
                : ResultCreator.result(ResultCode.DELETE_FAILED);
    }

    /**
     * 根据用户名和没有 ID、用户名和创建时间的网页数据来收藏网页
     *
     * @param website  没有 ID、用户名和创建时间的网页数据
     * @param userName 收藏该网页的用户名称
     * @return 是否收藏成功？（如果已经收藏过了，就不能收藏第二次，会报错）
     */
    @PostMapping
    public ResultVO<?> saveWebsiteData(
            @RequestBody WebWithNoIdentityDTO website
            , @RequestParam("userName") String userName) {

        // 第三个参数传入 false，表示不要同步到 Elasticsearch。因为该数据已经在 Elasticsearch 中存在
        boolean success = websiteService.saveWebsiteData(website, userName, false);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * 根据 URL 和用户名，收藏新的网页数据
     *
     * @param urlAndName 网页链接和用户信息
     * @param syncToEs   是否同步数据到 Elasticsearch
     * @return 响应文本
     */
    @SystemLog(title = "Mark", optsType = OptsType.CREATE)
    @PostMapping("/add")
    public ResultVO<?> saveWebsiteData(@RequestBody WebUrlAndNameVO urlAndName,
                                       @RequestParam("syncToEs") boolean syncToEs) {

        String userName = urlAndName.getUserName();
        String url = urlAndName.getUrl();

        WebWithNoIdentityDTO rawWebsite = websiteService.scrapeWebsiteDataFromUrl(url, userName);

        boolean success = websiteService.saveWebsiteData(rawWebsite, userName, syncToEs);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }
}
