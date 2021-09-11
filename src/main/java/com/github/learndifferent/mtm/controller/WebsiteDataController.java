package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.validation.delete.website.DeleteWebsitePermission;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.vo.WebUrlAndNameVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    private final ElasticsearchManager elasticsearchManager;

    @Autowired
    public WebsiteDataController(WebsiteService websiteService,
                                 ElasticsearchManager elasticsearchManager) {
        this.websiteService = websiteService;
        this.elasticsearchManager = elasticsearchManager;
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

        boolean success = websiteService.saveWebsiteData(website, userName);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * 根据 URL 和用户名，收藏新的网页数据
     *
     * @param urlAndName          网页链接和用户信息
     * @param syncToElasticsearch 是否同步数据到 Elasticsearch
     * @return {@code boolean[]} boolean 数组 index 为 0 的位置表示是否存放到数据库中，
     * boolean 数组 index 为 1 的位置表示是否存放到 Elasticsearch 中。
     */
    @SystemLog(title = "Mark", optsType = OptsType.CREATE)
    @PostMapping("/add")
    public boolean[] saveWebsiteData(@RequestBody WebUrlAndNameVO urlAndName,
                                     @RequestParam("syncToElasticsearch") boolean syncToElasticsearch) {

        String userName = urlAndName.getUserName();
        String url = urlAndName.getUrl();

        WebWithNoIdentityDTO rawWebsite = websiteService.scrapeWebsiteDataFromUrl(url, userName);

        // 如果选择同步到 Elasticsearch 中，就异步执行保存网页数据的方法并返回结果
        // 如果选择不同步，也就是 syncToEs 为 false 的情况：直接返回 true 作为结果，表示无需异步存放
        Future<Boolean> futureResult = elasticsearchManager
                .saveDocAsync(rawWebsite, syncToElasticsearch);

        // 将网页数据放入数据库，返回是否成功放入
        boolean toDatabase = websiteService.saveWebsiteData(rawWebsite, userName);

        // true 表示存放成功或无需存放到 Elasticsearch；false 表示存放失败
        boolean toElasticsearch = true;
        try {
            // 获取异步存放数据到 Elasticsearch 的结果
            toElasticsearch = futureResult.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        return new boolean[]{toDatabase, toElasticsearch};
    }
}
