package top.ticho.boot.datasource.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * TiRootService
 *
 * @author zhajianjun
 * @date 2022-10-17 13:28
 */
public interface TiRootService<T> extends IService<T> {

    /**
     * 获取批量大小
     *
     * @return {@link Integer }
     */
    Integer batchSize();

    /**
     * 最大批量大小
     *
     * @return {@link Integer }
     */
    Integer maxBatchSize();

    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    default boolean saveBatch(Collection<T> entityList) {
        return saveBatch(entityList, batchSize());
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    default boolean updateBatchById(Collection<T> entityList) {
        return updateBatchById(entityList, batchSize());
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    default boolean saveOrUpdateBatch(Collection<T> entityList) {
        return saveOrUpdateBatch(entityList, batchSize());
    }

    default boolean saveOrUpdate(T entity, Wrapper<T> updateWrapper) {
        return this.saveOrUpdate(entity);
    }

}
