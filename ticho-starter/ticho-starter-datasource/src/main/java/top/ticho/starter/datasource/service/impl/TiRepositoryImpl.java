package top.ticho.starter.datasource.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import top.ticho.starter.datasource.mapper.TiMapper;
import top.ticho.starter.datasource.prop.TiDataSourceProperty;
import top.ticho.starter.datasource.service.TiRepository;

import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * TiRepository 实现类（ 泛型：M 是 mapper 对象，T 是实体 ）
 *
 * @author zhajianjun
 * @date 2022-10-17 13:29
 */
@Slf4j
public class TiRepositoryImpl<M extends TiMapper<T>, T> extends ServiceImpl<M, T> implements TiRepository<T> {

    @Resource
    private TiDataSourceProperty tiDataSourceProperty;

    public TableInfo getTableInfo() {
        return TableInfoHelper.getTableInfo(this.getEntityClass());
    }

    public String getTableName() {
        return getTableInfo().getTableName();
    }

    @Override
    public Integer batchSize() {
        return tiDataSourceProperty.getBatchSize();
    }

    @Override
    public Integer maxBatchSize() {
        return tiDataSourceProperty.getMaxBatchSize();
    }

    @Override
    public boolean save(T entity) {
        if (entity == null) {
            log.info("{}保存异常，对象为null", getTableName());
            return false;
        }
        return baseMapper.insert(entity) == 1;
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        if (CollUtil.isEmpty(entityList)) {
            log.info("{}批量保存异常，集合为null或者大小为0", getTableName());
            return false;
        }
        if (batchSize <= 0 || batchSize > tiDataSourceProperty.getMaxBatchSize()) {
            batchSize = batchSize();
        }
        int size = entityList.size();
        if (size <= batchSize) {
            return size == baseMapper.insertBatch(entityList);
        }
        List<List<T>> split = CollUtil.split(entityList, batchSize);
        Integer total = split.stream().map(baseMapper::insertBatch).reduce(0, Integer::sum);
        return total == entityList.size();
    }

    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            log.info("{}删除异常，主键ID为null", getTableName());
            return false;
        }
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean removeByIds(Collection<?> ids) {
        return removeByIds(ids, batchSize());
    }

    public boolean removeByIds(Collection<?> ids, int batchSize) {
        if (CollUtil.isEmpty(ids)) {
            log.info("{}批量删除异常，集合为null或者大小为0", getTableName());
            return false;
        }
        if (batchSize <= 0 || batchSize > tiDataSourceProperty.getMaxBatchSize()) {
            batchSize = batchSize();
        }
        int size = ids.size();
        if (size <= batchSize) {
            return size == baseMapper.deleteByIds(ids);
        }
        List<? extends List<?>> split = CollUtil.split(ids, batchSize);
        Integer total = split.stream().map(baseMapper::deleteByIds).reduce(0, Integer::sum);
        return total == size;
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        if (CollUtil.isEmpty(entityList)) {
            log.info("{}批量修改异常，集合为null或者大小为0", getTableName());
            return false;
        }
        int size = entityList.size();
        if (size <= batchSize) {
            return size == baseMapper.updateBatch(entityList);
        }
        List<List<T>> split = CollUtil.split(entityList, batchSize);
        Integer total = split.stream().map(baseMapper::updateBatch).reduce(0, Integer::sum);
        return total == entityList.size();
    }

    @Override
    public boolean updateById(T func) {
        if (func == null) {
            log.info("{}更新异常，对象为null", getTableName());
            return false;
        }
        return baseMapper.updateById(func) > 0;
    }

    @Override
    public T getById(Serializable id) {
        if (id == null) {
            log.info("{}查询异常，主键ID为null", getTableName());
            return null;
        }
        return baseMapper.selectById(id);
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdate(T entity) {
        if (entity == null) {
            log.info("{}保存更新异常，对象为null", getTableName());
            return false;
        }
        int result = baseMapper.insertOrUpdateT(entity);
        return result >= 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        if (CollUtil.isEmpty(entityList)) {
            log.info("{}批量保存更新异常，集合为null或者大小为0", getTableName());
            return false;
        }
        int size = entityList.size();
        if (size <= batchSize) {
            return size == baseMapper.insertOrUpdateBatch(entityList);
        }
        List<List<T>> split = CollUtil.split(entityList, batchSize);
        Integer total = split.stream().map(baseMapper::insertOrUpdateBatch).reduce(0, Integer::sum);
        return total == entityList.size();
    }

}
