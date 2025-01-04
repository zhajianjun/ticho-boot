package top.ticho.boot.datasource.injector;

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
public class BaseSqlInjector extends DefaultSqlInjector {


    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new InsertBatch());
        methodList.add(new UpdateBatch());
        methodList.add(new InsertOrUpdateBatch());
        methodList.add(new InsertOrUpdate());
        return methodList;
    }

}
