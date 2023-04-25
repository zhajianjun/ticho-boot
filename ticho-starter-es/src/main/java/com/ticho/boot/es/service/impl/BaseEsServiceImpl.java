package com.ticho.boot.es.service.impl;

import cn.easyes.core.biz.EntityInfo;
import cn.easyes.core.conditions.interfaces.BaseEsMapper;
import com.ticho.boot.es.service.BaseEsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-25 15:53
 */
@Slf4j
public class BaseEsServiceImpl<M extends BaseEsMapper<T>, T> implements BaseEsService<T> {

    @Autowired
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
    public boolean save(T entity) {
        if (entity == null) {
            log.info("{}保存异常，对象为null", getIndexName());
            return false;
        }
        return baseEsMapper.insert(entity) == 1;
    }

    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            log.info("{}删除异常，主键ID为null", getIndexName());
            return false;
        }
        return baseEsMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateById(T entity) {
        if (entity == null) {
            log.info("{}更新异常，角色为null", getIndexName());
            return false;
        }
        return baseEsMapper.updateById(entity) > 0;
    }

    @Override
    public T getById(Serializable id) {
        return null;
    }

}
