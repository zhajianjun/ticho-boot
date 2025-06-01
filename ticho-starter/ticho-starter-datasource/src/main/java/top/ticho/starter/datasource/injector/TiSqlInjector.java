package top.ticho.starter.datasource.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * 自定义sql注入器
 *
 * @author zhajianjun
 * @date 2022-10-17 09:14
 */
public class TiSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
        if (tableInfo.havePK()) {
            methodList.add(new TiInsertBatch());
            methodList.add(new TiUpdateBatch());
            methodList.add(new TiInsertOrUpdateBatch());
            methodList.add(new TiInsertOrUpdate());
        } else {
            methodList.add(new TiInsertBatch());
            logger.warn(String.format("%s ,Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.",
                tableInfo.getEntityType()));
        }
        return methodList;
    }

}
