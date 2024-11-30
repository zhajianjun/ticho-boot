package top.ticho.boot.datasource.util;

import com.github.pagehelper.Page;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.ticho.boot.view.core.PageResult;

import java.math.BigDecimal;

/**
 * 分页对象转换工具
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageUtil {

    public static <T> PageResult<T> getResult(Page<T> page) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setPageNum(page.getPageNum());
        pageResult.setPageSize(page.getPageSize());
        pageResult.setPages(page.getPages());
        pageResult.setTotal(BigDecimal.valueOf(page.getTotal()).intValue());
        pageResult.setRows(page.getResult());
        return pageResult;
    }

    public static <T> PageResult<T> getResult(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setPageNum((int) page.getCurrent());
        pageResult.setPageSize((int) page.getSize());
        pageResult.setPages((int) page.getPages());
        pageResult.setTotal(BigDecimal.valueOf(page.getTotal()).intValue());
        pageResult.setRows(page.getRecords());
        return pageResult;

    }
}
