package top.ticho.boot.es.service;

import cn.easyes.core.biz.EntityInfo;
import cn.easyes.core.conditions.select.LambdaEsQueryWrapper;
import cn.easyes.core.core.BaseEsMapper;
import cn.easyes.core.core.EsWrappers;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-25 15:53
 */
public interface BaseEsService<T> {

    /**
     * 默认批次提交数量
     */
    int DEFAULT_BATCH_SIZE = 200;

    /**
     * 获取 entity 的 class
     *
     * @return {@link Class<T>}
     */
    Class<T> getEntityClass();

    /**
     * 得到实体信息
     *
     * @return {@link EntityInfo}
     */
    EntityInfo getEntityInfo();

    /**
     * 得到索引名称
     *
     * @return {@link String}
     */
    String getIndexName();

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    BaseEsMapper<T> getBaseMapper();

    /**
     * 保存数据
     *
     * @param entity 数据对象
     */
    default boolean save(T entity) {
        return save(entity, getIndexName());
    }

    /**
     * 保存数据
     *
     * @param entity 实体
     * @param indexNames 索引名称
     * @return boolean
     */
    boolean save(T entity, String... indexNames);

    /**
     * 删除数据
     *
     * @param id id
     * @return boolean
     */
    default boolean removeById(Serializable id) {
        return removeById(id, getIndexName());
    }

    /**
     * 删除数据
     *
     * @param id id
     * @param indexNames 索引名称
     * @return boolean
     */
    boolean removeById(Serializable id, String... indexNames);

    /**
     * 修改数据
     *
     * @param entity 数据对象
     * @return boolean
     */
    default boolean updateById(T entity) {
        return updateById(entity, getIndexName());
    }

    /**
     * 修改数据
     *
     * @param entity 数据对象
     * @param indexNames 索引名称
     * @return boolean
     */
    boolean updateById(T entity, String... indexNames);

    /**
     * 根据id查询数据
     *
     * @param id 主键
     * @return {@link T}
     */
    default T getById(Serializable id) {
        return getById(id, getIndexName());
    }

    /**
     * 根据id查询数据
     *
     * @param id id
     * @param indexNames 索引名称
     * @return {@link T}
     */
    T getById(Serializable id, String... indexNames);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表
     */
    default List<T> listByIds(Collection<? extends Serializable> idList) {
        return getBaseMapper().selectBatchIds(idList);
    }

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList     主键列表
     * @param indexNames 指定查询的索引名数组
     */
    default List<T> listByIds(Collection<? extends Serializable> idList, String... indexNames) {
        return getBaseMapper().selectBatchIds(idList, indexNames);
    }

    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     * @param batchSize  插入批次数量
     */
    default boolean saveBatch(Collection<T> entityList, int batchSize) {
        return saveBatch(entityList, batchSize, getIndexName());
    }

    /**
     * 批量插入
     *
     * @param entityList 插入的数据对象列表
     * @param indexNames 指定插入的索引名数组
     * @return 是否插入成功
     */
    boolean saveBatch(Collection<T> entityList, int batchSize, String... indexNames);

    /**
     * 批量插入
     *
     * @param entityList 插入的数据对象列表
     * @return 是否插入成功
     */
    default boolean saveBatch(Collection<T> entityList) {
        return saveBatch(entityList, DEFAULT_BATCH_SIZE, getIndexName());
    }

    /**
     * 批量插入
     *
     * @param entityList 插入的数据对象列表
     * @param indexNames 指定插入的索引名数组
     * @return 是否插入成功
     */
    default boolean saveBatch(Collection<T> entityList, String... indexNames) {
        return saveBatch(entityList, DEFAULT_BATCH_SIZE, indexNames);
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 更新对象列表
     * @return 是否更新成功
     */
    default boolean updateBatchById(Collection<T> entityList) {
        return updateBatchById(entityList, DEFAULT_BATCH_SIZE, getIndexName());
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 更新对象列表
     * @param indexNames 指定更新的索引名称数组
     * @return 是否更新成功
     */
    default boolean updateBatchById(Collection<T> entityList, String... indexNames) {
        return updateBatchById(entityList, DEFAULT_BATCH_SIZE, indexNames);
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 更新对象列表
     * @return 是否更新成功
     */
    default boolean updateBatchById(Collection<T> entityList, int batchSize) {
        return updateBatchById(entityList, batchSize, getIndexName());
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 更新对象列表
     * @param indexNames 指定更新的索引名称数组
     * @return 是否更新成功
     */
    boolean updateBatchById(Collection<T> entityList, int batchSize, String... indexNames);


    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键ID列表
     */
    default boolean removeByIds(Collection<? extends Serializable> idList) {
        return removeByIds(idList, DEFAULT_BATCH_SIZE, getIndexName());
    }

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList id列表
     * @param indexNames 索引名称
     * @return 是否删除成功
     */
    default boolean removeByIds(Collection<? extends Serializable> idList, String... indexNames) {
        return removeByIds(idList, DEFAULT_BATCH_SIZE, indexNames);
    }

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList id列表
     * @param batchSize 批量大小
     * @return 是否删除成功
     */
    default boolean removeByIds(Collection<? extends Serializable> idList, int batchSize) {
        return removeByIds(idList, batchSize, getIndexName());
    }

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList id列表
     * @param batchSize 批量大小
     * @param indexNames 索引名称
     * @return 是否删除成功
     */
    boolean removeByIds(Collection<? extends Serializable> idList, int batchSize, String... indexNames);

    /**
     * 列表查询
     *
     * @param wrapper 包装器
     * @return {@link List}<{@link T}>
     */
    List<T> list(LambdaEsQueryWrapper<T> wrapper);

    /**
     * 列表查询
     *
     * @param indexNames 索引名称
     * @return {@link List}<{@link T}>
     */
    default List<T> list(String... indexNames) {
        LambdaEsQueryWrapper<T> wrapper = EsWrappers.lambdaQuery(null);
        wrapper.index(indexNames);
        return list(wrapper);
    }

    /**
     * 列表查询
     *
     * @return {@link List}<{@link T}>
     */
    default List<T> list() {
        return list(EsWrappers.lambdaQuery(null));
    }

    /**
     * 统计数量
     *
     * @param wrapper 包装器
     * @return {@link Long}
     */
    Long count(LambdaEsQueryWrapper<T> wrapper);

    /**
     * 统计数量
     *
     * @param indexNames 索引名称
     * @return {@link Long}
     */
    default Long count(String... indexNames) {
        LambdaEsQueryWrapper<T> wrapper = EsWrappers.lambdaQuery(null);
        wrapper.index(indexNames);
        return count(wrapper);
    }

    /**
     * 统计数量
     *
     * @return {@link Long}
     */
    default Long count() {
        return count(EsWrappers.lambdaQuery(null));
    }

}
