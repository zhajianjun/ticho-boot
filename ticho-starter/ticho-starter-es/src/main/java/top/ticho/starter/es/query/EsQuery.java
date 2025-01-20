package top.ticho.boot.es.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author zhajianjun
 * @date 2023-04-15 12:21:05
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EsQuery<T> extends EsSimpleQuery {
    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final int DEFAULT_MAX_PAGE_SIZE = 10000;

    /**
     * es 查询条件
     */
    private QueryBuilder queryBuilder;

    /**
     * 转换对象的类
     */
    private Class<T> clazz;

    public static <T> EsQuery<T> convert(EsSimpleQuery esSimpleQuery) {
        if (esSimpleQuery == null) {
            return null;
        }
        EsQuery<T> objectEsQuery = new EsQuery<>();
        objectEsQuery.setIndexs(esSimpleQuery.getIndexs());
        objectEsQuery.setFrom(esSimpleQuery.getFrom());
        objectEsQuery.setSize(esSimpleQuery.getSize());
        objectEsQuery.setFields(esSimpleQuery.getFields());
        objectEsQuery.setSortFields(esSimpleQuery.getSortFields());
        objectEsQuery.setHighlightField(esSimpleQuery.getHighlightField());
        objectEsQuery.setPageSize(esSimpleQuery.getPageSize());
        objectEsQuery.setPageNum(esSimpleQuery.getPageNum());
        objectEsQuery.setSearchAfter(esSimpleQuery.getSearchAfter());
        objectEsQuery.setSortOrders(esSimpleQuery.getSortOrders());
        return objectEsQuery;
    }

}
