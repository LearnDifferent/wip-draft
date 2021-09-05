package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.modify.marked.IfMarkedThenReturn;
import com.github.learndifferent.mtm.annotation.modify.url.UrlClean;
import com.github.learndifferent.mtm.annotation.validation.marked.UserAlreadyMarkedCheck;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebForSearchDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithCountDTO;
import com.github.learndifferent.mtm.entity.WebsiteDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.query.WebFilter;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.ShortenUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * WebsiteService 实现类
 *
 * @author zhou
 * @date 2021/09/05
 */
@Service
public class WebsiteServiceImpl implements WebsiteService {

    private final WebsiteMapper websiteMapper;
    private final ElasticsearchManager elasticsearchManager;

    @Autowired
    public WebsiteServiceImpl(WebsiteMapper websiteMapper, ElasticsearchManager elasticsearchManager) {
        this.websiteMapper = websiteMapper;
        this.elasticsearchManager = elasticsearchManager;
    }

    @Override
    public List<WebsiteDTO> findWebsiteByFilter(WebFilter filter) {
        List<WebsiteDO> webs = websiteMapper.findWebsiteByFilter(filter);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public List<WebsiteWithCountDTO> showMostMarked(int from, int size) {
        return websiteMapper.showMostMarked(from, size);
    }

    @Override
    public int countAll() {
        return websiteMapper.countAll();
    }

    @Override
    public int countDistinctUrl() {
        return websiteMapper.countDistinctUrl();
    }

    @Override
    public List<WebsiteDTO> showAllWebDesc(int from, int size) {
        List<WebsiteDO> webs = websiteMapper.showAllWebDesc(from, size);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public List<WebsiteDTO> showAllWebDesc() {
        List<WebsiteDO> webs = websiteMapper.showAllWebDesc();
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public int countUserPost(String userName) {
        return websiteMapper.countUserPost(userName);
    }

    @Override
    public int countWithoutUserPost(String userName) {
        return websiteMapper.countWithoutUserPost(userName);
    }

    @Override
    public List<WebsiteDTO> findWebsWithoutUserPaging(String userName, int from, int size) {
        List<WebsiteDO> webs = websiteMapper.findWebsWithoutUserPaging(userName, from, size);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public List<WebsiteDTO> findWebsByUserPaging(String userName, int from, int size) {
        List<WebsiteDO> webs = websiteMapper.findWebsByUserPaging(userName, from, size);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public WebsiteDTO findWebById(int webId) {
        WebsiteDO web = websiteMapper.getWebById(webId);
        return DozerUtils.convert(web, WebsiteDTO.class);
    }

    @Override
    @UserAlreadyMarkedCheck(
            usernameParamName = "userName",
            paramNameContainsUrl = "rawWebsite",
            paramClassContainsUrl = WebWithNoIdentityDTO.class,
            urlFieldName = "url")
    public boolean saveWebWithNoIdentity(WebWithNoIdentityDTO rawWebsite, String userName) {

        // 异步，放入 Elasticsearch
        WebForSearchDTO searchWeb = DozerUtils.convert(rawWebsite, WebForSearchDTO.class);
        elasticsearchManager.saveDoc(searchWeb);

        // 添加信息后，放入数据库
        WebsiteDO websiteDO = DozerUtils.convert(rawWebsite, WebsiteDO.class);
        return websiteMapper.addWeb(websiteDO
                .setUserName(userName).setCreateTime(new Date()));
    }

    /**
     * 先经过 @UrlClean 来清理 URL 的格式，
     * 然后经过 @IfMarkedThenReturn 来查找网页数据是否已经存在数据库中。
     * <p>如果已经存在数据库中，且用户已经收藏过，就抛出异常；
     * 如果已经存在，且用户没有收藏，就返回数据库内的数据。</p>
     * <p>如果没有在数据库中，就从网页中抓取并返回</p>
     *
     * @param url      网页链接
     * @param userName 收藏该网页的用户
     * @return 剔除了唯一信息的网页数据
     * @throws ServiceException 会判断并抛出异常
     */
    @UrlClean(urlParamName = "url")
    @IfMarkedThenReturn(urlParamName = "url", usernameParamName = "userName")
    @Override
    public WebWithNoIdentityDTO grabRawWebByUrl(String url, String userName) {

        try {
            Document document = Jsoup.parse(new URL(url), 3000);
//        Document document = Jsoup.connect(url).get(); // 这样无法抛出网页格式异常

            // 如果获取到了，就保存
            String title = ShortenUtils.shorten(document.title(), 47);
            String img = getFirstImg(document);
            String desc = getDesc(document);

            // 如果无法查询到网页名称，就默认设定为 url
            if (StringUtils.isEmpty(title)) {
                title = ShortenUtils.shorten(url, 30);
            }
            // 如果无法查询到简介，就默认设定为 url
            if (StringUtils.isEmpty(desc)) {
                desc = ShortenUtils.shorten(url, 30);
            }

            return WebWithNoIdentityDTO.builder()
                    .title(title).url(url).img(img).desc(desc).build();
        } catch (IOException e) {
            if (e instanceof MalformedURLException) {
                throw new ServiceException(ResultCode.URL_MALFORMED);
            } else if (e instanceof SocketTimeoutException) {
                throw new ServiceException(ResultCode.URL_ACCESS_DENIED);
            } else {
                throw new ServiceException(ResultCode.SAVE_FAILED);
            }
        }
    }

    private String getDesc(Document document) {
        String text = document.body().text();
        return ShortenUtils.shorten(ShortenUtils.flatten(text), 260);
    }

    private String getFirstImg(Document document) {
        Elements images = document.select("img[src]");

        if (images.size() >= 1) {
            return images.get(0).attr("abs:src");
        }
        // 如果没有图片，就返回一个默认图片地址
        return "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F00%2F93%2F63%2F2656f2a6a663e1c.jpg&refer=http%3A%2F%2Fbpic.588ku.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1618635152&t=e26535a2d80f40281592178ee20ee656";
    }

    @Override
    public List<WebForSearchDTO> getAllWebForSearch() {
        return websiteMapper.getAllWebForSearch();
    }

    @Override
    public boolean updateWebById(WebsiteDTO website) {
        WebsiteDO websiteDO = DozerUtils.convert(website, WebsiteDO.class);
        return websiteMapper.updateWebById(websiteDO);
    }

    @Override
    public List<WebsiteDTO> findWebsByUrl(String url) {
        List<WebsiteDO> webs = websiteMapper.findAllWebsByUrl(url);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public boolean delWebById(int webId) {
        return websiteMapper.delWebById(webId);
    }
}
