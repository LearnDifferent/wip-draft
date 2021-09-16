package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.SearchResultsDTO;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import com.github.learndifferent.mtm.manager.TrendsManager;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.vo.FindPageInitVO;
import com.github.learndifferent.mtm.vo.SearchResultsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 查找页面的 Controller
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/find")
public class FindController {

    private final ElasticsearchManager elasticsearchManager;
    private final TrendsManager trendsManager;

    @Autowired
    public FindController(ElasticsearchManager elasticsearchManager,
                          TrendsManager trendsManager) {
        this.elasticsearchManager = elasticsearchManager;
        this.trendsManager = trendsManager;
    }

    /**
     * 载入的时候，获取热搜数据和是否存在可供搜索的数据
     *
     * @return 热搜数据和数据库是否存在
     */
    @GetMapping
    public ResultVO<FindPageInitVO> load() {

        Set<String> trendingList = trendsManager.getTrends();
        boolean exist = elasticsearchManager.existsIndex();

        FindPageInitVO data = FindPageInitVO.builder()
                .trendingList(trendingList).dataStatus(exist).build();

        return ResultCreator.okResult(data);
    }

    /**
     * 删除某个热搜词
     *
     * @param word 被删除的热搜词
     * @return 是否删除成功
     */
    @DeleteMapping("/trends/{word}")
    public boolean delTrendsByWord(@PathVariable("word") String word) {
        return trendsManager.delTrendsByWord(word);
    }

    /**
     * 删除所有热搜词
     *
     * @return 是否成功
     */
    @DeleteMapping("/trends")
    public boolean delAllTrends() {
        return trendsManager.delAllTrends();
    }

    /**
     * 搜索并返回查询到的网页数据
     *
     * @param pageInfo 分页数据
     * @param keyword  关键词（可以为空字符串或 null）
     * @return 查询到的网页结果
     */
    @GetMapping("/search")
    public ResultVO<SearchResultsVO> search(@PageInfo PageInfoDTO pageInfo
            , @RequestParam("keyword") String keyword) {

        SearchResultsDTO searchResultsDTO = elasticsearchManager.getSearchResult(keyword, pageInfo);
        SearchResultsVO results = DozerUtils.convert(searchResultsDTO, SearchResultsVO.class);

        return ResultCreator.okResult(results);
    }

    /**
     * 根据数据库中的数据重新生成 Elasticsearch 的数据库
     *
     * @return 是否成功
     */
    @GetMapping("/build")
    public boolean reGenerateSearchDataBasedOnDatabase() {
        return elasticsearchManager.reGenerateSearchData();
    }

    /**
     * 删除 Elasticsearch 中所有的数据
     *
     * @return 是否删除成功
     */
    @DeleteMapping("/build")
    public boolean deleteSearch() {
        return elasticsearchManager.checkAndDeleteIndex();
    }

    @GetMapping("/createIndex")
    public boolean hasIndexOrCreate() {
        // 初始化操作，生成用于搜索的 index（网络问题会抛出自定义的网络异常）
        return elasticsearchManager.hasIndexOrCreate();
    }
}
