package top.ticho.boot.view.core;


import cn.hutool.core.util.NumberUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@ApiModel("分页对象")
public class PageResult<T> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "页码，从1开始", position = 10)
    private Integer pageNum;

    @ApiModelProperty(value = "页面大小", position = 20)
    private Integer pageSize;

    @ApiModelProperty(value = "总页数", position = 30)
    private Integer pages;

    @ApiModelProperty(value = "总数", position = 40)
    private Integer total;

    @ApiModelProperty(value = "查询数据列表", position = 50)
    private List<T> rows;

    public PageResult(Number pageNum, Number pageSize, Number total, List<T> rows) {
        // @formatter:off
        BigDecimal pageSizeDec = NumberUtil.toBigDecimal(pageSize);
        BigDecimal totalDec = NumberUtil.toBigDecimal(total);
        this.pageNum = NumberUtil.toBigDecimal(pageNum).intValue();
        this.pageSize = pageSizeDec.intValue();
        this.total = totalDec.intValue();
        this.pages = pageSizeDec.intValue() <= 0 ? 0 : (int) Math.ceil(totalDec.divide(pageSizeDec, 2, RoundingMode.HALF_UP).doubleValue());
        this.rows = Optional.ofNullable(rows).orElseGet(ArrayList::new);
        // @formatter:on
    }
}
