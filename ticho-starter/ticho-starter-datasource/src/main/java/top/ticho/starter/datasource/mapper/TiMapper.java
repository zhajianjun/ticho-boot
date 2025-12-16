package top.ticho.starter.datasource.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.ticho.starter.datasource.util.TiPageUtil;
import top.ticho.starter.view.core.TiPageQuery;
import top.ticho.starter.view.core.TiPageResult;

import java.util.Collection;
import java.util.function.Function;

/**
 * TiMapper接口，继承自MyBatis-Plus的BaseMapper，提供自定义的批量操作方法
 *
 * @author zhajianjun
 * @date 2022-10-17 08:53
 */
public interface TiMapper<T> extends BaseMapper<T> {

    /**
     * 自定义批量插入
     *
     * @param list 要插入的实体对象集合
     * @return 插入操作影响的行数
     */
    int insertBatch(@Param("list") Collection<T> list);

    /**
     * 自定义批量更新
     *
     * @param list 要更新的实体对象集合
     * @return 更新操作影响的行数
     */
    int updateBatch(@Param("list") Collection<T> list);

    /**
     * 自定义新增或者更新
     *
     * @param data 要插入或更新的实体对象
     * @return 插入或更新操作影响的行数
     */
    int insertOrUpdateT(T data);

    /**
     * 自定义批量新增或者更新
     *
     * @param list 要插入或更新的实体对象集合
     * @return 插入或更新操作影响的行数
     */
    int insertOrUpdateBatch(@Param("list") Collection<T> list);

    /**
     * 分页查询
     *
     * @param query        分页参数
     * @param queryWrapper 查询条件
     */
    default <M extends TiPageQuery> TiPageResult<T> selectPage(M query, Wrapper<T> queryWrapper) {
        Page<T> page = new Page<>(query.getPageNum(), query.getPageSize(), query.getCount());
        selectPage(page, queryWrapper);
        return TiPageUtil.of(page);
    }

    /**
     * 分页查询
     *
     * @param query        分页参数
     * @param queryWrapper 查询条件
     * @param mapping      转换
     */
    default <M extends TiPageQuery, N> TiPageResult<N> selectPage(M query, Wrapper<T> queryWrapper, Function<T, N> mapping) {
        Page<T> page = new Page<>(query.getPageNum(), query.getPageSize(), query.getCount());
        selectPage(page, queryWrapper);
        return TiPageUtil.of(page, mapping);
    }

}
