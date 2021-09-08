package com.github.learndifferent.mtm.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.SearchResultsDTO;
import com.github.learndifferent.mtm.dto.WebForSearchDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.PageUtil;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 操作 Elasticsearch
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Component
public class ElasticsearchManager implements ApplicationContextAware {

    private final RestHighLevelClient client;
    private final WebsiteMapper websiteMapper;
    private final TrendsManager trendsManager;

    private ApplicationContext applicationContext;

    @Autowired
    public ElasticsearchManager(@Qualifier("restHighLevelClient") RestHighLevelClient client,
                                WebsiteMapper websiteMapper,
                                TrendsManager trendsManager) {
        this.client = client;
        this.websiteMapper = websiteMapper;
        this.trendsManager = trendsManager;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 是否存在该 Index，如果没有就创建 Index
     *
     * @return 是否存在 Index，没有该 Index 的话返回是否创建成功
     */
    public boolean hasIndexOrCreate() {

        return existsIndex() || createIndex();
    }

    private boolean createIndex() {
        CreateIndexRequest request = new CreateIndexRequest(EsConstant.INDEX);
        try {
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    /**
     * 是否存在该 Index
     *
     * @return true 表示存在，false 表示不存在
     */
    public boolean existsIndex() {
        try {
            GetIndexRequest request = new GetIndexRequest(EsConstant.INDEX);
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    /**
     * 存放文档
     *
     * @param web 需要存放的数据
     */
    @Async("asyncTaskExecutor")
    public void saveDoc(WebForSearchDTO web) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(web);
            IndexRequest request = new IndexRequest(EsConstant.INDEX);
            // 用网址作为 ID
            request.id(web.getUrl());
            request.timeout("10s");
            request.source(data, XContentType.JSON);
        } catch (IOException e) {
            // 如果无法存放，就放弃存放
            log.info("网络故障，无法将数据存放到 Elasticsearch 中……");
            e.printStackTrace();
        }
    }

    /**
     * 删除步骤：先检查该 index 是否存在，
     * <p>如果不存在，返回 true 表示已经删除；</p>
     * <p>如果不存在该 index，就执行删除</p>
     *
     * @return 是否删除成功
     */
    public boolean checkAndDeleteIndex() {

        return !existsIndex() || deleteIndex();
    }

    private boolean deleteIndex() {
        DeleteIndexRequest request = new DeleteIndexRequest(EsConstant.INDEX);
        try {
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    /**
     * 确保之前的数据已经清空，再根据数据库中的数据生成 Elasticsearch 的数据
     *
     * @return 是否成功
     */
    public boolean reGenerateSearchData() {

        boolean notClear = !checkAndDeleteIndex();
        if (notClear) {
            // 如果无法清空之前的数据，抛出未知异常
            throw new ServiceException(ResultCode.ERROR);
        }

        // 清空之前的数据后，开始进行批量生成数据的操作
        return bulkAdd(getAllWebForSearch());
    }

    /**
     * 获取所有网页数据，包装为 Elasticsearch 需要的数据结构
     *
     * @return 获取到的网页数据
     */
    private List<WebForSearchDTO> getAllWebForSearch() {
        return websiteMapper.getAllWebForSearch();
    }

    private boolean bulkAdd(List<WebForSearchDTO> webs) {

        BulkRequest bulkRequest = new BulkRequest();

        for (WebForSearchDTO web : webs) {
            String json = JsonUtils.toJson(web);
            IndexRequest indexRequest = new IndexRequest(EsConstant.INDEX);
            indexRequest.id(web.getUrl());
            indexRequest.source(json, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        try {
            BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            // 没问题返回 true，出现问题返回 false
            return !response.hasFailures();
        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    /**
     * 分解搜索的关键词，并加入到热搜列表中
     *
     * @param keyword 没有进行分词处理和语言识别的搜索词
     */
    @Async("asyncTaskExecutor")
    public void analyzeKeywordAndPutToTrendingList(String keyword) {

        if (StringUtils.isEmpty(keyword)) {
            return;
        }

        // 检测 keyword 的语言并选择合适的分词器
        String analyzer = detectLanguageAndGetAnalyzer(keyword);

        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer(EsConstant.INDEX, analyzer, keyword);
        try {
            AnalyzeResponse analyze = client.indices().analyze(request, RequestOptions.DEFAULT);
            List<AnalyzeResponse.AnalyzeToken> tokens = analyze.getTokens();
            for (AnalyzeResponse.AnalyzeToken token : tokens) {
                String val = token.getTerm();
                if (val.length() > 1) {
                    // 统计字节数大于 1 的关键词，出现一次就加 1 个 score
                    trendsManager.addToTrendingList(val);
                }
            }
        } catch (IOException e) {
            log.info("出错就算了，这个热搜数据不重要");
        }
    }

    /**
     * 识别是哪国的语言，然后返回需要的 ES 分词器（目前支持英语、中文和日语）
     *
     * @param keyword 被检测的关键词
     * @return 需要的分词器
     */
    @NotNull
    private String detectLanguageAndGetAnalyzer(String keyword) {

        LanguageDetector detector = LanguageDetectorBuilder.fromLanguages(Language.JAPANESE, Language.CHINESE).build();
        Language lan = detector.detectLanguageOf(keyword);

        // 默认使用英文分词器
        String analyzer = "english";

        if (Language.JAPANESE.equals(lan)) {
            // 如果是日语，使用日语的分词器
            analyzer = EsConstant.ANALYZER_JAPANESE;
        }

        if (Language.CHINESE.equals(lan)) {
            // 如果是中文，使用中文的分词器
            analyzer = EsConstant.ANALYZER_CHINESE;
        }

        return analyzer;
    }

    /**
     * 根据关键词搜索（还要统计关键词的次数来做热搜）
     *
     * @param keyword  关键词
     * @param pageInfo 分页信息
     * @return 结果（搜索结果，总页数，错误信息等）
     * @throws ServiceException 关键词为空的情况， @EmptyStringCheck 注解会抛出无匹配结果异常。
     *                          如果搜索结果为 0，也会抛出无结果异常。
     *                          如果出现网络异常，也会抛出异常。
     */
    @EmptyStringCheck
    public SearchResultsDTO getSearchResult(
            @EmptyStringCheck.ExceptionIfEmpty(resultCode = ResultCode.NO_RESULTS_FOUND) String keyword,
            PageInfoDTO pageInfo) {


        // 因为要调用内部类的代理方法，所以先获取代理类
        ElasticsearchManager elasticsearchManager = applicationContext.getBean(ElasticsearchManager.class);
        // 将搜索词分词后放入热搜统计
        elasticsearchManager.analyzeKeywordAndPutToTrendingList(keyword);

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword, EsConstant.DESC, EsConstant.TITLE))
                .timeout(new TimeValue(1, TimeUnit.MINUTES))
                .highlighter(new HighlightBuilder()
                        .field(EsConstant.DESC)
                        .field(EsConstant.TITLE)
                        .preTags(EsConstant.PRE_TAGS)
                        .postTags(EsConstant.POST_TAGS)
                        .numOfFragments(0)
                ).from(from).size(size);

        SearchRequest request = new SearchRequest(EsConstant.INDEX).source(sourceBuilder);

        SearchHits hits;

        try {
            hits = client.search(request, RequestOptions.DEFAULT).getHits();
        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }

        // 查询到的总数
        long totalCount = hits.getTotalHits().value;
        if (totalCount == 0) {
            // 没有结果的时候，抛出无结果异常
            throw new ServiceException(ResultCode.NO_RESULTS_FOUND);
        }

        // 总页数
        int totalPage = PageUtil.getAllPages((int) totalCount, size);

        // 获取需要的网页数据
        List<WebForSearchDTO> webs = getWebForSearchByHits(hits);

        return SearchResultsDTO.builder()
                .totalCount(totalCount)
                .totalPage(totalPage)
                .webs(webs)
                .build();
    }

    /**
     * 根据 SearchHits 获取网页数据
     *
     * @param hits hits
     * @return 需要的网页数据
     */
    @NotNull
    private List<WebForSearchDTO> getWebForSearchByHits(SearchHits hits) {

        List<WebForSearchDTO> webs = new ArrayList<>();
        for (SearchHit hit : hits) {
            WebForSearchDTO web = hitHighlightAndGetWeb(hit, EsConstant.DESC, EsConstant.TITLE);
            webs.add(web);
        }

        return webs;
    }

    /**
     * 实现高亮并返回实体类
     *
     * @param hit    搜索结果
     * @param field1 字段1
     * @param field2 字段2
     * @return 高亮后的实体类
     */
    private WebForSearchDTO hitHighlightAndGetWeb(SearchHit hit, String field1, String field2) {

        Map<String, Object> source = hit.getSourceAsMap();

        HighlightField highlighted1 = hit.getHighlightFields().get(field1);
        HighlightField highlighted2 = hit.getHighlightFields().get(field2);

        if (highlighted1 != null) {
            Text[] texts = highlighted1.fragments();

            StringBuilder sb = new StringBuilder();
            for (Text text : texts) {
                sb.append(text);
            }

            source.put(field1, sb.toString());
        }

        if (highlighted2 != null) {
            Text[] texts = highlighted2.fragments();

            StringBuilder sb = new StringBuilder();
            for (Text text : texts) {
                sb.append(text);
            }

            source.put(field2, sb.toString());
        }

        return convertToWeb(source);
    }

    /**
     * 转化为实体类
     *
     * @param source 待转化
     * @return 实体类
     */
    private WebForSearchDTO convertToWeb(Map<String, Object> source) {

        String title = (String) source.get(EsConstant.TITLE);
        String url = (String) source.get(EsConstant.URL);
        String img = (String) source.get(EsConstant.IMG);
        String desc = (String) source.get(EsConstant.DESC);

        return WebForSearchDTO.builder().title(title).url(url).img(img).desc(desc).build();
    }

}
