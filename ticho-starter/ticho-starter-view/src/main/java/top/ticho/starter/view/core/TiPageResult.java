package top.ticho.starter.view.core;


import cn.hutool.core.util.NumberUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 分页对象
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Data
@NoArgsConstructor
public class TiPageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 页码，从1开始 */
    private Integer pageNum;
    /** 页面大小 */
    private Integer pageSize;
    /** 总页数 */
    private Integer pages;
    /** 总数 */
    private Integer total;
    /** 查询数据列表 */
    private List<T> rows;

    public TiPageResult(Number pageNum, Number pageSize, Number total) {
        this(pageNum, pageSize, total, null);
    }

    public TiPageResult(Number pageNum, Number pageSize, Number total, List<T> rows) {
        BigDecimal pageSizeDec = NumberUtil.toBigDecimal(pageSize);
        BigDecimal totalDec = NumberUtil.toBigDecimal(total);
        this.pageNum = NumberUtil.toBigDecimal(pageNum).intValue();
        this.pageSize = pageSizeDec.intValue();
        this.total = totalDec.intValue();
        this.pages = pageSizeDec.intValue() <= 0 ? 0 : (int) Math.ceil(totalDec.divide(pageSizeDec, 2, RoundingMode.HALF_UP).doubleValue());
        this.rows = Optional.ofNullable(rows).orElseGet(ArrayList::new);
    }

}
