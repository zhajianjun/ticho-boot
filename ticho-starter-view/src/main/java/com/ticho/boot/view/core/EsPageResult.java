package com.ticho.boot.view.core;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * es分页对象
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ApiModel("es分页对象")
public class EsPageResult<T> extends PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 索引列表 */
    @ApiModelProperty(value = "索引列表", position = 45)
    private Set<String> indexs;

    public EsPageResult(Number pageNum, Number pageSize, Number total, Collection<String> indexs, List<T> rows) {
        super(pageNum, pageSize, total, rows);
        this.indexs = new HashSet<>(indexs);
    }
}
