package top.ticho.starter.es.service.impl;

import cn.easyes.core.biz.EntityInfo;
import cn.easyes.core.conditions.select.LambdaEsQueryWrapper;
import cn.easyes.core.core.BaseEsMapper;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import top.ticho.starter.es.service.BaseEsService;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author zhajianjun
 * @date 2023-04-25 15:53
 */
@Slf4j
public class BaseEsServiceImpl<M extends BaseEsMapper<T>, T> implements BaseEsService<T> {

    @Resource
    protected M baseEsMapper;

    @Override
    public BaseEsMapper<T> getBaseMapper() {
        return baseEsMapper;
    }

    @Override
    public Class<T> getEntityClass() {
        return baseEsMapper.getEntityClass();
    }

    public EntityInfo getEntityInfo() {
        return baseEsMapper.getEntityInfo();
    }

    @Override
    public String getIndexName() {
        EntityInfo entityInfo = getEntityInfo();
        return entityInfo.getIndexName();
    }

    @Override
    public boolean save(T entity, String... indexNames) {
        if (entity == null) {
            log.info("索引{}保存异常，对象为null", String.join(",", indexNames));
            return false;
        }
        return baseEsMapper.insert(entity, indexNames) > 0;
    }

    @Override
    public boolean removeById(Serializable id, String... indexNames) {
        if (id == null) {
            log.info("索引{}删除异常，主键ID为null", String.join(",", indexNames));
            return false;
        }
        return baseEsMapper.deleteById(id, indexNames) > 0;
    }

    @Override
    public boolean updateById(T entity, String... indexNames) {
        if (entity == null) {
            log.info("索引{}更新异常，对象为null", String.join(",", indexNames));
            return false;
        }
        return baseEsMapper.updateById(entity, indexNames) > 0;
    }

    @Override
    public T getById(Serializable id, String... indexNames) {
        return baseEsMapper.selectById(id, indexNames);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize, String... indexNames) {
        if (CollUtil.isEmpty(entityList)) {
            log.info("索引{}批量保存异常，集合为null或者大小为0", String.join(",", indexNames));
            return false;
        }
        if (batchSize <= 0 || batchSize > 1000) {
            batchSize = BaseEsService.DEFAULT_BATCH_SIZE;
        }
        int size = entityList.size();
        if (size <= batchSize) {
            return size == baseEsMapper.insertBatch(entityList, indexNames);
        }
        List<List<T>> split = CollUtil.split(entityList, batchSize);
        Integer total = split.stream().map(x -> baseEsMapper.insertBatch(x, indexNames)).reduce(0, Integer::sum);
        return total == entityList.size();
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize, String... indexNames) {
        if (CollUtil.isEmpty(entityList)) {
            log.info("索引{}批量更新异常，集合为null或者大小为0", String.join(",", indexNames));
            return false;
        }
        if (batchSize <= 0 || batchSize > 1000) {
            batchSize = BaseEsService.DEFAULT_BATCH_SIZE;
        }
        int size = entityList.size();
        if (size <= batchSize) {
            return size == baseEsMapper.updateBatchByIds(entityList, indexNames);
        }
        List<List<T>> split = CollUtil.split(entityList, batchSize);
        Integer total = split.stream().map(x -> baseEsMapper.updateBatchByIds(x, indexNames)).reduce(0, Integer::sum);
        return total == size;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList, int batchSize, String... indexNames) {
        if (CollUtil.isEmpty(idList)) {
            log.info("索引{}批量删除异常，集合为null或者大小为0", String.join(",", indexNames));
            return false;
        }
        if (batchSize <= 0 || batchSize > 1000) {
            batchSize = BaseEsService.DEFAULT_BATCH_SIZE;
        }
        int size = idList.size();
        if (size <= batchSize) {
            return size == baseEsMapper.deleteBatchIds(idList, indexNames);
        }
        List<? extends List<? extends Serializable>> split = CollUtil.split(idList, batchSize);
        Integer total = split.stream().map(x -> baseEsMapper.deleteBatchIds(x, indexNames)).reduce(0, Integer::sum);
        return total == size;
    }

    @Override
    public List<T> list(LambdaEsQueryWrapper<T> wrapper) {
        return baseEsMapper.selectList(wrapper);
    }

    @Override
    public Long count(LambdaEsQueryWrapper<T> wrapper) {
        return baseEsMapper.selectCount(wrapper);
    }


}
