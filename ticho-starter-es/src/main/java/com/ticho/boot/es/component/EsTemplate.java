package com.ticho.boot.es.component;

import com.ticho.boot.es.page.PageInfo;
import com.ticho.boot.es.query.EsQuery;
import com.ticho.boot.view.core.Entity;
import com.ticho.boot.view.core.EsPageResult;
import com.ticho.boot.view.core.PageResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-17 15:08
 */
public interface EsTemplate {

    /**
     * 保存es数据
     *
     * @param index 索引
     * @param entity 对象
     */
    void save(String index, Entity entity);

    /**
     * 保存es数据 map
     *
     * @param index 索引
     * @param entity 对象
     */
    void save(String index, Map<String, Object> entity);

    /**
     * 批量保存数据
     *
     * @param index 指数
     * @param entities 实体
     */
    void saveBatch(String index, List<? extends Entity> entities);

    /**
     * 批量保存数据 map
     *
     * @param index 指数
     * @param entities 实体
     */
    void saveBatchForMap(String index, List<Map<String, Object>> entities);

    /**
     * 保存或者更新es数据
     *
     * @param index 索引
     * @param entity 对象
     */
    void saveOrUpdate(String index, Entity entity);

    /**
     * 保存或者更新es数据
     *
     * @param index 索引
     * @param entity 对象
     */
    void saveOrUpdate(String index, Map<String, Object> entity);

    /**
     * 批量保存或者更新es数据
     *
     * @param index 索引
     * @param entities 对象列表
     */
    void saveOrUpdateBatch(String index, List<? extends Entity> entities);

    /**
     * 批量保存或者更新es数据
     *
     * @param index 索引
     * @param entities 对象列表
     */
    void saveOrUpdateBatchForMap(String index, List<Map<String, Object>> entities);

    /**
     * 根据id删除es数据
     *
     * @param index 索引
     * @param id 编号
     */
    void removeById(String index, String id);

    /**
     * 批量根据id删除数据
     *
     * @param index 索引
     * @param ids 编号列表
     */
    void removeByIds(String index, Collection<String> ids);

    /**
     * 根据id更改数据
     *
     * @param index 索引
     * @param entity 对象
     */
    void updateById(String index, Entity entity);

    /**
     * 根据id更改数据
     *
     * @param index 索引
     * @param entity 对象
     */
    void updateById(String index, Map<String, Object> entity);

    /**
     * 根据id更改数据
     *
     * @param index 索引
     * @param entities 对象列表
     */
    void updateBatch(String index, List<? extends Entity> entities);

    /**
     * 根据id更改数据
     *
     * @param index 索引
     * @param entities 对象列表
     */
    void updateBatchForMap(String index, List<Map<String, Object>> entities);

    /**
     * 根据id查询数据
     *
     * @param index 索引
     * @param id 编号
     * @param tClass 返回的对象类
     * @return T
     */
    <T> T getById(String index, String id, Class<T> tClass);

    /**
     * 根据id查询数据
     *
     * @param index 索引
     * @param id id
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getById(String index, String id);

    /**
     * 根据条件查询
     *
     * @param esQuery es查询条件
     * @return 查询数据
     */
    <T> EsPageResult<T> page(EsQuery<T> esQuery);

    /**
     * 根据条件查询
     *
     * @param esQuery es查询
     * @return {@link PageInfo}<{@link Map}<{@link String}, {@link Object}>>
     */
    EsPageResult<Map<String, Object>> pageForMap(EsQuery<Map<String, Object>> esQuery);

}
