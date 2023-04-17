package com.ticho.boot.es.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.es.page.PageInfo;
import com.ticho.boot.es.query.EsQuery;
import com.ticho.boot.json.util.JsonUtil;
import com.ticho.boot.view.core.Entity;
import com.ticho.boot.view.exception.BizException;
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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * es操作
 *
 * @author zhajianjun
 * @date 2023-04-15 12:26:56
 */
@Slf4j
public class EsTemplateImpl implements EsTemplate {
    /** The constant MAX_BATCH_SIZE. */
    private static final int MAX_BATCH_SIZE = 50;
    /** The Rest client. */
    private final RestHighLevelClient restHighLevelClient;

    public EsTemplateImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }


    public void save(String type, String index, Entity entity) {
        if (entity == null) {
            log.info("es保存数据异常，对象为null");
            return;
        }
        IndexRequest indexRequest = getIndexRequest(type, index, entity);
        this.save(indexRequest);
    }

    public void save(String type, String index, Map<String, Object> entity) {
        if (entity == null) {
            log.info("es保存数据异常，对象为null");
            return;
        }
        IndexRequest indexRequest = getIndexRequest(type, index, entity);
        this.save(indexRequest);
    }

    public void saveBatch(String type, String index, List<? extends Entity> entities) {
        this.saveBatch(type, index, entities, MAX_BATCH_SIZE);
    }

    public void saveBatch(String type, String index, List<? extends Entity> entityList, Integer bachSize) {
        if (CollUtil.isEmpty(entityList)) {
            log.info("es批量保存数据异常，集合为null或者大小为0");
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entityList.size() <= MAX_BATCH_SIZE) {
            saveBatchDb(type, index, entityList);
            return;
        }
        List<? extends List<? extends Entity>> split = CollUtil.split(entityList, bachSize);
        split.forEach(item -> saveBatchDb(type, index, item));
    }

    private void saveBatchDb(String type, String index, List<? extends Entity> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Entity entity : entities) {
                IndexRequest updateRequest = getIndexRequest(type, index, entity);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("es批量保存数据失败");
        }
    }

    public void saveBatchForMap(String type, String index, List<Map<String, Object>> entities) {
        this.saveBatchForMap(type, index, entities, MAX_BATCH_SIZE);
    }

    public void saveBatchForMap(String type, String index, List<Map<String, Object>> entityList, Integer bachSize) {
        if (CollUtil.isEmpty(entityList)) {
            log.info("es批量保存数据异常，集合为null或者大小为0");
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entityList.size() <= MAX_BATCH_SIZE) {
            saveBatchForMapDb(type, index, entityList);
            return;
        }
        List<? extends List<Map<String, Object>>> split = CollUtil.split(entityList, bachSize);
        split.forEach(item -> saveBatchForMapDb(type, index, item));
    }

    private void saveBatchForMapDb(String type, String index, List<Map<String, Object>> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Map<String, Object> entity : entities) {
                IndexRequest updateRequest = getIndexRequest(type, index, entity);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("es批量保存数据失败");
        }
    }

    public void saveOrUpdate(String type, String index, Entity entity) {
        if (entity == null) {
            log.info("es保存或更新数据异常，对象为null");
            return;
        }
        UpdateRequest updateRequest = getUpdateRequest(type, index, entity, true);
        this.update(updateRequest);
    }

    /**
     * 保存或者更新es数据
     *
     * @param type 文档
     * @param index 索引
     * @param entity 对象
     */
    @Override
    public void saveOrUpdate(String type, String index, Map<String, Object> entity) {
        if (entity == null) {
            log.info("es保存或更新数据异常，对象为null");
            return;
        }
        UpdateRequest updateRequest = getUpdateRequest(type, index, entity, true);
        this.update(updateRequest);
    }

    @Override
    public void saveOrUpdateBatch(String type, String index, List<? extends Entity> entities) {
        this.saveOrUpdateBatch(type, index, entities, MAX_BATCH_SIZE);
    }

    public void saveOrUpdateBatch(String type, String index, List<? extends Entity> entityList, Integer bachSize) {
        if (CollUtil.isEmpty(entityList)) {
            log.info("es批量保存或者更新数据异常，集合为null或者大小为0");
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entityList.size() <= MAX_BATCH_SIZE) {
            saveOrUpdateBatchDb(type, index, entityList);
            return;
        }
        List<? extends List<? extends Entity>> split = CollUtil.split(entityList, bachSize);
        split.forEach(item -> saveOrUpdateBatchDb(type, index, item));
    }

    /**
     * 批量保存或者更新es数据
     *
     * @param type 文档
     * @param index 索引
     * @param entities 对象列表
     */
    public void saveOrUpdateBatchDb(String type, String index, List<? extends Entity> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Entity entity : entities) {
                UpdateRequest updateRequest = getUpdateRequest(type, index, entity, true);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("es批量保存或者更新数据失败");
        }
    }

    /**
     * 批量保存或者更新es数据
     *
     * @param type 文档
     * @param index 索引
     * @param entities 对象列表
     */
    public void saveOrUpdateBatchForMap(String type, String index, List<Map<String, Object>> entities) {
        saveOrUpdateBatchForMap(type, index, entities, MAX_BATCH_SIZE);
    }

    public void saveOrUpdateBatchForMap(String type, String index, List<Map<String, Object>> entityList,
            Integer bachSize) {
        if (CollUtil.isEmpty(entityList)) {
            log.info("es批量保存或者更新数据异常，集合为null或者大小为0");
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entityList.size() <= MAX_BATCH_SIZE) {
            saveOrUpdateBatchForMapDb(type, index, entityList);
            return;
        }
        List<List<Map<String, Object>>> split = CollUtil.split(entityList, bachSize);
        split.forEach(item -> saveOrUpdateBatchForMapDb(type, index, item));
    }

    /**
     * Save or update batch for map.
     *
     * @param type the type
     * @param index the index
     * @param entities the entities
     */
    public void saveOrUpdateBatchForMapDb(String type, String index, List<Map<String, Object>> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Map<String, Object> entity : entities) {
                UpdateRequest updateRequest = getUpdateRequest(type, index, entity, true);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("es批量保存或者更新数据失败");
        }
    }

    /**
     * 根据id删除es数据
     *
     * @param type 文档
     * @param index 索引
     * @param id 编号
     */
    public void removeById(String type, String index, String id) {
        if (StrUtil.isBlank(id)) {
            log.info("es删除数据异常，id为null");
            return;
        }
        try {
            DeleteRequest deleteRequest = Requests.deleteRequest(index).type(type).id(id);
            DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            printStatus(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("es删除数据失败");
        }
    }

    /**
     * 批量根据id删除数据
     *
     * @param type 文档
     * @param index 索引
     * @param ids 编号列表
     */
    public void removeByIds(String type, String index, Collection<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            log.info("es批量删除数据异常，id列表集合为null或者大小为0");
            return;
        }
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (String id : ids) {
                DeleteRequest deleteRequest = Requests.deleteRequest(index).type(type).id(id);
                bulkRequest.add(deleteRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("es批量删除数据失败");
        }
    }

    /**
     * 根据id更改数据
     *
     * @param type 文档
     * @param index 索引
     * @param entity 对象
     */
    public void updateById(String type, String index, Entity entity) {
        if (Objects.isNull(entity)) {
            log.info("es删除数据异常，对象为null");
            return;
        }
        UpdateRequest updateRequest = getUpdateRequest(type, index, entity, false);
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
            ;
            printStatus(index);
        } catch (ElasticsearchStatusException e) {
            RestStatus status = e.status();
            log.error("{}, status is {}", e.getMessage(), status.name(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("es保存数据失败");
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
            log.error(e.getMessage(), e);
            throw new BizException("es更新数据失败");
        }
    }

    /**
     * 根据id更改数据
     *
     * @param type 文档
     * @param index 索引
     * @param entity 对象
     */
    public void updateById(String type, String index, Map<String, Object> entity) {
        UpdateRequest updateRequest = getUpdateRequest(type, index, entity, false);
        update(updateRequest);
    }

    /**
     * 批量更改数据
     *
     * @param type 文档
     * @param index 索引
     * @param entities 对象列表
     */
    public void updateBatch(String type, String index, List<? extends Entity> entities) {
        updateBatch(type, index, entities, MAX_BATCH_SIZE);
    }

    public void updateBatch(String type, String index, List<? extends Entity> entities, Integer bachSize) {
        if (entities == null) {
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entities.size() <= MAX_BATCH_SIZE) {
            updateBatchDb(type, index, entities);
            return;
        }
        List<? extends List<? extends Entity>> split = CollUtil.split(entities, bachSize);
        for (List<? extends Entity> maps : split) {
            updateBatchDb(type, index, maps);
        }
    }

    /**
     * Update batch.
     *
     * @param type the type
     * @param index the index
     * @param entities the entities
     */
    public void updateBatchDb(String type, String index, List<? extends Entity> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Entity entity : entities) {
                UpdateRequest updateRequest = getUpdateRequest(type, index, entity, false);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("es更新数据失败");
        }
    }

    /**
     * 根据id更改map数据
     *
     * @param type 文档
     * @param index 索引
     * @param entities 对象列表
     */
    public void updateBatchForMap(String type, String index, List<Map<String, Object>> entities) {
        updateBatchForMap(type, index, entities, MAX_BATCH_SIZE);
    }

    public void updateBatchForMap(String type, String index, List<Map<String, Object>> entities, Integer bachSize) {
        if (entities == null) {
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entities.size() <= MAX_BATCH_SIZE) {
            updateBatchForMapDb(type, index, entities);
            return;
        }
        List<List<Map<String, Object>>> split = CollUtil.split(entities, bachSize);
        for (List<Map<String, Object>> maps : split) {
            updateBatchForMapDb(type, index, maps);
        }
    }

    /**
     * Update batch for map.
     *
     * @param type the type
     * @param index the index
     * @param entities the entities
     */
    public void updateBatchForMapDb(String type, String index, List<Map<String, Object>> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Map<String, Object> entity : entities) {
                UpdateRequest updateRequest = getUpdateRequest(type, index, entity, false);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("es更新数据失败");
        }
    }

    /**
     * 根据id查询数据
     *
     * @param type 文档
     * @param index 索引
     * @param id 编号
     * @param tClass 返回的对象类
     * @return T
     */
    public <T> T getById(String type, String index, String id, Class<T> tClass) {
        EsQuery<T> esQuery = new EsQuery<>();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.matchQuery("id", id));
        esQuery.setQueryBuilder(query);
        esQuery.setClazz(tClass);
        esQuery.setTypes(type);
        esQuery.setIndex(index);
        PageInfo<T> pageInfo = getPageInfo(esQuery);
        if (pageInfo == null || CollUtil.isEmpty(pageInfo.getData())) {
            return null;
        }
        return pageInfo.getData().get(0);
    }

    /**
     * 根据条件查询
     *
     * @param esQuery es查询条件
     * @return 查询数据
     */
    @SuppressWarnings("unchecked")
    public <T> PageInfo<T> getPageInfo(EsQuery<T> esQuery) {
        SearchResponse searchResponse = searchResponse(esQuery);
        PageInfo<T> pageInfo = new PageInfo<>();
        List<T> data = new ArrayList<>();
        Set<String> indexs = new HashSet<>();
        Set<String> types = new HashSet<>();
        pageInfo.setTypes(types);
        pageInfo.setData(data);
        pageInfo.setIndexs(indexs);
        pageInfo.setPageNum(esQuery.getPageNum());
        pageInfo.setPageSize(esQuery.getPageSize());

        Class<T> clazz = esQuery.getClazz();
        Optional.ofNullable(searchResponse).map(x -> {
            SearchHits hits = x.getHits();
            pageInfo.setTotal(hits.getTotalHits().value);
            return hits;
        }).map(SearchHits::getHits).ifPresent(hits -> {
            for (SearchHit searchHit : hits) {
                indexs.add(searchHit.getIndex());
                types.add(searchHit.getType());
                if (clazz.equals(Map.class)) {
                    data.add((T) searchHit.getSourceAsMap());
                } else {
                    data.add(JsonUtil.toJavaObject(searchHit.getSourceAsString(), clazz));
                }
            }
        });
        return pageInfo;
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
            SearchRequest searchRequest = new SearchRequest(esQuery.getIndex());
            SearchSourceBuilder searchBuilder = searchRequest.source();
            if (CollUtil.isNotEmpty(esQuery.getTypes())) {
                searchRequest.types(esQuery.getTypes().toArray(new String[0]));
            }
            searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
            // 查询超时时间
            searchBuilder.timeout(new TimeValue(30000));
            // 需要显示的字段，逗号分隔（缺省为全部字段）
            if (CollUtil.isNotEmpty(esQuery.getFields())) {
                searchBuilder.fetchSource(esQuery.getFields().toArray(new String[0]), null);
            }
            // 排序字段
            setSortField(esQuery, searchBuilder);
            // 设置高亮字段
            if (StrUtil.isNotBlank(esQuery.getHighlightField())) {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.field(esQuery.getHighlightField());
                searchBuilder.highlighter(highlightBuilder);
            }
            // 添加查询参数
            searchBuilder.query(esQuery.getQueryBuilder());
            // 分页
            if (Objects.isNull(esQuery.getFrom())) {
                esQuery.setFrom(0);
            }
            if (Objects.isNull(esQuery.getSize())) {
                esQuery.setSize(10000);
            }
            searchBuilder.from(esQuery.getFrom()).size(esQuery.getSize());
            List<String> searchAfter = esQuery.getSearchAfter();
            if (CollUtil.isNotEmpty(searchAfter)) {
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
            //打印的内容
            if (log.isDebugEnabled()) {
                log.debug("【SearchRequest==>{}】", searchRequest);
            }
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("catch error:\n", e);
            throw new BizException("select error");
        }
        if (Objects.isNull(searchResponse) || searchResponse.status().getStatus() != RestStatus.OK.getStatus()) {
            throw new BizException("select error");
        }
        return searchResponse;
    }


    /**
     * 设置排序字段
     *
     * @param esQuery es查询
     * @param searchBuilder 搜索生成器
     */
    private void setSortField(EsQuery<?> esQuery, SearchSourceBuilder searchBuilder) {
        List<String> sortFields = esQuery.getSortFields();
        if (CollUtil.isEmpty(sortFields)) {
            return;
        }
        List<String> sortOrder = esQuery.getSortOrders();
        SortOrder asc = SortOrder.ASC;
        if (CollUtil.isEmpty(sortOrder)) {
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
     * @param type 类型
     * @param index 指数
     * @param entity 实体
     * @param docAsUpsert true-文档不存在则插入，有则更新；false-文档不存在,会抛出ElasticsearchException
     * @return {@link UpdateRequest}
     */
    private UpdateRequest getUpdateRequest(String type, String index, Entity entity, boolean docAsUpsert) {
        String id = entity.getId();
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        IndexRequest indexRequest = getIndexRequest(type, index, entity);
        updateRequest.doc(indexRequest);
        updateRequest.docAsUpsert(docAsUpsert);
        return updateRequest;
    }

    private IndexRequest getIndexRequest(String type, String index, Entity entity) {
        String id = entity.getId();
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(JsonUtil.toJsonString(entity), XContentType.JSON);
        return indexRequest;
    }


    /**
     * 得到更新请求
     *
     * @param type 类型
     * @param index 指数
     * @param entity 实体
     * @param docAsUpsert true-文档不存在则插入，有则更新；false-文档不存在,会抛出ElasticsearchException
     * @return {@link UpdateRequest}
     */
    private UpdateRequest getUpdateRequest(String type, String index, Map<String, Object> entity, boolean docAsUpsert) {
        String id = Optional.ofNullable(entity.get("id")).map(Object::toString).orElseGet(IdUtil::getSnowflakeNextIdStr);
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        IndexRequest indexRequest = getIndexRequest(type, index, entity);
        updateRequest.doc(indexRequest);
        updateRequest.docAsUpsert(docAsUpsert);
        return updateRequest;
    }

    private IndexRequest getIndexRequest(String type, String index, Map<String, Object> entity) {
        String id = Optional.ofNullable(entity.get("id")).map(Object::toString).orElseGet(IdUtil::getSnowflakeNextIdStr);
        IndexRequest indexRequest = new IndexRequest(index, type, id);
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
        if (StrUtil.isEmpty(errorMessage)) {
            return;
        }
        log.error(errorMessage);
    }

}

