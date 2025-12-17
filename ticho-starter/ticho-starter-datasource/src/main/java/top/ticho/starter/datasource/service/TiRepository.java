package top.ticho.starter.datasource.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import top.ticho.starter.datasource.mapper.TiMapper;
import top.ticho.starter.datasource.util.TiPageUtil;
import top.ticho.starter.view.core.TiPageQuery;
import top.ticho.starter.view.core.TiPageResult;

import java.util.Collection;
import java.util.function.Function;

/**
 * TiRepository
 *
 * @author zhajianjun
 * @date 2022-10-17 13:28
 */
public interface TiRepository<T> extends IService<T> {

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

    TiMapper<T> getTiMapper();

    /**
     * 分页查询
     *
     * @param query        分页参数
     * @param queryWrapper 查询条件
     */
    default <M extends TiPageQuery> TiPageResult<T> page(M query, Wrapper<T> queryWrapper) {
        Page<T> page = new Page<>(query.getPageNum(), query.getPageSize(), query.getCount());
        getTiMapper().selectPage(query, queryWrapper);
        return TiPageUtil.of(page);
    }

    /**
     * 分页查询
     *
     * @param query        分页参数
     * @param queryWrapper 查询条件
     * @param mapping      转换
     */
    default <M extends TiPageQuery, N> TiPageResult<N> page(M query, Wrapper<T> queryWrapper, Function<T, N> mapping) {
        Page<T> page = new Page<>(query.getPageNum(), query.getPageSize(), query.getCount());
        getTiMapper().selectPage(query, queryWrapper, mapping);
        return TiPageUtil.of(page, mapping);
    }

}
