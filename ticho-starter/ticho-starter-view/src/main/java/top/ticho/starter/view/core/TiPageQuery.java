package top.ticho.starter.view.core;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通用分页查询条件
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Data
public class TiPageQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 当前页码 */
    private Integer pageNum;
    /** 页面大小 */
    private Integer pageSize;

    public void checkPage(int defaultPageSize) {
        if (Objects.isNull(pageNum) || pageNum <= 0) {
            pageNum = 1;
        }
        if (Objects.isNull(pageSize) || pageSize <= 0) {
            pageSize = defaultPageSize;
        }
    }

    public void checkPage() {
        checkPage(10);
    }

}
