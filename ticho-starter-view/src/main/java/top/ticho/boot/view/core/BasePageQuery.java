package top.ticho.boot.view.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通用分页查询条件
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Data
@ApiModel("基础分页查询条件")
public class BasePageQuery {

    @ApiModelProperty(value = "当前页码", required = true, example = "1", position = 1)
    private Integer pageNum;

    @ApiModelProperty(value = "页面大小", required = true, example = "10", position = 5)
    private Integer pageSize;

    public void checkPage(int defaultPageSize) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = defaultPageSize;
        }
    }

    public void checkPage() {
        checkPage(10);
    }

}
