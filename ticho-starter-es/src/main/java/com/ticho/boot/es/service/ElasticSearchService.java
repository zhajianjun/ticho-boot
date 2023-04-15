package com.ticho.boot.es.service;

import com.ticho.boot.es.entity.Entity;
import com.ticho.boot.es.page.PageInfo;
import com.ticho.boot.es.query.EsQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * es数据操作接口
 *
 * @author zhajianjun
 * @date 2023-04-15 12:25:51
 */
public interface ElasticSearchService {

    /**
     * 保存或者更新es数据
     *
     * @param type 文档
     * @param index 索引
     * @param entity 对象
     */
    void saveOrUpdate(String type, String index, Entity entity);

    /**
     * 保存或者更新es数据
     *
     * @param type 文档
     * @param index 索引
     * @param entity 对象
     */
    void saveOrUpdate(String type, String index, Map<String, String> entity);

    /**
     * 批量保存或者更新es数据
     *
     * @param type 文档
     * @param index 索引
     * @param entities 对象列表
     */
    void saveOrUpdateBatch(String type, String index, List<? extends Entity> entities);

    /**
     * 批量保存或者更新es数据
     *
     * @param type 文档
     * @param index 索引
     * @param entities 对象列表
     */
    void saveOrUpdateBatchForMap(String type, String index, List<Map<String, String>> entities);

    /**
     * 根据id删除es数据
     *
     * @param type 文档
     * @param index 索引
     * @param id 编号
     */
    void removeById(String type, String index, String id);

    /**
     * 批量根据id删除数据
     *
     * @param type 文档
     * @param index 索引
     * @param ids 编号列表
     */
    void removeByIds(String type, String index, Collection<String> ids);

    /**
     * 根据id更改数据
     *
     * @param type 文档
     * @param index 索引
     * @param entity 对象
     */
    void updateById(String type, String index, Entity entity);


    /**
     * 根据id更改数据
     *
     * @param type 文档
     * @param index 索引
     * @param entity 对象
     */
    void updateById(String type, String index, Map<String, String> entity);

    /**
     * 根据id更改数据
     *
     * @param type 文档
     * @param index 索引
     * @param entities 对象列表
     */
    void updateBatch(String type, String index, List<? extends Entity> entities);


    /**
     * 根据id更改数据
     *
     * @param type 文档
     * @param index 索引
     * @param entities 对象列表
     */
    void updateBatchForMap(String type, String index, List<Map<String, String>> entities);


    /**
     * 根据id查询数据
     *
     * @param type 文档
     * @param index 索引
     * @param id 编号
     * @param tClass 返回的对象类
     * @return T
     */
    <T> T getById(String type, String index, String id, Class<T> tClass);

    /**
     * 根据条件查询
     *
     * @param esQuery es查询条件
     * @return 查询数据
     */
    <T> PageInfo<T> getPageInfo(EsQuery<T> esQuery);
}

