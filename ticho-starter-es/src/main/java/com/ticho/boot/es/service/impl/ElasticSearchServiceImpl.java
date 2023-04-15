package com.ticho.boot.es.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.es.entity.Entity;
import com.ticho.boot.es.page.PageInfo;
import com.ticho.boot.es.query.EsQuery;
import com.ticho.boot.es.service.ElasticSearchService;
import com.ticho.boot.json.util.JsonUtil;
import com.ticho.boot.view.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
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
 *
 *
 * @author zhajianjun
 * @date 2023-04-15 12:26:56
 */
@Slf4j
public class ElasticSearchServiceImpl implements ElasticSearchService {

    /**
     * The constant MAX_BATCH_SIZE.
     */
    private static final int MAX_BATCH_SIZE = 1000;

    /**
     * The Rest client.
     */
    private RestHighLevelClient restClient;

    @Override
    public void saveOrUpdate(String type, String index, Entity entity) {
        UpdateRequest updateRequest = getUpdateRequest(type, index, entity, true);
        this.update(updateRequest);
    }

    @Override
    public void saveOrUpdate(String type, String index, Map<String, String> entity) {
        UpdateRequest updateRequest = getUpdateRequest(type, index, entity, true);
        this.update(updateRequest);
    }

    public void saveOrUpdateBatch(String type, String index, List<? extends Entity> entities, Integer bachSize) {
        if (entities == null) {
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entities.size() <= MAX_BATCH_SIZE) {
            saveOrUpdateBatch(type, index, entities);
            return;
        }
        List<? extends List<? extends Entity>> split = CollUtil.split(entities, bachSize);
        for (List<? extends Entity> maps : split) {
            saveOrUpdateBatch(type, index, maps);
        }
    }

    public void saveOrUpdateDb(String type, String index, Map<String, String> entity) {
        UpdateRequest updateRequest = getUpdateRequest(type, index, entity, true);
        this.update(updateRequest);
    }

    /**
     * Save or update batch.
     *
     * @param type the type
     * @param index the index
     * @param entities the entities
     */
    public void saveOrUpdateBatch(String type, String index, List<? extends Entity> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Entity entity : entities) {
                UpdateRequest updateRequest = getUpdateRequest(type, index, entity, true);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("更新数据失败");
        }
    }

    public void saveOrUpdateBatchForMap(String type, String index, List<Map<String, String>> entities){
        saveOrUpdateBatchForMap(type, index, entities, MAX_BATCH_SIZE);
    }

    public void saveOrUpdateBatchForMap(String type, String index, List<Map<String, String>> entities, Integer bachSize) {
        if (entities == null) {
            return;
        }
        if (bachSize == null || bachSize < 0) {
            bachSize = MAX_BATCH_SIZE;
        }
        if (entities.size() <= MAX_BATCH_SIZE) {
            saveOrUpdateBatchForMapDb(type, index, entities);
            return;
        }
        List<List<Map<String, String>>> split = CollUtil.split(entities, bachSize);
        for (List<Map<String, String>> maps : split) {
            saveOrUpdateBatchForMapDb(type, index, maps);
        }
    }

    /**
     * Save or update batch for map.
     *
     * @param type the type
     * @param index the index
     * @param entities the entities
     */
    public void saveOrUpdateBatchForMapDb(String type, String index, List<Map<String, String>> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Map<String, String> entity : entities) {
                UpdateRequest updateRequest = getUpdateRequest(type, index, entity, true);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("更新数据失败");
        }
    }


    @Override
    public void removeById(String type, String index, String id) {
        try {
            DeleteRequest deleteRequest = Requests.deleteRequest(index).type(type).id(id);
            DeleteResponse response = restClient.delete(deleteRequest, RequestOptions.DEFAULT);
            printStatus(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("删除数据失败");
        }
    }

    @Override
    public void removeByIds(String type, String index, Collection<String> ids) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (String id : ids) {
                DeleteRequest deleteRequest = Requests.deleteRequest(index).type(type).id(id);
                bulkRequest.add(deleteRequest);
            }
            BulkResponse response = restClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("删除数据失败");
        }
    }

    @Override
    public void updateById(String type, String index, Entity entity) {
        UpdateRequest updateRequest = getUpdateRequest(type, index, entity, false);
        update(updateRequest);
    }

    /**
     * Update.
     *
     * @param updateRequest the update request
     */
    private void update(UpdateRequest updateRequest) {
        try {
            UpdateResponse update = restClient.update(updateRequest, RequestOptions.DEFAULT);
            printStatus(update);
        } catch (ElasticsearchStatusException e) {
            RestStatus status = e.status();
            if (status.compareTo(RestStatus.NOT_FOUND) == 0) {
                log.warn("更新失败，数据不存在");
            } else {
                log.error("{}, status is {}", e.getMessage(), status.name(), e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("更新数据失败");
        }
    }

    @Override
    public void updateById(String type, String index, Map<String, String> entity) {
        UpdateRequest updateRequest = getUpdateRequest(type, index, entity, false);
        update(updateRequest);
    }

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
            BulkResponse response = restClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("更新数据失败");
        }
    }

    public void updateBatchForMap(String type, String index, List<Map<String, String>> entities){
        updateBatchForMap(type, index, entities, MAX_BATCH_SIZE);
    }

    public void updateBatchForMap(String type, String index, List<Map<String, String>> entities, Integer bachSize) {
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
        List<List<Map<String, String>>> split = CollUtil.split(entities, bachSize);
        for (List<Map<String, String>> maps : split) {
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
    public void updateBatchForMapDb(String type, String index, List<Map<String, String>> entities) {
        try {
            BulkRequest bulkInsertRequest = new BulkRequest();
            for (Map<String, String> entity : entities) {
                UpdateRequest updateRequest = getUpdateRequest(type, index, entity, false);
                bulkInsertRequest.add(updateRequest);
            }
            BulkResponse response = restClient.bulk(bulkInsertRequest, RequestOptions.DEFAULT);
            printStatus(response);
            printErrorMessage(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("更新数据失败");
        }
    }

    @Override
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

    @Override
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
     * Search response search response.
     *
     * @param esQuery the es query
     * @return the search response
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
            searchResponse = restClient.search(searchRequest, RequestOptions.DEFAULT);
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
     * Sets sort field.
     *
     * @param esQuery the es query
     * @param searchBuilder the search builder
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
     * Gets update request.
     *
     * @param type the type
     * @param index the index
     * @param entity the entity
     * @param shouldUpsertDoc the should upsert doc
     * @return the update request
     */
    private UpdateRequest getUpdateRequest(String type, String index, Entity entity, boolean shouldUpsertDoc) {
        String id = entity.getId();
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(JsonUtil.toJsonString(entity), XContentType.JSON);
        updateRequest.doc(indexRequest);
        updateRequest.docAsUpsert(shouldUpsertDoc);
        return updateRequest;
    }

    /**
     * Gets update request.
     *
     * @param type the type
     * @param index the index
     * @param entity the entity
     * @param shouldUpsertDoc the should upsert doc
     * @return the update request
     */
    private UpdateRequest getUpdateRequest(String type, String index, Map<String, String> entity, boolean shouldUpsertDoc) {
        String id = entity.get("id");
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(entity);
        updateRequest.doc(indexRequest);
        updateRequest.docAsUpsert(shouldUpsertDoc);
        return updateRequest;
    }

    /**
     * Print status.
     *
     * @param toContentObject the to content object
     */
    public void printStatus(StatusToXContentObject toContentObject) {
        if (log.isDebugEnabled()) {
            RestStatus restStatus = toContentObject.status();
            log.debug("es status is {}", restStatus.name());
        }
    }

    /**
     * Gets error message.
     *
     * @param response the response
     * @return the error message
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
     * Print error message.
     *
     * @param response the response
     */
    public void printErrorMessage(BulkResponse response) {
        String errorMessage = getErrorMessage(response);
        if (StrUtil.isEmpty(errorMessage)) {
            return;
        }
        log.error(errorMessage);
    }

}

