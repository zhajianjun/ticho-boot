package top.ticho.starter.es.component;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.StatusToXContentObject;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import top.ticho.starter.es.query.EsQuery;
import top.ticho.starter.view.core.TiEntity;
import top.ticho.starter.view.core.TiEsPageResult;
import top.ticho.starter.view.exception.TiBizException;
import top.ticho.tool.core.TiCollUtil;
import top.ticho.tool.core.TiIdUtil;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.json.util.TiJsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * es操作
 *
 * @author zhajianjun
 * @date 2023-04-15 12:26
 */
@Slf4j
public class EsTemplateImpl implements EsTemplate {
    /** The constant MAX_BATCH_SIZE. */
    private static final int MAX_BATCH_SIZE = 100;
    /** The Rest client. */
    private final RestHighLevelClient restHighLevelClient;

    public EsTemplateImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }


    public void save(String index, TiEntity tiEntity) {
        if (tiEntity == null) {
            log.info("es保存数据异常，对象为null");
            return;
        }
        IndexRequest indexRequest = getIndexRequest(index, tiEntity);
        this.save(indexRequest);
    }

    public void save(String index, Map<String, Object> entity) {
        if (entity == null) {
            log.info("es保存数据异常，对象为null");
            return;
        }
        IndexRequest indexRequest = getIndexRequest(index, entity);
        this.save(indexRequest);
    }

    public void saveBatch(String index, List<? extends TiEntity> entities) {
        this.saveBatch(index, entities, MAX_BATCH_SIZE);
    }

    public void saveBatch(String index, List<? extends TiEntity> entityList, Integer bachSize) {
        if (TiCollUtil.isEmpty(entityList)) {
            log.info("es批量保存数据异常，集合为null或者大小为0");
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entityList.size() <= MAX_BATCH_SIZE) {
            saveBatchDb(index, entityList);
            return;
        }
        List<? extends List<? extends TiEntity>> split = TiCollUtil.split(entityList, bachSize);
        split.forEach(item -> saveBatchDb(index, item));
    }

    private void saveBatchDb(String index, List<? extends TiEntity> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (TiEntity tiEntity : entities) {
                IndexRequest updateRequest = getIndexRequest(index, tiEntity);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            throw new TiBizException("es批量保存数据失败", e);
        }
    }

    public void saveBatchForMap(String index, List<Map<String, Object>> entities) {
        this.saveBatchForMap(index, entities, MAX_BATCH_SIZE);
    }

    public void saveBatchForMap(String index, List<Map<String, Object>> entityList, Integer bachSize) {
        if (TiCollUtil.isEmpty(entityList)) {
            log.info("es批量保存数据异常，集合为null或者大小为0");
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entityList.size() <= MAX_BATCH_SIZE) {
            saveBatchForMapDb(index, entityList);
            return;
        }
        List<? extends List<Map<String, Object>>> split = TiCollUtil.split(entityList, bachSize);
        split.forEach(item -> saveBatchForMapDb(index, item));
    }

    private void saveBatchForMapDb(String index, List<Map<String, Object>> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Map<String, Object> entity : entities) {
                IndexRequest updateRequest = getIndexRequest(index, entity);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            throw new TiBizException("es批量保存数据失败", e);
        }
    }

    public void saveOrUpdate(String index, TiEntity tiEntity) {
        if (tiEntity == null) {
            log.info("es保存或更新数据异常，对象为null");
            return;
        }
        UpdateRequest updateRequest = getUpdateRequest(index, tiEntity, true);
        this.update(updateRequest);
    }

    /**
     * 保存或者更新es数据
     *
     * @param index  索引
     * @param entity 对象
     */
    @Override
    public void saveOrUpdate(String index, Map<String, Object> entity) {
        if (entity == null) {
            log.info("es保存或更新数据异常，对象为null");
            return;
        }
        UpdateRequest updateRequest = getUpdateRequest(index, entity, true);
        this.update(updateRequest);
    }

    @Override
    public void saveOrUpdateBatch(String index, List<? extends TiEntity> entities) {
        this.saveOrUpdateBatch(index, entities, MAX_BATCH_SIZE);
    }

    public void saveOrUpdateBatch(String index, List<? extends TiEntity> entityList, Integer bachSize) {
        if (TiCollUtil.isEmpty(entityList)) {
            log.info("es批量保存或者更新数据异常，集合为null或者大小为0");
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entityList.size() <= MAX_BATCH_SIZE) {
            saveOrUpdateBatchDb(index, entityList);
            return;
        }
        List<? extends List<? extends TiEntity>> split = TiCollUtil.split(entityList, bachSize);
        split.forEach(item -> saveOrUpdateBatchDb(index, item));
    }

    /**
     * 批量保存或者更新es数据
     *
     * @param index    索引
     * @param entities 对象列表
     */
    public void saveOrUpdateBatchDb(String index, List<? extends TiEntity> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (TiEntity tiEntity : entities) {
                UpdateRequest updateRequest = getUpdateRequest(index, tiEntity, true);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            throw new TiBizException("es批量保存或者更新数据失败", e);
        }
    }

    /**
     * 批量保存或者更新es数据
     *
     * @param index    索引
     * @param entities 对象列表
     */
    public void saveOrUpdateBatchForMap(String index, List<Map<String, Object>> entities) {
        saveOrUpdateBatchForMap(index, entities, MAX_BATCH_SIZE);
    }

    public void saveOrUpdateBatchForMap(String index, List<Map<String, Object>> entityList, Integer bachSize) {
        if (TiCollUtil.isEmpty(entityList)) {
            log.info("es批量保存或者更新数据异常，集合为null或者大小为0");
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entityList.size() <= MAX_BATCH_SIZE) {
            saveOrUpdateBatchForMapDb(index, entityList);
            return;
        }
        List<List<Map<String, Object>>> split = TiCollUtil.split(entityList, bachSize);
        split.forEach(item -> saveOrUpdateBatchForMapDb(index, item));
    }

    /**
     * Save or update batch for map.
     *
     * @param index    the index
     * @param entities the entities
     */
    public void saveOrUpdateBatchForMapDb(String index, List<Map<String, Object>> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Map<String, Object> entity : entities) {
                UpdateRequest updateRequest = getUpdateRequest(index, entity, true);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            throw new TiBizException("es批量保存或者更新数据失败", e);
        }
    }

    /**
     * 根据id删除es数据
     *
     * @param index 索引
     * @param id    编号
     */
    public void removeById(String index, String id) {
        if (TiStrUtil.isBlank(id)) {
            log.info("es删除数据异常，id为null");
            return;
        }
        try {
            DeleteRequest deleteRequest = Requests.deleteRequest(index).id(id);
            DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            printStatus(response);
        } catch (Exception e) {
            throw new TiBizException("es删除数据失败", e);
        }
    }

    /**
     * 批量根据id删除数据
     *
     * @param index 索引
     * @param ids   编号列表
     */
    public void removeByIds(String index, Collection<String> ids) {
        if (TiCollUtil.isEmpty(ids)) {
            log.info("es批量删除数据异常，id列表集合为null或者大小为0");
            return;
        }
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (String id : ids) {
                DeleteRequest deleteRequest = Requests.deleteRequest(index).id(id);
                bulkRequest.add(deleteRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            throw new TiBizException("es批量删除数据失败", e);
        }
    }

    /**
     * 根据id更改数据
     *
     * @param index    索引
     * @param tiEntity 对象
     */
    public void updateById(String index, TiEntity tiEntity) {
        if (Objects.isNull(tiEntity)) {
            log.info("es删除数据异常，对象为null");
            return;
        }
        UpdateRequest updateRequest = getUpdateRequest(index, tiEntity, false);
        update(updateRequest);
    }


    /**
     * 保存
     *
     * @param indexRequest indexRequest
     */
    private void save(IndexRequest indexRequest) {
        try {
            IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            printStatus(index);
        } catch (ElasticsearchStatusException e) {
            RestStatus status = e.status();
            log.error("{}, status is {}", e.getMessage(), status.name(), e);
        } catch (Exception e) {
            throw new TiBizException("es保存数据失败", e);
        }
    }

    /**
     * Update.
     *
     * @param updateRequest the update request
     */
    private void update(UpdateRequest updateRequest) {
        try {
            UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            printStatus(update);
        } catch (ElasticsearchStatusException e) {
            RestStatus status = e.status();
            if (status.compareTo(RestStatus.NOT_FOUND) == 0) {
                log.warn("es更新数据失败，数据不存在");
            } else {
                log.error("{}, status is {}", e.getMessage(), status.name(), e);
            }
        } catch (Exception e) {
            throw new TiBizException("es更新数据失败", e);
        }
    }

    /**
     * 根据id更改数据
     *
     * @param index  索引
     * @param entity 对象
     */
    public void updateById(String index, Map<String, Object> entity) {
        UpdateRequest updateRequest = getUpdateRequest(index, entity, false);
        update(updateRequest);
    }

    /**
     * 批量更改数据
     *
     * @param index    索引
     * @param entities 对象列表
     */
    public void updateBatch(String index, List<? extends TiEntity> entities) {
        updateBatch(index, entities, MAX_BATCH_SIZE);
    }

    public void updateBatch(String index, List<? extends TiEntity> entities, Integer bachSize) {
        if (entities == null) {
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entities.size() <= MAX_BATCH_SIZE) {
            updateBatchDb(index, entities);
            return;
        }
        List<? extends List<? extends TiEntity>> split = TiCollUtil.split(entities, bachSize);
        for (List<? extends TiEntity> maps : split) {
            updateBatchDb(index, maps);
        }
    }

    /**
     * Update batch.
     *
     * @param index    the index
     * @param entities the entities
     */
    public void updateBatchDb(String index, List<? extends TiEntity> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (TiEntity tiEntity : entities) {
                UpdateRequest updateRequest = getUpdateRequest(index, tiEntity, false);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            throw new TiBizException("es更新数据失败", e);
        }
    }

    /**
     * 根据id更改map数据
     *
     * @param index    索引
     * @param entities 对象列表
     */
    public void updateBatchForMap(String index, List<Map<String, Object>> entities) {
        updateBatchForMap(index, entities, MAX_BATCH_SIZE);
    }

    public void updateBatchForMap(String index, List<Map<String, Object>> entities, Integer bachSize) {
        if (entities == null) {
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entities.size() <= MAX_BATCH_SIZE) {
            updateBatchForMapDb(index, entities);
            return;
        }
        List<List<Map<String, Object>>> split = TiCollUtil.split(entities, bachSize);
        for (List<Map<String, Object>> maps : split) {
            updateBatchForMapDb(index, maps);
        }
    }

    /**
     * Update batch for map.
     *
     * @param index    the index
     * @param entities the entities
     */
    public void updateBatchForMapDb(String index, List<Map<String, Object>> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Map<String, Object> entity : entities) {
                UpdateRequest updateRequest = getUpdateRequest(index, entity, false);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            throw new TiBizException("es更新数据失败", e);
        }
    }

    public <T> T getById(String index, String id, Class<T> tClass) {
        EsQuery<T> esQuery = new EsQuery<>();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.matchQuery("id", id));
        esQuery.setQueryBuilder(query);
        esQuery.setClazz(tClass);
        esQuery.setIndexs(Collections.singletonList(index));
        TiEsPageResult<T> pageInfo = page(esQuery);
        if (pageInfo == null || TiCollUtil.isEmpty(pageInfo.getRows())) {
            return null;
        }
        return pageInfo.getRows().get(0);
    }

    public Map<String, Object> getById(String index, String id) {
        EsQuery<Map<String, Object>> esQuery = new EsQuery<>();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.matchQuery("id", id));
        esQuery.setQueryBuilder(query);
        esQuery.setIndexs(Collections.singletonList(index));
        TiEsPageResult<Map<String, Object>> pageInfo = pageForMap(esQuery);
        if (pageInfo == null || TiCollUtil.isEmpty(pageInfo.getRows())) {
            return null;
        }
        return pageInfo.getRows().get(0);
    }

    /**
     * 根据条件查询
     *
     * @param esQuery es查询条件
     * @return 查询数据
     */
    public <T> TiEsPageResult<T> page(EsQuery<T> esQuery) {
        SearchResponse searchResponse = searchResponse(esQuery);
        List<T> rows = new ArrayList<>();
        Set<String> indexs = new HashSet<>();
        AtomicInteger total = new AtomicInteger();
        Class<T> clazz = esQuery.getClazz();
        Optional.ofNullable(searchResponse).map(x -> {
            SearchHits hits = x.getHits();
            total.set(Long.valueOf(hits.getTotalHits().value).intValue());
            return hits;
        }).map(SearchHits::getHits).ifPresent(hits -> {
            for (SearchHit searchHit : hits) {
                indexs.add(searchHit.getIndex());
                rows.add(TiJsonUtil.toObject(searchHit.getSourceAsString(), clazz));
            }
        });
        return new TiEsPageResult<>(esQuery.getPageNum(), esQuery.getPageSize(), total, indexs, rows);
    }

    /**
     * 根据条件查询
     *
     * @param esQuery es查询条件
     * @return 查询数据
     */
    public TiEsPageResult<Map<String, Object>> pageForMap(EsQuery<Map<String, Object>> esQuery) {
        SearchResponse searchResponse = searchResponse(esQuery);
        List<Map<String, Object>> rows = new ArrayList<>();
        Set<String> indexs = new HashSet<>();
        AtomicInteger total = new AtomicInteger();
        Optional.ofNullable(searchResponse).map(x -> {
            SearchHits hits = x.getHits();
            total.set(Long.valueOf(hits.getTotalHits().value).intValue());
            return hits;
        }).map(SearchHits::getHits).ifPresent(hits -> {
            for (SearchHit searchHit : hits) {
                indexs.add(searchHit.getIndex());
                rows.add(searchHit.getSourceAsMap());
            }
        });
        return new TiEsPageResult<>(esQuery.getPageNum(), esQuery.getPageSize(), total, indexs, rows);
    }

    /**
     * 搜索响应
     *
     * @param esQuery es查询
     * @return {@link SearchResponse}
     */
    public SearchResponse searchResponse(EsQuery<?> esQuery) {
        // 执行搜索,返回搜索响应信息
        SearchResponse searchResponse;
        try {
            SearchRequest searchRequest = new SearchRequest(esQuery.getIndexs().toArray(new String[0]));
            SearchSourceBuilder searchBuilder = searchRequest.source();
            searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
            // 查询超时时间
            searchBuilder.timeout(new TimeValue(30000));
            // 需要显示的字段，逗号分隔（缺省为全部字段）
            if (TiCollUtil.isNotEmpty(esQuery.getFields())) {
                searchBuilder.fetchSource(esQuery.getFields().toArray(new String[0]), null);
            }
            // 排序字段
            setSortField(esQuery, searchBuilder);
            // 设置高亮字段
            if (TiStrUtil.isNotBlank(esQuery.getHighlightField())) {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.field(esQuery.getHighlightField());
                searchBuilder.highlighter(highlightBuilder);
            }
            // 添加查询参数
            QueryBuilder queryBuilder = esQuery.getQueryBuilder();
            // 如果不存在则查询所有
            if (queryBuilder == null) {
                queryBuilder = QueryBuilders.matchAllQuery();
            }
            searchBuilder.query(queryBuilder);
            // 分页
            if (Objects.isNull(esQuery.getFrom())) {
                esQuery.setFrom(0);
            }
            if (Objects.isNull(esQuery.getSize())) {
                esQuery.setSize(10000);
            }
            searchBuilder.from(esQuery.getFrom()).size(esQuery.getSize());
            List<String> searchAfter = esQuery.getSearchAfter();
            if (TiCollUtil.isNotEmpty(searchAfter)) {
                // searchAfter 必须从0开始
                esQuery.setFrom(0);
                searchBuilder.searchAfter(searchAfter.toArray());
            }
            // from + size must be less than or equal to: [10000]
            int largeDataSize = 10000;
            if (esQuery.getFrom() + esQuery.getSize() > largeDataSize) {
                searchRequest.scroll(new Scroll(new TimeValue(60000)));
            }
            // 设置是否按查询匹配度排序
            searchBuilder.explain(true);
            // 打印的内容
            if (log.isDebugEnabled()) {
                log.debug("【SearchRequest==>{}】", searchRequest);
            }
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new TiBizException("select error", e);
        }
        if (Objects.isNull(searchResponse) || searchResponse.status().getStatus() != RestStatus.OK.getStatus()) {
            throw new TiBizException("select error");
        }
        return searchResponse;
    }


    /**
     * 设置排序字段
     *
     * @param esQuery       es查询
     * @param searchBuilder 搜索生成器
     */
    private void setSortField(EsQuery<?> esQuery, SearchSourceBuilder searchBuilder) {
        List<String> sortFields = esQuery.getSortFields();
        if (TiCollUtil.isEmpty(sortFields)) {
            return;
        }
        List<String> sortOrder = esQuery.getSortOrders();
        SortOrder asc = SortOrder.ASC;
        if (TiCollUtil.isEmpty(sortOrder)) {
            sortFields.forEach(x -> searchBuilder.sort(x, asc));
            return;
        }
        int sortOrderLength = sortOrder.size();
        int sortFieldLength = sortFields.size();
        for (int i = 0; i < sortFieldLength; i++) {
            String sortField = sortFields.get(i);
            if (i > sortOrderLength) {
                searchBuilder.sort(sortField, asc);
                continue;
            }
            boolean isAsc = asc.name().equalsIgnoreCase(sortOrder.get(i));
            searchBuilder.sort(sortField, isAsc ? asc : SortOrder.DESC);
        }
    }

    /**
     * 得到更新请求
     *
     * @param index       索引
     * @param tiEntity    实体
     * @param docAsUpsert true-文档不存在则插入，有则更新；false-文档不存在,会抛出ElasticsearchException
     * @return {@link UpdateRequest}
     */
    private UpdateRequest getUpdateRequest(String index, TiEntity tiEntity, boolean docAsUpsert) {
        String id = tiEntity.getId();
        UpdateRequest updateRequest = new UpdateRequest(index, id);
        IndexRequest indexRequest = getIndexRequest(index, tiEntity);
        updateRequest.doc(indexRequest);
        updateRequest.docAsUpsert(docAsUpsert);
        return updateRequest;
    }

    private IndexRequest getIndexRequest(String index, TiEntity tiEntity) {
        String id = tiEntity.getId();
        IndexRequest indexRequest = new IndexRequest(index).id(id);
        indexRequest.source(TiJsonUtil.toJsonString(tiEntity), XContentType.JSON);
        return indexRequest;
    }


    /**
     * 得到更新请求
     *
     * @param index       索引
     * @param entity      实体
     * @param docAsUpsert true-文档不存在则插入，有则更新；false-文档不存在,会抛出ElasticsearchException
     * @return {@link UpdateRequest}
     */
    private UpdateRequest getUpdateRequest(String index, Map<String, Object> entity, boolean docAsUpsert) {
        String id = Optional.ofNullable(entity.get("id")).map(Object::toString).orElseGet(TiIdUtil::ulid);
        UpdateRequest updateRequest = new UpdateRequest(index, id);
        IndexRequest indexRequest = getIndexRequest(index, entity);
        updateRequest.doc(indexRequest);
        updateRequest.docAsUpsert(docAsUpsert);
        return updateRequest;
    }

    private IndexRequest getIndexRequest(String index, Map<String, Object> entity) {
        String id = Optional.ofNullable(entity.get("id")).map(Object::toString).orElseGet(TiIdUtil::ulid);
        IndexRequest indexRequest = new IndexRequest(index).id(id);
        indexRequest.source(entity);
        return indexRequest;
    }

    /**
     * 打印状态
     *
     * @param toContentObject 内容对象
     */
    public void printStatus(StatusToXContentObject toContentObject) {
        if (log.isDebugEnabled()) {
            RestStatus restStatus = toContentObject.status();
            log.debug("es status is {}", restStatus.name());
        }
    }

    /**
     * 得到错误消息
     *
     * @param response 响应
     * @return {@link String}
     */
    public String getErrorMessage(BulkResponse response) {
        int cnt = 0;
        StringJoiner sb = new StringJoiner("\n");
        for (BulkItemResponse item : response.getItems()) {
            if (item.getFailure() != null) {
                cnt++;
                sb.add(item.getFailure().getMessage());
                if (cnt >= 3) {
                    break;
                }
            }
        }
        return sb.toString();
    }

    /**
     * 打印错误信息
     *
     * @param response 响应
     */
    public void printErrorMessage(BulkResponse response) {
        String errorMessage = getErrorMessage(response);
        if (TiStrUtil.isEmpty(errorMessage)) {
            return;
        }
        log.error(errorMessage);
    }

}

