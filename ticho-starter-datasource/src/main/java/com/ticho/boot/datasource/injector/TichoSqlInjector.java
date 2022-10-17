package com.ticho.boot.datasource.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import java.util.List;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-10-17 09:14
 */
public class TichoSqlInjector extends DefaultSqlInjector {


    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        //更新时自动填充的字段，不用插入值
        methodList.add(new InsertBatch());
        methodList.add(new UpdateBatch());
        return methodList;
    }

}
