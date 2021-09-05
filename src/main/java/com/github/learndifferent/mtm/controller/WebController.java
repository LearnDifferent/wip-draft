package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.validation.delete.website.DeleteWebsitePermission;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.vo.WebUrlAndNameVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 获取和存储网页相关
 *
 * @author zhou
 */

@RestController
@RequestMapping("/web")
public class WebController {

    private WebsiteService websiteService;

    @Autowired
    public void setWebsiteService(WebsiteService websiteService) {
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
    public ResultVO<?> delWeb(@RequestParam("webId") int webId) {

        boolean success = websiteService.delWebById(webId);
        return success ? ResultCreator.result(ResultCode.DELETE_SUCCESS)
                : ResultCreator.result(ResultCode.DELETE_FAILED);
    }

    /**
     * 根据用户名和网页数据收藏网页
     *
     * @param website  没有 ID、用户名和创建时间的网页数据
     * @param userName 收藏该网页的用户名称
     * @return 是否收藏成功？（如果已经收藏过了，就不能收藏第二次，会报错）
     */
    @PostMapping
    public ResultVO<?> saveWebWithNoIdentity(
            @RequestBody WebWithNoIdentityDTO website
            , @RequestParam("userName") String userName) {

        boolean success = websiteService.saveWebWithNoIdentity(website, userName);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * 根据 URL 和用户名，收藏新的网页
     *
     * @param urlAndName 网页链接和用户信息
     * @return 响应文本
     */
    @PostMapping("/add")
    public ResultVO<?> saveWeb(@RequestBody WebUrlAndNameVO urlAndName) {

        String userName = urlAndName.getUserName();
        String url = urlAndName.getUrl();

        WebWithNoIdentityDTO rawWebsite = websiteService.grabRawWebByUrl(url, userName);

        boolean success = websiteService.saveWebWithNoIdentity(rawWebsite, userName);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }
}
