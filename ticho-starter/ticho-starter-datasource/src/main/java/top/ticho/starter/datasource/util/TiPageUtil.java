package top.ticho.starter.datasource.util;

import com.github.pagehelper.Page;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.ticho.starter.view.core.TiPageResult;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页对象转换工具
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TiPageUtil {

    public static <T> TiPageResult<T> of(Page<T> page) {
        TiPageResult<T> tiPageResult = new TiPageResult<>();
        tiPageResult.setPageNum(page.getPageNum());
        tiPageResult.setPageSize(page.getPageSize());
        tiPageResult.setPages(page.getPages());
        tiPageResult.setTotal(BigDecimal.valueOf(page.getTotal()).intValue());
        tiPageResult.setRows(page.getResult());
        return tiPageResult;
    }

    public static <T, R> TiPageResult<R> of(Page<T> page, Function<T, R> function) {
        TiPageResult<R> tiPageResult = new TiPageResult<>();
        tiPageResult.setPageNum(page.getPageNum());
        tiPageResult.setPageSize(page.getPageSize());
        tiPageResult.setPages(page.getPages());
        tiPageResult.setTotal(BigDecimal.valueOf(page.getTotal()).intValue());
        tiPageResult.setRows(page.getResult().stream().map(function).collect(Collectors.toList()));
        return tiPageResult;
    }

    public static <T> TiPageResult<T> of(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        TiPageResult<T> tiPageResult = new TiPageResult<>();
        tiPageResult.setPageNum(Long.valueOf(page.getCurrent()).intValue());
        tiPageResult.setPageSize(Long.valueOf(page.getSize()).intValue());
        tiPageResult.setPages(Long.valueOf(page.getPages()).intValue());
        tiPageResult.setTotal(BigDecimal.valueOf(page.getTotal()).intValue());
        tiPageResult.setRows(page.getRecords());
        return tiPageResult;
    }

    public static <T, R> TiPageResult<R> of(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page, Function<T, R> function) {
        TiPageResult<R> tiPageResult = new TiPageResult<>();
        tiPageResult.setPageNum(Long.valueOf(page.getCurrent()).intValue());
        tiPageResult.setPageSize(Long.valueOf(page.getSize()).intValue());
        tiPageResult.setPages(Long.valueOf(page.getPages()).intValue());
        tiPageResult.setTotal(BigDecimal.valueOf(page.getTotal()).intValue());
        tiPageResult.setRows(page.getRecords().stream().map(function).collect(Collectors.toList()));
        return tiPageResult;
    }

}
