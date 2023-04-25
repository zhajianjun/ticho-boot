package com.ticho.boot.es.service;

import cn.easyes.core.biz.EntityInfo;
import cn.easyes.core.conditions.interfaces.BaseEsMapper;

import java.io.Serializable;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-25 15:53
 */
public interface BaseEsService<T> {

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
    boolean save(T entity);

    /**
     * 删除数据字典
     *
     * @param id 编号
     */
    boolean removeById(Serializable id);

    /**
     * 修改数据字典
     *
     * @param entity 数据对象
     */
    boolean updateById(T entity);

    /**
     * 根据id查询数据字典
     *
     * @param id 主键
     * @return {@link T}
     */
    T getById(Serializable id);


}
