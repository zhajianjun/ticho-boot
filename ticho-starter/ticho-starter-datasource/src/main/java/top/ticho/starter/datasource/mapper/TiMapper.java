package top.ticho.starter.datasource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

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

}
