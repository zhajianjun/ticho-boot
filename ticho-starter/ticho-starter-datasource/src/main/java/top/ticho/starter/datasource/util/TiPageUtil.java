package top.ticho.starter.datasource.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.ticho.starter.view.core.TiPageQuery;
import top.ticho.starter.view.core.TiPageResult;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页对象转换工具
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TiPageUtil {

    public static <T> TiPageResult<T> of(Number pageNum, Number pageSize, Number total, List<T> rows) {
        return new TiPageResult<>(pageNum, pageSize, total, rows);
    }

    public static <T, R> TiPageResult<R> of(TiPageResult<T> source, Function<T, R> function) {
        TiPageResult<R> target = new TiPageResult<>();
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setPages(source.getPages());
        target.setTotal(source.getTotal());
        target.setRows(source.getRows().stream().map(function).collect(Collectors.toList()));
        return target;
    }

    public static <T> TiPageResult<T> of(IPage<T> page) {
        TiPageResult<T> tiPageResult = new TiPageResult<>();
        tiPageResult.setPageNum(Long.valueOf(page.getCurrent()).intValue());
        tiPageResult.setPageSize(Long.valueOf(page.getSize()).intValue());
        tiPageResult.setPages(Long.valueOf(page.getPages()).intValue());
        tiPageResult.setTotal(Long.valueOf(page.getTotal()).intValue());
        tiPageResult.setRows(page.getRecords());
        return tiPageResult;
    }

    public static <T, R> TiPageResult<R> of(IPage<T> page, Function<T, R> function) {
        TiPageResult<R> tiPageResult = new TiPageResult<>();
        tiPageResult.setPageNum(Long.valueOf(page.getCurrent()).intValue());
        tiPageResult.setPageSize(Long.valueOf(page.getSize()).intValue());
        tiPageResult.setPages(Long.valueOf(page.getPages()).intValue());
        tiPageResult.setTotal(Long.valueOf(page.getTotal()).intValue());
        tiPageResult.setRows(page.getRecords().stream().map(function).collect(Collectors.toList()));
        return tiPageResult;
    }

    public static <T> TiPageResult<T> ofEmpty(TiPageQuery query) {
        return new TiPageResult<>(query.getPageNum(), query.getPageSize(), 0);
    }

    public static <T> TiPageResult<T> ofEmpty(Number pageNum, Number pageSize) {
        return new TiPageResult<>(pageNum, pageSize, 0);
    }

}
