package top.ticho.starter.datasource.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import java.util.List;

/**
 * 自定义sql注入器
 *
 * @author zhajianjun
 * @date 2022-10-17 09:14
 */
public class TiSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new TiInsertBatch());
        methodList.add(new TiUpdateBatch());
        methodList.add(new TiInsertOrUpdateBatch());
        methodList.add(new TiInsertOrUpdate());
        return methodList;
    }

}
