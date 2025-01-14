package top.ticho.boot.datasource.util;

import com.github.pagehelper.Page;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.ticho.boot.view.core.TiPageResult;

import java.math.BigDecimal;

/**
 * 分页对象转换工具
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TiPageUtil {

    public static <T> TiPageResult<T> getResult(Page<T> page) {
        TiPageResult<T> tiPageResult = new TiPageResult<>();
        tiPageResult.setPageNum(page.getPageNum());
        tiPageResult.setPageSize(page.getPageSize());
        tiPageResult.setPages(page.getPages());
        tiPageResult.setTotal(BigDecimal.valueOf(page.getTotal()).intValue());
        tiPageResult.setRows(page.getResult());
        return tiPageResult;
    }

    public static <T> TiPageResult<T> getResult(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        TiPageResult<T> tiPageResult = new TiPageResult<>();
        tiPageResult.setPageNum((int) page.getCurrent());
        tiPageResult.setPageSize((int) page.getSize());
        tiPageResult.setPages((int) page.getPages());
        tiPageResult.setTotal(BigDecimal.valueOf(page.getTotal()).intValue());
        tiPageResult.setRows(page.getRecords());
        return tiPageResult;

    }
}
