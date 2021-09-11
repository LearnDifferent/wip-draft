package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.WebForSearchDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithCountDTO;
import com.github.learndifferent.mtm.query.WebFilter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * WebsiteService
 *
 * @author zhou
 * @date 2021/09/05
 */
public interface WebsiteService {

    /**
     * 根据 WebFilter 筛选器来筛选网页数据
     *
     * @param filter 筛选器，筛选器包含：
     *               需要加载多少条数据，
     *               所有用户名（没有用户名的时候表示查找所有），
     *               日期（可以是一个或两个，没有的时候表示查找所有）
     * @return 筛选出来的网页
     */
    List<WebsiteDTO> findWebsitesDataByFilter(WebFilter filter);

    /**
     * 获取最多用户收藏的 URL 及其网页信息
     *
     * @param from 分页起始
     * @param size 页面大小
     * @return 最多用户收藏的网页及其信息
     */
    List<WebsiteWithCountDTO> showMostMarked(@Param("from") int from,
                                             @Param("size") int size);

    /**
     * 计算所有数据
     *
     * @return 一共有多少条数据
     */
    int countAll();

    /**
     * 计算 URL 出现的次数，并剔除重复
     *
     * @return 一共有多少条数据
     */
    int countDistinctUrl();

    /**
     * 倒序查询所有网页（分页）
     *
     * @param from 起始
     * @param size 页面大小
     * @return 查询到的网页
     */
    List<WebsiteDTO> showAllWebsiteDataDesc(int from, int size);

    /**
     * 倒序查询所有网页
     *
     * @return 查询到的网页
     */
    List<WebsiteDTO> showAllWebsiteDataDesc();

    /**
     * 计算某个用户收藏的网页的总数
     *
     * @param userName 某个用户
     * @return 一共有多少条数据
     */
    int countUserPost(String userName);

    /**
     * 计算除去某个用户收藏的网页的总数
     *
     * @param userName 除去某个用户
     * @return 一共有多少条数据
     */
    int countExcludeUserPost(String userName);

    /**
     * 查找除去某个用户的所有网页
     *
     * @param userName 不查找该用户 / 某个用户
     * @param from     from
     * @param size     size
     * @return 除去某个用户的所有网页
     */
    List<WebsiteDTO> findWebsitesDataExcludeUser(@Param("userName") String userName,
                                                 @Param("from") int from,
                                                 @Param("size") int size);

    /**
     * 查找某个用户的收藏
     *
     * @param userName 某个用户
     * @param from     from
     * @param size     size
     * @return 某个用户的所有收藏
     */
    List<WebsiteDTO> findWebsitesDataByUser(@Param("userName") String userName,
                                            @Param("from") int from,
                                            @Param("size") int size);

    /**
     * 通过id找到网页数据
     *
     * @param webId id
     * @return {@code WebsiteDTO}
     */
    WebsiteDTO findWebsiteDataById(int webId);

    /**
     * 保存没有 ID、用户名和创建时间的网页数据，并添加用户信息，生成时间（ID 会在数据库中生成）。
     * <p>如果已经收藏过了，就不能收藏第二次，会抛出异常</p>
     *
     * @param rawWebsite 没有 ID、用户名和创建时间的网页数据
     * @param userName   保存该网页的用户
     * @return 是否成功
     */
    boolean saveWebsiteData(WebWithNoIdentityDTO rawWebsite, String userName);

    /**
     * 根据链接，获取网页的 title、url、img 和简介数据
     *
     * @param url      网页链接
     * @param userName 收藏该网页的用户
     * @return 网页信息
     */
    WebWithNoIdentityDTO scrapeWebsiteDataFromUrl(String url, String userName);

    /**
     * 获取所有用于 Elasticsearch 的数据
     *
     * @return 用于 Elasticsearch 的数据
     */
    List<WebForSearchDTO> getAllWebsitesDataForSearch();

    /**
     * 通过 ID 更新网页
     *
     * @param websiteDO 新的网页数据
     * @return 是否成功
     */
    boolean updateWebsiteDataById(WebsiteDTO websiteDO);

    /**
     * 通过url找到网页数据
     *
     * @param url url
     * @return {@code List<WebsiteDTO>}
     */
    List<WebsiteDTO> findWebsitesDataByUrl(String url);

    /**
     * 通过id删除网页数据
     *
     * @param webId id
     * @return boolean
     */
    boolean delWebsiteDataById(int webId);
}
